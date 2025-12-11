/**
 * @description RuoYi 用户管理模块接口
 */
import { moduleRequest } from "@/api/request";

const http = moduleRequest("/system/user");

/**
 * @Description: RuoYi 用户管理相关API
 */
const userApi = {
  /** 查询用户列表 */
  listUser(params?: any) {
    return http.get<any>("/list", { params });
  },

  /** 获取用户详情 */
  getUser(userId: string | number) {
    return http.get<any>(`/${userId}`);
  },

  /** 新增用户 */
  addUser(params: any) {
    return http.post<any>("", params);
  },

  /** 修改用户 */
  updateUser(params: any) {
    return http.put<any>("", params);
  },

  /** 删除用户 */
  delUser(userIds: string | number | Array<string | number>) {
    return http.delete<any>(`/${userIds}`);
  },

  /** 导出用户 */
  exportUser(params?: any) {
    return http.post<any>("/export", params, { responseType: 'blob' });
  },

  /** 下载用户导入模板 */
  importTemplate() {
    return http.post<any>("/importTemplate", {}, { responseType: 'blob' });
  },

  /** 重置用户密码 */
  resetUserPwd(userId: string | number, password: string) {
    return http.put<any>(`/resetPwd?userId=${userId}&password=${password}`);
  },

  /** 改变用户状态 */
  changeUserStatus(userId: string | number, status: string) {
    return http.put<any>(`/changeStatus`, { userId, status });
  },

  /** 获取部门树 */
  treeselect() {
    return http.get<any>("/treeselect");
  },

  /** 根据角色ID查询部门树结构 */
  deptTreeSelect() {
    return http.get<any>("/deptTree");
  }
};

export { userApi };