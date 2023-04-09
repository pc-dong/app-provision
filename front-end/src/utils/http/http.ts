import instance from "./axios";

const post = (url: string, data: any) => {
    return new Promise((resolve, reject) => instance.post(url, data)
        .then(res => resolve(res))
        .catch(error => reject(error)));
}

const get = (url: string, params: any) => {
    return new Promise((resolve, reject) => instance.get(url, {params})
        .then(res => resolve(res))
        .catch(error => reject(error)));
}

const put = (url: string, data: any) => {
    return new Promise((resolve, reject) => instance.put(url, data)
        .then(res => resolve(res))
        .catch(error => reject(error)));
}

const del = (url: string, params: any) => {
    return new Promise((resolve, reject) => instance.delete(url, {params})
        .then(res => resolve(res))
        .catch(error => reject(error)));
}

export default {post, get, put, del}