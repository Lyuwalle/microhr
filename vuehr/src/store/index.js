/*这是一个vuex*/
import Vue from 'vue'
import Vuex from 'vuex'
import { Notification } from 'element-ui';
import {getRequest} from "../utils/api";

Vue.use(Vuex)

const now = new Date();

const store = new Vuex.Store({
    state:{
        /*从服务端加载下来的菜单项都放在这个routes数组里面*/
        routes:[],
        sessions:{},
        hrs: [],
        currentSession: null,
        currentHr: JSON.parse(window.sessionStorage.getItem("user")),
        filterKey: '',
        stomp: null,
        isDot: {}
    },
    /*更改 Vuex 的 store 中的状态的唯一方法是提交 mutation*/
    mutations:{
        INIT_CURRENTHR(state, hr) {
            state.currentHr = hr;
        },
        /**/
        initRoutes(state, data) {
            state.routes = data;
        },

        INIT_DATA(state) {
            //浏览器本地的历史聊天记录可以在这里完成
            let data = localStorage.getItem('vue-chat-session');
            if (data) {
                state.sessions = JSON.parse(data);
            }
        },
        INIT_HR(state, data) {
            state.hrs = data;
        }
    },
    actions:{
        initData(context) {
            context.commit('INIT_DATA')
            getRequest("/chat/hrs").then(resp => {
                if (resp) {
                    context.commit('INIT_HR', resp);
                }
            })
        }
    }
})
export default store;