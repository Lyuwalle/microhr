<template>
    <div>
<!--   布局容器   -->
        <el-container>
          <el-header class="homeHeader">
            <div class="title" >
              <a href="/#/home" style="text-decoration-line: none; color: #9a6759">微人事</a>
            </div>
            <div>
              <!--            @command表示处理command-->
              <el-dropdown class="userInfo" @command="handleCommand">
                  <span class="el-dropdown-link" >
                    <!--展示的是登录用户的名字以及显示登录用户的图片，从window.sessionStorage里面拿-->
                    {{user.name}}<i><img :src="user.userface" alt=""></i>
                  </span>
                <el-dropdown-menu slot="dropdown">
                  <!--                command表示点击事件-->
                  <el-dropdown-item command="userinfo">个人中心</el-dropdown-item>
                  <el-dropdown-item command="setting">设置</el-dropdown-item>
                  <el-dropdown-item command="logout" divided>注销登录</el-dropdown-item>
                </el-dropdown-menu>
              </el-dropdown>
            </div>

          </el-header>

          <el-container>
            <el-aside width="200px">
<!--              侧边栏下拉菜单-->
<!--              @select="menuClick"表示点击菜单时会触发menuClick,menuClick是和index有关联的，可以通过console.log去测试-->
<!--              unique-opened表示点击菜单只打开一个菜单项-->
<!--              router表示使用 vue-router 的模式，启用该模式会在激活导航时以 index 作为 path 进行路由跳转-->
                      <el-menu router unique-opened
                          background-color="#545c64"
                          text-color="#fff"
                          active-text-color="#ffd04b">
<!--                        这个routes指的是下面computed计算属性里面的routes,即routes不从router里面拿，而是从store里面拿-->
                        <el-submenu :index="index+''" v-for="(item, index) in routes" v-if="!item.hidden" :key="index">
                          <template slot="title">
                            <i style="color: chocolate; margin-right: 4px"  :class="item.iconCls"></i>
                            <span>{{item.name}}</span>
                          </template>
<!--这里的index表示要跳转的页面，点击一级菜单之后不需要跳转到响应的页面，只需要把子菜单展开就可以了，而点击二级菜单之后要跳转到相应的页面-->
<!--                          因此二级菜单的index是数据库对应的path-->
                          <el-menu-item :index="child.path + ''" v-for="(child, indexj) in item.children" :key="indexj">

                            <i style="margin-right: 5px; color: burlywood" class="fa fa-coffee" aria-hidden="false"></i>
                            <span>{{child.name}}</span>
                          </el-menu-item>

                        </el-submenu>
                      </el-menu>

            </el-aside>
            <el-main>
              <!--    router-view是一个映射，根据地址栏的url显示不同的组件，映射的关系在route.js里面-->
<!--              跳转后的页面就直接在main里面显示，-->
<!--              面包屑,只有当前页面不是首页的时候面包屑才会展示出来-->
              <el-breadcrumb separator="/" v-if="this.$router.currentRoute.path!='/home'">
                <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
<!--                显示当前路径的名称-->
                <el-breadcrumb-item>{{this.$router.currentRoute.name}}</el-breadcrumb-item>
              </el-breadcrumb>

              <div class="homeWelcome" v-if="this.$router.currentRoute.path=='/home'">
                欢迎来到微人事！
              </div>
              <div class="EmailMe" v-if="this.$router.currentRoute.path=='/home'">
                EmailMe@2538207459@qq.com
              </div>
<!-- github和weibo标签             target="_blank"表示点击图标会跳转到一个新打开的页面-->
              <div class="GitHub" v-if="this.$router.currentRoute.path=='/home'">
                <a href="https://github.com/Lyuwalle" style="color: black" target="_blank"><i class="fa fa-github" aria-hidden="true"></i></a>&nbsp
                <a href="https://m.weibo.cn/profile/5661604657" style="color: #ea1a1a" target="_blank"><i class="fa fa-weibo" aria-hidden="true"></i></a>
              </div>
              <router-view class="homeRouterView"/>
            </el-main>
          </el-container>
        </el-container>
    </div>
</template>

<script>
export default {
  name: "Home",
  data(){
    return{
      //user:JSON.parse(window.sessionStorage.getItem("user"))
      //改为直接使用计算属性，保持数据一致性
    }
  },
  computed:{
    routes(){
      /*拿到store里面存的routes*/
      return this.$store.state.routes;
    },
    user(){
      return this.$store.state.currentHr;
    }
  },
  methods:{
    handleCommand(command){
      if(command == 'logout'){
        this.$confirm('此操作将注销登录, 是否继续?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.getRequest('/logout');
          //清除用户信息并回到登录页面
          window.sessionStorage.removeItem("user");
          /*除了清除sessionStorage里面的信息，还要清空store里面routes的数据，否则下一个用户将会加载上一个用户的菜单，当刷新时，才会调用initMenus*/
          this.$store.commit('initRoutes', [])
          this.$router.replace('/')
        }).catch(() => {
          this.$message({
            type: 'info',
            message: '已取消注销'
          });
        });
      }else if (command == 'userinfo') {
        this.$router.push('/hrinfo');
      }
    },

  }
}
</script>

<style scoped>
.homeRouterView {
  margin-top: 10px;
}
.EmailMe{
  text-align: center;
  font-family: "MV Boli";
  padding-top: 350px;
}
.GitHub{
  font-size: large;
  text-align: center;
  padding-top: 0px;
}
.homeWelcome {
  text-align: center;
  font-size: 60px;
  font-family: 长仿宋体;
  color: #033232;
  padding-top: 80px;
}
.homeHeader {
  background-color: #90b7de;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0px 15px;
  box-sizing: border-box;
}
.homeHeader .title {
  font-size: 30px;
  font-family: 华文行楷;
  color: #ffffff
}
.homeHeader .userInfo {
  /*手指符号*/
  cursor: pointer;
}
/*用户头像的样式*/
.el-dropdown-link img {
  width: 48px;
  height: 48px;
  border-radius: 24px;
  margin-left: 8px;
}

.el-dropdown-link {
  display: flex;
  align-items: center;
}
</style>