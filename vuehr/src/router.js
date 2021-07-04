import Vue from 'vue'
import VueRouter from 'vue-router'
import Login from './views/Login.vue'
import Home from './views/Home'
import HrInfo from './views/HrInfo.vue'

Vue.use(VueRouter)

const routes = [
    /*当地址栏为/的时候就会提示用户登录*/
  {
    path: '/',
    name: 'Login',
    component: Login,
    /*表示通过this.$router.options.routes拿到routes数组之后不包含这个当前路径的值*/
    hidden:true
  },
  {
    path: '/home',
    name: 'Home',
    component: Home,
    hidden:true,
    meta: {
      roles: ['admin', 'user']
    },
    children:[
      {
        path: '/hrinfo',
        name: '个人中心',
        component: HrInfo,
        hidden: true
      }
    ]
  },
  {
    path: '*',
    redirect: '/home'
  }

]

const router = new VueRouter({
  routes
})

export default router
