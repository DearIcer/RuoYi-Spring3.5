/**
 * @description RuoYi Vue 认证模块
 */
import { defineStore } from "pinia";
import { getFlatMenuList, getShowMenuList, getAllBreadcrumbList } from "@/utils";
import { Login } from "@/api/interface";
import { loginApi } from "@/api";
import { useUserStore } from "./user";
import { useTabsStore } from "./tabs";
import { useKeepAliveStore } from "./keepAlive";
import { initDynamicRouter } from "@/routers/modules/dynamicRouter";
import { ElNotification } from "element-plus";
import { getTimeState } from "@/utils";
import router from "@/routers";
import { TabsMenuProps } from "../interface";

const name = "ruoyi-auth"; // 定义模块名称

/* AuthState - RuoYi格式 */
export interface AuthState {
  /** 登录的加载状态 */
  loginLoading: boolean;
  /** 按钮权限列表 */
  authButtonList: string[];
  /** 菜单权限列表 */
  authMenuList: Menu.MenuOptions[];
}

/** 认证模块 - RuoYi */
export const useAuthStore = defineStore({
  id: name,
  state: (): AuthState => ({
    loginLoading: false,
    authButtonList: [],
    authMenuList: []
  }),
  getters: {
    // 按钮权限列表
    authButtonListGet: state => state.authButtonList,
    // 菜单权限列表 ==> 这里的菜单没有经过任何处理
    authMenuListGet: state => state.authMenuList,
    // 菜单权限列表 ==> 左侧菜单栏渲染，需要剔除 isHide == true
    showMenuListGet: state => getShowMenuList(state.authMenuList),
    // 菜单权限列表 ==> 扁平化之后的一维数组菜单，主要用来添加动态路由
    flatMenuListGet: state => getFlatMenuList(state.authMenuList),
    // 递归处理后的所有面包屑导航列表
    breadcrumbListGet: state => getAllBreadcrumbList(state.authMenuList)
  },
  actions: {
    /** 获取按钮列表 - 从用户permissions获取 */
    async getAuthButtonList() {
      const userStore = useUserStore();
      this.authButtonList = userStore.permissions || [];
    },

    /** 获取菜单列表 - 优先从后端加载，失败则使用本地静态菜单 */
    async getAuthMenuList() {
      try {
        // 从后端RuoYi API加载菜单
        const res = await loginApi.getRouters();
        if (res.code === 200 && res.data && res.data.length > 0) {
          // 转换RuoYi菜单格式为前端格式
          this.authMenuList = this.transformRuoYiMenus(res.data);
          console.log("[RuoYi] Backend menus loaded:", this.authMenuList);
          return;
        }
      } catch (error) {
        console.warn("[RuoYi] Failed to load backend menus, using static menus", error);
      }
      // 后端加载失败，使用本地静态菜单
      this.authMenuList = this.getStaticMenus();
      console.log("[Local] Static menus loaded:", this.authMenuList);
    },

    /** 转换RuoYi菜单格式为前端所需格式 */
    transformRuoYiMenus(menus: any[], parentPath: string = ""): Menu.MenuOptions[] {
      return menus
        .filter((menu: any) => menu.path) // 过滤无效菜单
        .map((menu: any) => {
          // 处理路径：RuoYi子菜单路径是相对路径，需要拼接父路径
          let fullPath = menu.path || "";
          if (fullPath && !fullPath.startsWith("/") && !fullPath.startsWith("http")) {
            fullPath = parentPath ? `${parentPath}/${menu.path}` : `/${menu.path}`;
          }
          // 确保路径以/开头（对于非外部链接）
          if (fullPath && !fullPath.startsWith("/") && !fullPath.startsWith("http")) {
            fullPath = "/" + fullPath;
          }

          // 处理组件路径：RuoYi的Layout/ParentView不需要加载
          let component = menu.component;
          if (component === "Layout" || component === "ParentView" || component === "InnerLink") {
            component = undefined; // 这些由前端框架处理
          }

          // 处理重定向：noRedirect 表示不重定向
          let redirect = menu.redirect;
          if (redirect === "noRedirect") {
            redirect = undefined;
          }

          const menuItem: Menu.MenuOptions = {
            path: fullPath,
            name: menu.name || this.pathToName(menu.path),
            component: component,
            redirect: redirect,
            isHome: false, // 默认不是首页
            meta: {
              icon: this.mapRuoYiIcon(menu.meta?.icon || ""),
              title: menu.meta?.title || menu.name || "",
              isLink: menu.meta?.link || "",
              isHide: menu.hidden === true,
              isFull: false,
              isAffix: false,
              isKeepAlive: menu.meta?.noCache !== true
            }
          };

          // 特殊处理首页路径
          if (fullPath === "/index" || fullPath === "/home/index") {
            menuItem.isHome = true;
            menuItem.meta.isAffix = true;
          }

          // 处理子菜单
          if (menu.children && menu.children.length > 0) {
            menuItem.children = this.transformRuoYiMenus(menu.children, fullPath);
            // 如果有子菜单且没有重定向，则重定向到第一个子菜单
            if (!menuItem.redirect && menuItem.children && menuItem.children.length > 0) {
              // 查找第一个非隐藏的子菜单
              const visibleChild = menuItem.children.find(c => !c.meta?.isHide);
              if (visibleChild) {
                menuItem.redirect = visibleChild.path;
              } else {
                menuItem.redirect = menuItem.children[0].path;
              }
            }
          }

          return menuItem;
        });
    },

    /** 映射RuoYi图标到Element Plus图标 */
    mapRuoYiIcon(icon: string): string {
      if (!icon || icon === "#") return "";
      // RuoYi使用的图标名称映射到Element Plus图标
      const iconMap: Record<string, string> = {
        system: "ep:setting",
        monitor: "ep:monitor",
        tool: "ep:tools",
        guide: "ep:guide",
        user: "ep:user",
        peoples: "ep:user-filled",
        "tree-table": "ep:grid",
        tree: "ep:connection",
        post: "ep:postcard",
        dict: "ep:notebook",
        edit: "ep:edit",
        message: "ep:message",
        log: "ep:document",
        online: "ep:user",
        job: "ep:timer",
        druid: "ep:odometer",
        server: "ep:cpu",
        redis: "ep:coin",
        "redis-list": "ep:list",
        build: "ep:document-add",
        code: "ep:document-copy",
        swagger: "ep:link",
        form: "ep:document",
        logininfor: "ep:tickets"
      };
      return iconMap[icon] || `ep:${icon}`;
    },

    /** 将路径转换为路由名称 */
    pathToName(path: string): string {
      if (!path) return "";
      return path
        .replace(/^\//, "")
        .split(/[\/\-_]/)
        .map(s => s.charAt(0).toUpperCase() + s.slice(1))
        .join("");
    },

    /** 获取本地静态菜单配置 */
    getStaticMenus(): Menu.MenuOptions[] {
      return [
        // 首页
        {
          path: "/index",
          name: "Home",
          component: "/index",
          isHome: true,
          meta: {
            icon: "ant-design:home-outlined",
            title: "系统首页",
            isLink: "",
            isHide: false,
            isFull: false,
            isAffix: true,
            isKeepAlive: true
          }
        },
        // 系统管理 - RuoYi标准菜单
        {
          path: "/system",
          name: "System",
          redirect: "/system/user",
          meta: {
            icon: "ep:setting",
            title: "系统管理",
            isLink: "",
            isHide: false,
            isFull: false,
            isAffix: false,
            isKeepAlive: true
          },
          children: [
            {
              path: "/system/user",
              name: "SystemUser",
              component: "/system/user/index",
              meta: { icon: "ep:user", title: "用户管理", isLink: "", isHide: false, isFull: false, isAffix: false, isKeepAlive: true }
            },
            {
              path: "/system/role",
              name: "SystemRole",
              component: "/system/role/index",
              meta: { icon: "ep:user-filled", title: "角色管理", isLink: "", isHide: false, isFull: false, isAffix: false, isKeepAlive: true }
            },
            {
              path: "/system/menu",
              name: "SystemMenu",
              component: "/system/menu/index",
              meta: { icon: "ep:menu", title: "菜单管理", isLink: "", isHide: false, isFull: false, isAffix: false, isKeepAlive: true }
            },
            {
              path: "/system/dept",
              name: "SystemDept",
              component: "/system/dept/index",
              meta: { icon: "ep:office-building", title: "部门管理", isLink: "", isHide: false, isFull: false, isAffix: false, isKeepAlive: true }
            },
            {
              path: "/system/post",
              name: "SystemPost",
              component: "/system/post/index",
              meta: { icon: "ep:postcard", title: "岗位管理", isLink: "", isHide: false, isFull: false, isAffix: false, isKeepAlive: true }
            },
            {
              path: "/system/dict",
              name: "SystemDict",
              component: "/system/dict/index",
              meta: { icon: "ep:notebook", title: "字典管理", isLink: "", isHide: false, isFull: false, isAffix: false, isKeepAlive: true }
            },
            {
              path: "/system/config",
              name: "SystemConfig",
              component: "/system/config/index",
              meta: { icon: "ep:setting", title: "参数设置", isLink: "", isHide: false, isFull: false, isAffix: false, isKeepAlive: true }
            },
            {
              path: "/system/notice",
              name: "SystemNotice",
              component: "/system/notice/index",
              meta: { icon: "ep:message", title: "通知公告", isLink: "", isHide: false, isFull: false, isAffix: false, isKeepAlive: true }
            }
          ]
        },
        // 系统监控
        {
          path: "/monitor",
          name: "Monitor",
          redirect: "/monitor/online",
          meta: {
            icon: "ep:monitor",
            title: "系统监控",
            isLink: "",
            isHide: false,
            isFull: false,
            isAffix: false,
            isKeepAlive: true
          },
          children: [
            {
              path: "/monitor/online",
              name: "MonitorOnline",
              component: "/monitor/online/index",
              meta: { icon: "ep:user", title: "在线用户", isLink: "", isHide: false, isFull: false, isAffix: false, isKeepAlive: true }
            },
            {
              path: "/monitor/job",
              name: "MonitorJob",
              component: "/monitor/job/index",
              meta: { icon: "ep:timer", title: "定时任务", isLink: "", isHide: false, isFull: false, isAffix: false, isKeepAlive: true }
            },
            {
              path: "/monitor/druid",
              name: "MonitorDruid",
              component: "/monitor/druid/index",
              meta: { icon: "ep:odometer", title: "数据监控", isLink: "", isHide: false, isFull: false, isAffix: false, isKeepAlive: true }
            },
            {
              path: "/monitor/server",
              name: "MonitorServer",
              component: "/monitor/server/index",
              meta: { icon: "ep:cpu", title: "服务监控", isLink: "", isHide: false, isFull: false, isAffix: false, isKeepAlive: true }
            },
            {
              path: "/monitor/cache",
              name: "MonitorCache",
              component: "/monitor/cache/index",
              meta: { icon: "ep:coin", title: "缓存监控", isLink: "", isHide: false, isFull: false, isAffix: false, isKeepAlive: true }
            },
            {
              path: "/monitor/operlog",
              name: "MonitorOperlog",
              component: "/monitor/operlog/index",
              meta: { icon: "ep:document", title: "操作日志", isLink: "", isHide: false, isFull: false, isAffix: false, isKeepAlive: true }
            },
            {
              path: "/monitor/logininfor",
              name: "MonitorLogininfor",
              component: "/monitor/logininfor/index",
              meta: { icon: "ep:tickets", title: "登录日志", isLink: "", isHide: false, isFull: false, isAffix: false, isKeepAlive: true }
            }
          ]
        },
        // 系统工具
        {
          path: "/tool",
          name: "Tool",
          redirect: "/tool/build",
          meta: {
            icon: "ep:tools",
            title: "系统工具",
            isLink: "",
            isHide: false,
            isFull: false,
            isAffix: false,
            isKeepAlive: true
          },
          children: [
            {
              path: "/tool/build",
              name: "ToolBuild",
              component: "/tool/build/index",
              meta: { icon: "ep:document-add", title: "表单构建", isLink: "", isHide: false, isFull: false, isAffix: false, isKeepAlive: true }
            },
            {
              path: "/tool/gen",
              name: "ToolGen",
              component: "/tool/gen/index",
              meta: { icon: "ep:document-copy", title: "代码生成", isLink: "", isHide: false, isFull: false, isAffix: false, isKeepAlive: true }
            },
            {
              path: "/tool/swagger",
              name: "ToolSwagger",
              component: "/tool/swagger/index",
              meta: { icon: "ep:link", title: "系统接口", isLink: "", isHide: false, isFull: false, isAffix: false, isKeepAlive: true }
            }
          ]
        },
        // 个人中心 (隐藏)
        {
          path: "/userCenter",
          name: "UserCenter",
          component: "/userCenter/index",
          meta: {
            icon: "ep:user",
            title: "个人中心",
            isLink: "",
            isHide: true,
            isFull: false,
            isAffix: false,
            isKeepAlive: true
          }
        }
      ];
    },

    /** 账号密码登录 - RuoYi */
    async loginPwd(model: Login.LoginForm) {
      this.loginLoading = true;
      try {
        const res = await loginApi.login(model);
        if (res.code === 200 && res.token) {
          // RuoYi登录成功，保存token
          const userStore = useUserStore();
          userStore.setToken(res.token);
          // 登录成功后的操作
          await this.handleActionAfterLogin();
        }
      } catch (err) {
        return Promise.reject(err);
      } finally {
        this.loginLoading = false;
      }
    },

    /** 登录成功后的操作 */
    async handleActionAfterLogin() {
      try {
        const path = await initDynamicRouter();
        const tabsStore = useTabsStore();
        const keepAliveStore = useKeepAliveStore();

        // 清空keepalive数据
        keepAliveStore.setKeepAliveName([]);

        // 确保路径存在
        if (!path || path === "/") {
          // 如果没有找到有效路径，使用默认首页
          router.push("/index");
          return;
        }

        const home = this.authMenuList.filter(item => item.path === path);
        if (home.length) {
          const tabsParams: TabsMenuProps[] = [
            {
              icon: home[0].meta.icon as string,
              title: home[0].meta.title as string,
              path: path,
              name: home[0].name as string,
              close: !home[0].meta.isAffix,
              isKeepAlive: home[0].meta.isKeepAlive as boolean
            }
          ];
          tabsStore.setTabs(tabsParams);
        }

        // 跳转到首页
        router.push(path);
        ElNotification({
          title: getTimeState(),
          message: "欢迎回来 RuoYi",
          type: "success",
          duration: 3000
        });
      } catch (err) {
        console.log("[ err ] >", err);
        ElNotification({
          title: "系统错误",
          message: "系统错误,请联系系统管理员！",
          type: "warning",
          duration: 3000
        });
      }
    }
  }
});
