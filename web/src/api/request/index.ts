/**
 * @description RuoYi Vue API 请求封装
 */
import { ResultEnum } from "@/enums/httpEnum";
import { createRequest } from "./request";

const isHttpProxy = import.meta.env.VITE_HTTP_PROXY === "true"; // 是否使用代理
const url = import.meta.env.VITE_API_URL as string; // 请求地址

/**
 * @description 创建请求实例 - RuoYi API
 * 代理模式使用 /dev-api 前缀
 * @param moduleUrl 模块地址
 */
export const moduleRequest = (moduleUrl: string = "") =>
  createRequest({
    // 代理模式使用 /dev-api 前缀，会被代理到后端
    baseURL: isHttpProxy ? "/dev-api" + moduleUrl : url + moduleUrl,
    // 设置超时时间
    timeout: ResultEnum.TIMEOUT as number,
    // 跨域时候允许携带凭证
    withCredentials: true
  });

/**
 * @description 默认请求实例
 */
export const http = moduleRequest();
