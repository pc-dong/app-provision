import axios, {AxiosInstance} from "axios";
import {ElMessage} from "element-plus";

const instance:AxiosInstance = axios.create({baseURL: `${process.env.BASE_URL || '/api'}`, timeout: 30000});
export const ERROR_MESSAGE = {
    '400': '参数错误，请检查输入错误',
    '401': '未授权，请登录',
    '403': '拒绝访问',
    '404': '请求地址出错',
    '408': '请求超时',
    '500': '服务器内部错误',
    '501': '服务未实现',
    '502': '网关错误',
    '503': '服务不可用',
    '504': '网关超时',
    '505': 'HTTP版本不受支持',
}
export const TIMEOUT_ERROR_MESSAGE = '服务器响应超时，请刷新重试';
instance.interceptors.response.use(res => res,
    err => {
        if (err && err.response) {
            const status = ('' + err.response.status) as string;
            ElMessage.error(ERROR_MESSAGE[status as keyof typeof ERROR_MESSAGE] || `连接错误${status}`);
        } else {
            if (JSON.stringify(err).includes('timeout')) {
                ElMessage.error(TIMEOUT_ERROR_MESSAGE);
            } else {
                ElMessage.error("服务器异常，请联系管理员");
            }
        }
        return Promise.reject(err);
    });

export default instance;