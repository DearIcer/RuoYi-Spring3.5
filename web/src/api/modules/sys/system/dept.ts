/**
 * @description RuoYi 部门管理模块接口
 */
import { moduleRequest } from "@/api/request";

const http = moduleRequest("/system/dept");

/**
 * @Description: RuoYi 部门管理相关API
 */
const deptApi = {
  /** 查询部门列表 */
  listDept(params?: any) {
    return http.get<any>("/list", { params });
  },

  /** 查询部门详细 */
  getDept(deptId: string | number) {
    return http.get<any>(`/${deptId}`);
  },

  /** 新增部门 */
  addDept(params: any) {
    return http.post<any>("", params);
  },

  /** 修改部门 */
  updateDept(params: any) {
    return http.put<any>("", params);
  },

  /** 删除部门 */
  delDept(deptId: string | number) {
    return http.delete<any>(`/${deptId}`);
  },

  /** 查询部门下拉树结构 */
  treeselect() {
    return http.get<any>("/treeselect");
  },

  /** 加载对应角色部门列表树 */
  roleDeptTreeselect(roleId: string | number) {
    return http.get<any>(`/roleDeptTreeselect/${roleId}`);
  }
};

export { deptApi };