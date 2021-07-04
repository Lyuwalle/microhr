/*这是一个工具类，第一个功能是把后端传来的json的Menu的List数组加到router.js里面（格式化之后的routes数组加到router.js里面），其中component字符串要转化成一个组件，在后面加上.vue*/
/*第二个功能就是把格式化之后的routes数组放到store里面，通过store.commit('initRoutes', fmtRoutes)来实现*/
import {getRequest} from "./api";
/*从服务端加载好的routes向router里面加，然后再保存在在store里面*/
export const initMenu = (router, store) =>{
    if(store.state.routes.length > 0){
        /*说明里面有菜单数据，不用加载*/
        return;
    }
    /*如果用户按了F5刷新页面，此时的store里面的routes数组的长度就是0，要执行下面的方法*/
    /*data是请求回来的数据，从postman可以知道是一个Menu数组*/
    getRequest('/system/config/menu').then(data=>{
        if(data){
            let fmtRoutes = formatRoutes(data);
            /*把格式化之后的fmtRoutes加入到router里面*/
            router.addRoutes(fmtRoutes);
            /*在store里面去调用initRoutes方法，参数就是fmtRoutes*/
            store.commit('initRoutes', fmtRoutes)
        }
    })
}

export const formatRoutes = (routes) => {
    let fmRoutes = [];
    routes.forEach(router =>{
        let{
            path,
            component,
            name,
            meta,
            iconCls,
            children
        } = router;
        /*如果children instanceof Array，说明这个children是一级菜单的children，那么就递归去调用*/
        if(children && children instanceof Array){
            children = formatRoutes(children)
        }
        let fmRouter = {
            path:path,
            name:name,
            iconCls:iconCls,
            meta:meta,
            children:children,
            component(resolve){
                /*把后端传来的component字符串变成一个组件*/
                /*根据后端传来的组件的前缀不同，去不同的目录下加载不同的组件*/
                if(component.startsWith("Emp")){
                    require(['../views/emp/'+component+'.vue'], resolve);
                }else if(component.startsWith("Per")){
                    require(['../views/per/'+component+'.vue'], resolve);
                }else if(component.startsWith("Sal")){
                    require(['../views/sal/'+component+'.vue'], resolve);
                }else if(component.startsWith("Sys")){
                    require(['../views/sys/'+component+'.vue'], resolve);
                }else if(component.startsWith("Sta")){
                    require(['../views/sta/'+component+'.vue'], resolve);
                }else{
                    require(['../views/'+component+'.vue'], resolve);
                }
            }
        }
        fmRoutes.push(fmRouter);
    })
    return fmRoutes;
}