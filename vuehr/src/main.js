import Vue from 'vue'
import App from './App.vue'
import router from './router'
import ElementUI from 'element-ui';
import store from './store'
import 'element-ui/lib/theme-chalk/index.css';
import {initMenu} from "./utils/menus";
import 'font-awesome/css/font-awesome.min.css'

/*使用插件,之后再使用这几个方法，直接就可以this.postKeyValueRequest就可以，不需要再引用进来*/
import {postKeyValueRequest} from "./utils/api";
import {postRequest} from "./utils/api";
import {putRequest} from "./utils/api";
import {getRequest} from "./utils/api";
import {deleteRequest} from "./utils/api";
Vue.prototype.postKeyValueRequest = postKeyValueRequest;
Vue.prototype.postRequest = postRequest;
Vue.prototype.putRequest = putRequest;
Vue.prototype.getRequest = getRequest;
Vue.prototype.deleteRequest = deleteRequest;


Vue.config.productionTip = false

Vue.use(ElementUI, {size:'small'});

router.beforeEach((to, from, next) => {
  if(to.path == '/'){
    /*去login页面，直接next()*/
    next();
  }else{
    if(window.sessionStorage.getItem("user")){
      //说明已经登录
      /*去其他页面*/
      //console.log(to)
      initMenu(router, store);
      next();
    }else{
      //没有登录直接登录
      next('/?redirect='+to.path);
    }
  }

})

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
