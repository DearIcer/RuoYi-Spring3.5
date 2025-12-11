/**
 * @description RuoYi Vue 动态路由
 */
import { LOGIN_URL, HOME_URL } from "@/config";
import { RouteRecordRaw } from "vue-router";
import { ElNotification } from "element-plus";
import { useUserStore, useAuthStore } from "@/stores/modules";
import router from "@/routers";

// 引入 views 文件夹下所有 vue 文件
const modules = import.meta.glob("@/views/**/*.vue");

/**
 * @description 初始化动态路由 - RuoYi
 */
export const initDynamicRouter = async () => {
  const userStore = useUserStore();
  const authStore = useAuthStore();

  /** 路由初始化错误 */
  const routerError = (title: string = "无权限访问", message: string = "当前账号无任何菜单权限，请联系系统管理员！") => {
    ElNotification({
      title: title,
      message: message,
      type: "warning",
      duration: 3000
    });
    userStore.clearToken();
    router.replace(LOGIN_URL);
    return Promise.reject("No permission");
  };

  try {
    // 1. 获取用户信息 - RuoYi: /getInfo
    await userStore.getUserInfo();

    // 2. 获取菜单列表 - RuoYi: /getRouters
    await authStore.getAuthMenuList();

    // 3. 获取按钮权限列表
    await authStore.getAuthButtonList();

    // 4. 判断当前用户有没有菜单权限
    if (!authStore.authMenuListGet.length) {
      return routerError();
    }

    // 5. 添加动态路由，并找到第一个有效的页面作为首页
    console.log("[RuoYi] Adding dynamic routes, total:", authStore.flatMenuListGet.length);
    let firstValidPath: string | null = null;

    authStore.flatMenuListGet.forEach(item => {
      item.children && delete item.children;
      if (item.component && typeof item.component == "string") {
        // RuoYi组件路径格式: "system/user/index" -> "/src/views/system/user/index.vue"
        // 处理以/开头的绝对路径
        let componentPath;
        if (item.component && item.component.startsWith("/")) {
          componentPath = "/src/views" + item.component + ".vue";
        } else if (item.component) {
          componentPath = "/src/views/" + item.component + ".vue";
        } else {
          // 如果没有组件路径，跳过组件加载
          item.component = undefined;
        }

        if (item.component && modules[componentPath]) {
          item.component = modules[componentPath];
        } else if (item.component) {
          // 尝试不带index后缀的路径
          let altPath;
          if (item.component.startsWith("/")) {
            altPath = "/src/views" + item.component.replace(/\/index$/, "") + ".vue";
          } else {
            altPath = "/src/views/" + item.component.replace(/\/index$/, "") + ".vue";
          }
          if (modules[altPath]) {
            item.component = modules[altPath];
          } else {
            console.warn(`[RuoYi] Component not found: ${componentPath}`);
            console.warn(`[RuoYi] Available modules:`, Object.keys(modules).slice(0, 10), "...");
            item.component = undefined; // 清除无效组件路径
          }
        }
      }
      // 添加路由（无论是否有组件）
      console.log(`[RuoYi] Adding route: ${item.path}`);
      // 记录第一个有效的路由作为首页
      if (!firstValidPath && !item.meta.isHide && item.component) {
        firstValidPath = item.path;
      }
      if (item.meta.isFull) {
        router.addRoute(item as unknown as RouteRecordRaw);
      } else {
        router.addRoute("layout", item as unknown as RouteRecordRaw);
      }
    });

    // 6. 确定首页路径
    let homePath: string = HOME_URL;
    const homeMenu = authStore.authMenuListGet.find(item => item.isHome === true);
    if (homeMenu) {
      homePath = homeMenu.path;
    } else if (firstValidPath) {
      // 使用第一个有效组件的路由作为首页
      homePath = firstValidPath;
      console.log(`[RuoYi] No home page defined, using first valid route: ${homePath}`);
    }

    return Promise.resolve(homePath);
  } catch (error) {
    // 当按钮 || 菜单请求出错时，重定向到登陆页
    userStore.clearToken();
    router.replace(LOGIN_URL);
    return Promise.reject(error);
  }
};
