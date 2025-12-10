/**
 * @description RuoYi Vue API 接口定义
 */

// 请求响应参数（不包含data）- RuoYi格式
export interface Result {
  code: number;
  msg: string;
}

// 请求响应参数（包含data）- RuoYi格式
export interface ResultData<T = any> extends Result {
  data?: T;
  // RuoYi有时候直接返回额外字段
  [key: string]: any;
}

// 分页响应参数 - RuoYi TableDataInfo格式
export interface ResPage<T> {
  rows: T[];
  total: number;
  code: number;
  msg: string;
}

// 分页请求参数
export interface ReqPage {
  /** 页码 */
  pageNum: number;
  /** 数量 */
  pageSize: number;
  /** 排序字段 */
  orderByColumn?: string;
  /** 排序方式 asc/desc */
  isAsc?: string;
}

/** id请求参数 */
export interface ReqId {
  /** id */
  id: number | string;
}

export * from "./sys";
export * from "./biz";
