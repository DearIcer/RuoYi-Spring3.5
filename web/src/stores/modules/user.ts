/**
 * @description RuoYi Vue 用户模块
 */
import { defineStore } from "pinia";
import { Login } from "@/api/interface";
import piniaPersistConfig from "@/stores/helper/persist";
import { loginApi } from "@/api";
import { ElNotification } from "element-plus";

const name = "ruoyi-user"; // 定义模块名称

/* UserState - RuoYi格式 */
export interface UserState {
  /** token */
  accessToken: string;
  /** 用户信息 */
  userInfo: Login.UserInfo | null;
  /** 角色集合 */
  roles: string[];
  /** 权限集合 */
  permissions: string[];
}

/** 用户模块 - RuoYi */
export const useUserStore = defineStore({
  id: name,
  state: (): UserState => ({
    accessToken: "",
    userInfo: null,
    roles: [],
    permissions: []
  }),
  getters: {
    userInfoGet: state => state.userInfo,
    rolesGet: state => state.roles,
    permissionsGet: state => state.permissions
  },
  actions: {
    // Set Token
    setToken(token: string) {
      this.accessToken = token;
    },

    // 获取用户信息 - RuoYi格式
    async getUserInfo() {
      try {
        const res = await loginApi.getInfo();
        if (res.code === 200) {
          // RuoYi返回格式: { user, roles, permissions }
          this.setUserInfo(res.user);
          this.roles = res.roles || [];
          this.permissions = res.permissions || [];
          return this.userInfo;
        } else {
          throw new Error(res.msg || "获取用户信息失败");
        }
      } catch (error) {
        ElNotification({
          title: "系统错误",
          message: "获取个人信息失败，请联系系统管理员！",
          type: "warning",
          duration: 3000
        });
        throw error;
      }
    },

    /** 设置用户信息 */
    setUserInfo(userInfo: Login.UserInfo) {
      this.userInfo = userInfo;
    },

    /** 设置用户单个属性 */
    setUserInfoItem(key: string, value: any) {
      if (this.userInfo) {
        (this.userInfo as any)[key] = value;
      }
    },

    /** 清除token */
    clearToken() {
      this.accessToken = "";
    },

    /** 清理用户信息 */
    clearUserStore() {
      this.clearToken();
      this.userInfo = null;
      this.roles = [];
      this.permissions = [];
    }
  },
  persist: piniaPersistConfig(name)
});
