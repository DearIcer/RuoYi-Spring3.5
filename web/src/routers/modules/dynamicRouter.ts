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
    let homePath: string = HOME_URL; // 首页路径

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

    // 5. 查找首页
    const homeMenu = authStore.authMenuListGet.find(item => item.isHome === true);
    if (homeMenu) {
      homePath = homeMenu.path;
    } else {
      // 如果不存在首页，设置第一个菜单为首页
      const firstMenu = authStore.flatMenuListGet[0];
      if (firstMenu) {
        homePath = firstMenu.path;
      }
    }

    // 6. 添加动态路由
    authStore.flatMenuListGet.forEach(item => {
      item.children && delete item.children;
      if (item.component && typeof item.component == "string") {
        // RuoYi组件路径格式: "system/user/index" -> "/src/views/system/user/index.vue"
        const componentPath = "/src/views/" + item.component + ".vue";
        item.component = modules[componentPath];
        if (!item.component) {
          console.warn(`Component not found: ${componentPath}`);
        }
      }
      // 只添加有组件的路由
      if (item.component) {
        if (item.meta.isFull) {
          router.addRoute(item as unknown as RouteRecordRaw);
        } else {
          router.addRoute("layout", item as unknown as RouteRecordRaw);
        }
      }
    });

    return Promise.resolve(homePath);
  } catch (error) {
    // 当按钮 || 菜单请求出错时，重定向到登陆页
    userStore.clearToken();
    router.replace(LOGIN_URL);
    return Promise.reject(error);
  }
};
