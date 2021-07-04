<template>
  <div>
<!--    添加区域-->
    <div class="permissManaAdd">
      <el-input size="small" placeholder="请输入角色英文名" style="width: 300px;margin-right: 8px"
                v-model="role.name">
<!--        前缀-->
        <template slot="prepend">ROLE_</template>
      </el-input>
      <el-input size="small" placeholder="请输入角色中文名" style="width: 300px;margin-right: 8px"
                v-model="role.nameZh"
                @keydown.enter.native="doAddRole"></el-input>
      <el-button size="small" type="primary" icon="el-icon-plus" @click="doAddRole">添加角色</el-button>
    </div>
<!--显示区域-->
    <div class="permissManaShow">
      <!--                   accordion表示手风琴效果,activeName表示是哪一个id展开，选项卡的id就是name-->
<!--      @change="change"表示当前面板改变时触发-->
      <el-collapse v-model="activeName"
                   accordion
                   @change="change">
        <el-collapse-item :title="r.nameZh" :name="r.id" v-for="(r,index) in roles" :key="index">
          <el-card class="box-card">
            <div slot="header" class="clearfix">
<!--              可访问的资源表示这个角色可以访问的菜单，一级菜单和二级菜单放在了tree控件了里面-->
              <span>可访问的资源</span>
<!--              这个删除按钮表示删除这个角色-->
              <el-button style="float: right; padding: 3px 0;color: #ff0000;" icon="el-icon-delete"
                         type="text" @click="deleteRole(r)"></el-button>
            </div>
            <div>
<!--              树形控件-->
<!--              ”所有“是菜单表中的第一个，id=1，所有的一级菜单的parentId就是1-->
<!--              :default-checked-keys表示选择框selectedMenus数组中包含的项前面选中-->
<!--              node-key表示菜单项的id，作为预选中的参数-->
<!--              ref="tree"表示通过这个名字就可以查找到这个tree,这里是一个tree数组，有多少个角色就有多少个tree-->
<!--              :key表示区分每一个tree的标志，这样加载tree下面的选择框的时候就不会去加载同意俄格tree的-->
              <el-tree show-checkbox
                       node-key="id"
                       ref="tree"
                       :key="index"
                       :default-checked-keys="selectedMenus"
                       :data="allmenus"
                       :props="defaultProps">
              </el-tree>
              <div style="display: flex;justify-content: flex-end">
                <el-button @click="cancelUpdate" style="margin-top: 8px">取消修改</el-button>
                <el-button type="primary" style="margin-top: 8px" @click="doUpdate(r.id,index)">确认修改</el-button>
              </div>
            </div>
          </el-card>
        </el-collapse-item>
      </el-collapse>
    </div>
  </div>
</template>

<script>
export default {
  name: "PermissMana",
  data(){
    return{
      role:{
        name:'',
        nameZh:'',
      },
      activeName:-1,
      roles:[],
      defaultProps:{
        children: 'children',
        label: 'name'
      },
      /*后端传来的菜单放在allmenus里面*/
      allmenus:[],
      selectedMenus: [],
    }
  },
  mounted() {
    this.initRoles();
  },
  methods:{
    initRoles(){
      this.getRequest("/system/basic/permiss/").then(resp => {
        if(resp){
          this.roles = resp;
        }
      })
    },
    doAddRole(){
      if(this.role.name && this.role.nameZh){
        this.postRequest("/system/basic/permiss/role", this.role).then(resp => {
          if(resp){
            this.role.nameZh = '';
            this.role.name = '';
            this.initRoles();
          }
        })
      }else{
        this.$message.error("添加数据不可以为空！");
      }
    },
    change(rid){
      if (rid) {
        this.initAllMenus();
        this.initSelectedMenus(rid);
      }
    },
    deleteRole(role){
      this.$confirm('此操作将永久删除【' + role.nameZh + '】角色, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.deleteRequest("/system/basic/permiss/role/" + role.id).then(resp => {
          if (resp) {
            this.initRoles();
          }
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        });
      });
    },
    cancelUpdate(){
      /*取消修改时直接收起面板*/
      this.activeName = -1;
    },
    doUpdate(rid, index){
      let tree = this.$refs.tree[index];
      let selectedKeys = tree.getCheckedKeys(true);
      let url = '/system/basic/permiss/?rid=' + rid;
      selectedKeys.forEach(key => {
        url += '&mids=' + key;
      })
      this.putRequest(url).then(resp => {
        //console.log(url)
        if (resp) {
          /*更新完之后就要收起面板*/
          this.activeName = -1;
        }
      })
    },
    initAllMenus(){
      this.getRequest("/system/basic/permiss/menus").then(resp => {
        if (resp) {
          this.allmenus = resp;
        }
      })
    },
    initSelectedMenus(rid){
      this.getRequest("/system/basic/permiss/mids/" + rid).then(resp => {
        if (resp) {
          this.selectedMenus = resp;
        }
      })
    },
  }
}
</script>

<style scoped>
.permissManaAdd{
  display: flex;
  justify-content: flex-start;
}
.permissManaShow{
  margin-top: 10px;
  width: 700px;
}
</style>