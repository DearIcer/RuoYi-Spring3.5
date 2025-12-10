/**
 * @description RuoYi 登录模块接口
 */
import { Login } from "@/api/interface";
import { http } from "@/api/request";

/**
 * @Description: RuoYi 登录相关API
 */
const loginApi = {
  /** 用户登录 - RuoYi: POST /login */
  login(params: Login.LoginForm) {
    return http.post<Login.LoginResult>("/login", params, { loading: false });
  },

  /** 获取验证码 - RuoYi: GET /captchaImage */
  getCaptcha() {
    return http.get<Login.CaptchaResult>("/captchaImage", {}, { loading: false });
  },

  /** 获取用户信息 - RuoYi: GET /getInfo */
  getInfo() {
    return http.get<Login.UserInfoResult>("/getInfo", {}, { loading: false });
  },

  /** 获取路由菜单 - RuoYi: GET /getRouters */
  getRouters() {
    return http.get<any>("/getRouters", {}, { loading: false });
  },

  /** 用户退出登录 - RuoYi: POST /logout */
  logout() {
    return http.post("/logout", {});
  },

  // 兼容旧版本的接口名称
  /** @deprecated 使用 getCaptcha 代替 */
  picCaptcha() {
    return this.getCaptcha();
  },

  /** @deprecated 使用 getInfo 代替 */
  getLoginUser() {
    return this.getInfo();
  }
};

export { loginApi };
