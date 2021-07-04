import axios from 'axios';
/*单独引用element-ui中的Message（见element-ui官网，及其用法）*/
import { Message } from 'element-ui';
import router from '../router'

//axios中的响应拦截器，success和error是服务端响应的数据
/*success不是服务端返回的数据，success.data才是服务端返回的json数据*/
/*success.status是http的status，success.data.status是自定义的RespBean中的status*/
axios.interceptors.response.use(success=>{
    if(success.status && success.status==200 && success.data.status==500){
        /*表示将RespBean中的msg显示出来*/
        Message.error({message:success.data.msg})
        return;
    }
    if(success.data.msg){
        Message.success({message:success.data.msg})
    }
    return success.data;
}, error => {
    if (error.response.status == 504 || error.response.status == 404) {
        Message.error({message: '服务器被吃了( ╯□╰ )'})
    } else if (error.response.status == 403) {
        Message.error({message: '权限不足，请联系管理员'})
    } else if (error.response.status == 401) {
        /*mymessage.error({message: error.response.data.msg ? error.response.data.msg : '尚未登录，请登录'})
        router.replace('/');*/
        Message.error({message:"尚未登录，请登录！"});
        router.replace('/');
    } else {
        if (error.response.data.msg) {
            Message.error({message: error.response.data.msg})
        } else {
            /*表示后端服务器关闭，没有response，Could not get any response*/
            Message.error({message: '未知错误!'})
        }
    }
    return;
})
//设置一个全局的变量方便修改
let base = '';

//post请求，登录请求使用key value传参，url请求地址，params请求的参数
//定义完post请求之后，就可以在Login.vue中使用，导入
//登录是只支持key,value传参，不能用json传，spring security默认不支持json传参
export const postKeyValueRequest = (url, params) => {
    return axios({
        method: 'post',
        url: `${base}${url}`,
        data: params,
        //把params传到下面的transformRequest中，变成ret=username=javaboy&password=123
        transformRequest: [function (data) {
            let ret = '';
            for (let i in data) {
                ret += encodeURIComponent(i) + '=' + encodeURIComponent(data[i]) + '&'
            }
            return ret;
        }],
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        }
    });
}
//这个post是传递json的post
export const postRequest = (url, params) => {
    return axios({
        method: 'post',
        url: `${base}${url}`,
        data: params
    })
}
export const putRequest = (url, params) => {
    return axios({
        method: 'put',
        url: `${base}${url}`,
        data: params
    })
}
export const getRequest = (url, params) => {
    return axios({
        method: 'get',
        url: `${base}${url}`,
        params: params
    })
}
export const deleteRequest = (url, params) => {
    return axios({
        method: 'delete',
        url: `${base}${url}`,
        params: params
    })
}
