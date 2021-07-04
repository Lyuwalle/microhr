<template>
  <div>
    <el-tabs v-model="activeName" type="card">
      <!--在基础信息设置SysBasic里面引入定义好的组件，定义好的组件放在components文件夹下面-->
      <el-tab-pane label="部门管理" name="depmana"><DepMana></DepMana></el-tab-pane>
      <el-tab-pane label="职位管理" name="posmana"><PosMana></PosMana></el-tab-pane>
      <el-tab-pane label="职称管理" name="joblevelmana"><JobLevelMana></JobLevelMana></el-tab-pane>
      <el-tab-pane label="奖惩规则" name="ecmana"><EcMana></EcMana></el-tab-pane>
      <el-tab-pane label="权限组" name="permissmana"><PermissMana></PermissMana></el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import DepMana from '../../components/sys/basic/DepMana'
import PosMana from '../../components/sys/basic/PosMana'
import JobLevelMana from '../../components/sys/basic/JobLevelMana'
import EcMana from '../../components/sys/basic/EcMana'
import PermissMana from '../../components/sys/basic/PermissMana'
export default {
  name: "SysBasic",
  data(){
    return{
      activeName: 'depmana'
    }
  },
  /*注册组件*/
  components:{
    DepMana,
    PosMana,
    JobLevelMana,
    EcMana,
    PermissMana
  }
}
</script>

<style scoped>

</style>