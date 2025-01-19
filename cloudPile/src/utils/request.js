import axios from 'axios';
import {ElMessage} from 'element-plus';
import {CookieManager} from "@/utils/cookies.js";
import {loading} from "@/utils/loading.js";

const service = axios.create({
    // baseURL: "http://8.130.33.212:8080", // api的base_url
    baseURL: "http://localhost:8080", // api的base_url
    // baseURL: "http://192.168.10.33:8080", // api的base_url
    // baseURL: "http://45.144.137.247:24641/", // api的base_url
    timeout: 5000 // 请求超时时间
});

// 请求拦截器
service.interceptors.request.use(
    config => {
        // 添加Content-Type为application/json
        config.headers['Content-Type'] = 'application/json';
        // 如果请求路径匹配/user/**，则添加Authorization头
        if (!config.url.includes('/user/')) {
            const token = CookieManager.getCookie('token');
            if (token) {
                config.headers['Authorization'] = token;
            }
        }
        return config;
    },
    error => {
        // 处理请求错误
        return Promise.reject(error);
    }
);

// 响应拦截器
service.interceptors.response.use(
    response => {
        // 处理响应数据
        const res = response.data;
        if (res.code !== 200) {
            // 如果状态码不是200，则使用ElMessage显示错误信息
            ElMessage.error(res.message);
            return Promise.reject(new Error(res.message));
        } else {
            return res;
        }
    },
    error => {
        // 处理响应错误
        ElMessage.error(error.message);
        return Promise.reject(error);
    }
);

// 封装请求方法
export async function request(method, url, data = null) {
    try {
        loading.value = true;
        let response;
        if (method === 'get') {
            response = await service.get(url);
        } else if (method === 'post') {
            response = await service.post(url, data);
        } else if (method === 'put') {
            response = await service.put(url, data);
        } else if (method === 'delete') {
            response = await service.delete(url);
        } else if (method === 'options') {
            response = await service.options(url);
        }
        // else {
        //     throw new Error('Unsupported request method');
        // }
        return response;
    } catch (error) {
        throw error;
    } finally {
        loading.value = false;
    }
}