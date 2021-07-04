<template>
<!--  loginForm是form表单中的数据对象-->
<!-- ref="loginForm"表示通过loginForm可以去查找到表单的名字 -->
  <el-form
      v-loading="loading"
      element-loading-text="拼命登录中..."
      element-loading-spinner="el-icon-loading"
      element-loading-background="rgba(0, 0, 0, 0.8)"
      :rules="rules"
      ref="loginForm"
      :model="loginForm"
      class="loginContainer">
    <h3 class="loginTitle">系统登录</h3>
    <el-form-item prop="username">
      <el-input type="text" v-model="loginForm.username" auto-complete="off" placeholder="请输入用户名"></el-input>
    </el-form-item>
    <el-form-item prop="password">
      <el-input type="text" v-model="loginForm.password" auto-complete="off" placeholder="请输入密码"></el-input>
    </el-form-item>
<!--    验证码-->
    <el-form-item prop="code">
      <el-input type="text" v-model="loginForm.code" auto-complete="off" placeholder="点击图片更换验证码"
                @keydown.enter.native="submitLogin" style="width: 250px"></el-input>
<!--      图片-->
      <img :src="verifyCodeUrl" @click="updateVerifyCode" alt="">
    </el-form-item>
    <el-checkbox class="loginRemember" v-model="checked">记住我</el-checkbox>
<!--    点击登录会执行这个方法submitLogin，校验用户名和密码-->
    <el-button type="primary" style="width:100%" @click="submitLogin">登录</el-button>
  </el-form>
</template>

<script>

export default {
  name: "Login",
  data(){
    /*给verifyCodeUrl加一个时间戳，这样每次生成的地址都是不一样的*/
    return {
      verifyCodeUrl:'/verifyCode?time='+new Date(),
      loading:false,
      loginForm:{
        username:'admin',
        password:'123',
        code:''
      },
      checked: true,
      rules:{
        username:[{required:true, message:'请输入用户名', trigger:'blur'}],
        password:[{required:true, message:'密码不能为空', trigger:'blur'}],
        code:[{required:true, message:'请输入验证码', trigger:'blur'}]
      }
    }
  },
  methods:{
    updateVerifyCode(){
      this.verifyCodeUrl='/verifyCode?time='+new Date()
    },
    submitLogin(){

      /*this.$refs表示会获取到当前页面的所有的refs,*/
      this.$refs.loginForm.validate((valid) => {
        if(valid){
          /*加载进度条*/
          this.loading = true;
          /*这个resp就是服务端返回的数据，成功返回success.data，失败返回null并提示信息*/
          this.postKeyValueRequest('/doLogin', this.loginForm).then(resp => {
            /*关闭进度条*/
            this.loading = false;
            if(resp){
              //alert('b');
              /*登录成功之后弹出RespBean的ok信息，RespBean中的obj就是用户的json信息*/
              //alert(JSON.stringify(resp));
              /*登录成功的用户保存在window.sessionStorage里面，打开页面再关闭这个resp.obj用户信息就没有了*/
              window.sessionStorage.setItem("user", JSON.stringify(resp.obj));
              /*this.$router表示获取到router,replace表示跳转到home页之后就不再回到登录页面了*/
              let path = this.$route.query.redirect;
              this.$router.replace((path == '/' || path == undefined) ? '/home' : path);
            }else{
              /*登录失败刷新验证码*/
              this.verifyCodeUrl='/verifyCode?time='+new Date()
            }
          })
        }else{
          /*element-ui中的消息提示*/
          this.$message.error('请输入所有信息！');
          return false;
        }
      });
    }
  }
}
</script>

<style>
.loginContainer {
      border-radius: 15px;
      background-clip: padding-box;
      margin: 180px auto;
      width: 350px;
      padding: 15px 35px 15px 35px;
      background: #fff;
      border: 1px solid #eaeaea;
      /*表示阴影效果*/
      box-shadow: 0 0 25px #cac6c6;
}
.loginTitle {
    margin: 15px auto 20px auto;
    text-align: center;
    color: #021a32;
}
.loginRemember {
  text-align: left;
  margin: 0px 0px 15px 0px;
  color: #021a32;
}
/*下面的class是在网页端检查到的，直接复制过来的*/
.el-form-item__content{
  display: flex;
  align-items: center;
}
</style>