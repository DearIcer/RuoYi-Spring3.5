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

    /** 获取菜单列表 - RuoYi /getRouters */
    async getAuthMenuList() {
      const res = await loginApi.getRouters();
      if (res.code === 200 && res.data) {
        // 转换RuoYi菜单格式为前端格式
        this.authMenuList = this.transformRuoYiMenus(res.data);
      }
    },

    /** 转换RuoYi菜单格式为前端所需格式 */
    transformRuoYiMenus(menus: any[], parentPath: string = ""): Menu.MenuOptions[] {
      return menus.map((menu: any) => {
        // 处理路径：RuoYi子菜单路径是相对路径，需要拼接父路径
        let fullPath = menu.path;
        if (!fullPath.startsWith("/") && !fullPath.startsWith("http")) {
          fullPath = parentPath ? `${parentPath}/${menu.path}` : `/${menu.path}`;
        }

        // 处理组件路径：RuoYi的Layout不需要加载，只加载实际页面组件
        let component = menu.component;
        if (component === "Layout" || component === "ParentView") {
          component = undefined; // Layout由前端框架处理
        }

        const menuItem: Menu.MenuOptions = {
          path: fullPath,
          name: menu.name || menu.path,
          component: component,
          redirect: menu.redirect === "noRedirect" ? undefined : menu.redirect,
          isHome: false,
          meta: {
            icon: menu.meta?.icon || "",
            title: menu.meta?.title || menu.name || "",
            isLink: menu.meta?.link || "",
            isHide: menu.hidden || false,
            isFull: false,
            isAffix: false,
            isKeepAlive: !menu.meta?.noCache
          }
        };

        // 处理子菜单，传递当前路径作为父路径
        if (menu.children && menu.children.length > 0) {
          menuItem.children = this.transformRuoYiMenus(menu.children, fullPath);
          // 设置重定向到第一个子菜单
          if (!menuItem.redirect && menuItem.children && menuItem.children.length > 0) {
            menuItem.redirect = menuItem.children[0].path;
          }
        }

        return menuItem;
      });
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
