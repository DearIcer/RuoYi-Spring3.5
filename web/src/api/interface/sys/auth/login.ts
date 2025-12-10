/**
 * @description RuoYi 登录模块接口定义
 */

/**
 * @Description: RuoYi 登录相关接口
 */
export namespace Login {
  /**
   * 账号密码登录表单 - RuoYi格式
   */
  export interface LoginForm {
    /** 用户名 */
    username: string;
    /** 密码 */
    password: string;
    /** 验证码 */
    code?: string;
    /** 验证码UUID */
    uuid?: string;
  }

  /**
   * 登录返回 - RuoYi格式
   */
  export interface LoginResult {
    /** token */
    token: string;
  }

  /**
   * 验证码返回 - RuoYi格式
   */
  export interface CaptchaResult {
    /** 是否开启验证码 */
    captchaEnabled: boolean;
    /** 验证码UUID */
    uuid?: string;
    /** 验证码图片base64 */
    img?: string;
  }

  /** 用户信息 - RuoYi格式 */
  export interface UserInfo {
    /** 用户id */
    userId: number;
    /** 用户名 */
    userName: string;
    /** 用户昵称 */
    nickName: string;
    /** 用户头像 */
    avatar: string;
    /** 用户性别 */
    sex: string;
    /** 电话号码 */
    phonenumber: string;
    /** 邮箱 */
    email: string;
    /** 部门 */
    dept?: {
      deptId: number;
      deptName: string;
    };
    /** 密码更新日期 */
    pwdUpdateDate?: string;
  }

  /** 获取用户信息返回 - RuoYi格式 */
  export interface UserInfoResult {
    /** 用户信息 */
    user: UserInfo;
    /** 角色集合 */
    roles: string[];
    /** 权限集合 */
    permissions: string[];
    /** 是否需要修改初始密码 */
    isDefaultModifyPwd?: boolean;
    /** 密码是否过期 */
    isPasswordExpired?: boolean;
  }

  /**
   * 注销表单
   */
  export interface Logout {
    // RuoYi注销不需要参数
  }

  // 兼容旧版本 - LoginUserInfo别名
  export type LoginUserInfo = UserInfo & {
    /** 按钮码集合 - 对应permissions */
    buttonCodeList: string[];
    /** 权限码集合 */
    permissionCodeList: string[];
    /** 角色码集合 */
    roleCodeList: string[];
  };
}
