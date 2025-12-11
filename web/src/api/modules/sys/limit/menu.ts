/**
 * @description RuoYi 菜单管理模块接口
 */
import { moduleRequest } from "@/api/request";

const http = moduleRequest("/system/menu");

/**
 * @Description: RuoYi 菜单管理相关API
 */
const menuApi = {
  /** 查询菜单列表 */
  listMenu(params?: any) {
    return http.get<any>("/list", params);
  },

  /** 查询菜单详细 */
  getMenu(menuId: string | number) {
    return http.get<any>(`/${menuId}`);
  },

  /** 查询菜单下拉树结构 */
  treeselect() {
    return http.get<any>("/treeselect");
  },

  /** 根据角色ID查询菜单下拉树结构 */
  roleMenuTreeselect(roleId: string | number) {
    return http.get<any>(`/roleMenuTreeselect/${roleId}`);
  },

  /** 新增菜单 */
  addMenu(params: any) {
    return http.post<any>("", params);
  },

  /** 修改菜单 */
  updateMenu(params: any) {
    return http.put<any>("", params);
  },

  /** 删除菜单 */
  delMenu(menuId: string | number) {
    return http.delete<any>(`/${menuId}`);
  }
};

export { menuApi };
