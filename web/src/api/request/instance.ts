/**
 * @description 封装 axios 请求类 - RuoYi Vue
 */
import axios, { AxiosInstance, AxiosError, AxiosRequestConfig, InternalAxiosRequestConfig, AxiosResponse } from "axios";
import { showFullScreenLoading, tryHideFullScreenLoading } from "@/components/Loading/fullScreen";
import { LOGIN_URL } from "@/config";
import { ElMessage } from "element-plus";
import { ResultData } from "@/api/interface";
import { ResultEnum, TokenEnum } from "@/enums";
import { checkStatus } from "../helper/checkStatus";
import { useUserStore } from "@/stores/modules";
import { AxiosCanceler } from "../helper/axiosCancel";
import router from "@/routers";

// 自定义 AxiosRequestConfig 接口，增加 noLoading 属性
export interface CustomAxiosRequestConfig extends InternalAxiosRequestConfig {
  loading?: boolean;
  cancel?: boolean;
}
const axiosCanceler = new AxiosCanceler();

/**
 * @Description: RuoYi Vue http请求
 */
export default class RequestHttp {
  service: AxiosInstance;
  /**
   * @description 构造函数
   * @param config axios 配置
   */
  public constructor(config: AxiosRequestConfig) {
    // axios 实例化
    this.service = axios.create(config);
    // 设置请求拦截器
    this.setInterceptor();
  }
  /**  指定方法提示 */
  apiNameArray = ["add", "edit", "grant", "batch", "update", "delete", "remove", "reset"];
  /**  指定方法不提示 */
  noMessageApiNameArray: string[] = [];
  /**
   * @description 设置请求拦截器
   */
  setInterceptor() {
    /**
     * @description 请求拦截器
     * 客户端发送请求 -> [请求拦截器] -> 服务器
     * RuoYi 使用 JWT Token 认证
     */
    this.service.interceptors.request.use(
      (config: CustomAxiosRequestConfig) => {
        const userStore = useUserStore();
        // 重复请求不需要取消，在 api 服务中通过指定的第三个参数: { cancel: false } 来控制
        config.cancel ??= true;
        config.cancel && axiosCanceler.addPending(config);
        // 当前请求不需要显示 loading，在 api 服务中通过指定的第三个参数: { loading: false } 来控制
        config.loading ??= true;
        config.loading && showFullScreenLoading();
        // RuoYi: 在请求头中添加 Authorization: Bearer token
        if (config.headers && typeof config.headers.set === "function") {
          const { accessToken } = userStore;
          if (accessToken) {
            config.headers.set(TokenEnum.TOKEN_NAME, TokenEnum.TOKEN_PREFIX + accessToken);
          }
        }
        // get请求加时间戳
        if (config.method === "get") {
          config.params = {
            ...config.params,
            _t: new Date().getTime()
          };
        }
        return config;
      },
      (error: AxiosError) => {
        return Promise.reject(error);
      }
    );

    /**
     * @description 响应拦截器
     *  服务器换返回信息 -> [拦截统一处理] -> 客户端JS获取到信息
     * RuoYi 响应格式: { code: number, msg: string, data?: any }
     */
    this.service.interceptors.response.use(
      (response: AxiosResponse & { config: CustomAxiosRequestConfig }) => {
        const { data, config, headers } = response;
        const userStore = useUserStore();
        axiosCanceler.removePending(config);
        config.loading && tryHideFullScreenLoading();

        // RuoYi: 登录失效 (401)
        if (data.code == ResultEnum.OVERDUE) {
          userStore.clearUserStore();
          router.replace(LOGIN_URL);
          ElMessage.error(data.msg || "登录已过期，请重新登录");
          return Promise.reject(data);
        }

        // RuoYi: 全局错误信息拦截（防止下载文件的时候返回数据流，没有 code 直接报错）
        if (data.code !== undefined && data.code !== ResultEnum.SUCCESS) {
          ElMessage.error(data.msg || "操作失败");
          return Promise.reject(data);
        } else {
          // 统一成功提示
          const responseUrl: string = response.config.url || "";
          this.apiNameArray.forEach(apiName => {
            let responseApiArray = responseUrl.split("/");
            let method = responseApiArray[responseApiArray.length - 1];
            let result = this.noMessageApiNameArray.includes(method);
            if (!result && responseUrl.includes(apiName)) {
              ElMessage.success(data.msg || "操作成功");
            }
          });
        }
        if (config.responseType === "blob") {
          return { data, headers };
        }
        // 成功请求
        return data;
      },
      async (error: AxiosError) => {
        const { response } = error;
        tryHideFullScreenLoading();
        // 请求超时 && 网络错误单独判断，没有 response
        if (error.message.indexOf("timeout") !== -1) ElMessage.error("请求超时！请您稍后重试");
        if (error.message.indexOf("Network Error") !== -1) ElMessage.error("网络错误！请您稍后重试");
        // 根据服务器响应的错误状态码，做不同的处理
        if (response) checkStatus(response.status);
        // 服务器结果都没有返回，断网处理
        if (!window.navigator.onLine) router.replace("/500");
        return Promise.reject(error);
      }
    );
  }

  /**
   * @description get 请求
   */
  get<T>(url: string, params?: object, _object = {}): Promise<ResultData<T>> {
    return this.service.get(url, { params, ..._object });
  }

  /**
   * @description post 请求
   */
  post<T>(url: string, params?: object | string, _object = {}): Promise<ResultData<T>> {
    return this.service.post(url, params, _object);
  }

  /**
   * @description put 请求
   */
  put<T>(url: string, params?: object, _object = {}): Promise<ResultData<T>> {
    return this.service.put(url, params, _object);
  }

  /**
   * @description delete 请求
   */
  delete<T>(url: string, params?: any, _object = {}): Promise<ResultData<T>> {
    return this.service.delete(url, { params, ..._object });
  }

  /**
   * @description 下载文件
   */
  download(url: string, params?: object, _object = {}): Promise<BlobPart> {
    return this.service.post(url, params, { ..._object, responseType: "blob" });
  }
}
