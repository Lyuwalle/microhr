<template>
  <div style="width: 500px;">
      <el-input
          placeholder="请输入部门名称进行搜索..."
          prefix-icon="el-icon-search"
          v-model="filterText">
      </el-input>

<!--    :expand-on-click-node="false"表示在添加部门或者删除部门的时候不会展开tree-->
    <el-tree
              style="margin-top: 10px"
              :data="deps"
              :props="defaultProps"
              :expand-on-click-node="false"
              :filter-node-method="filterNode"
              ref="tree">
      <span class="custom-tree-node" style="display: flex;justify-content: space-between;width: 100%;"
          slot-scope="{ node, data }">
          <span>{{data.name }}</span>
           <span>
              <el-button
                  type="primary"
                  size="mini"
                  class="depBtn"
                  @click="() => showAddDepView(data)">
                添加部门
              </el-button>
              <el-button
                  type="danger"
                  size="mini"
                  class="depBtn"
                  @click="() => deleteDep(data)">
                删除部门
              </el-button>
          </span>
      </span>
    </el-tree>
<!--    添加对话框-->
    <el-dialog
        title="添加部门"
        :visible.sync="dialogVisible"
        width="30%">
      <div>
        <table>
          <tr>
            <td>
              <el-tag>上级部门</el-tag>
            </td>
            <td>{{pname}}</td>
          </tr>
          <tr>
            <td>
              <el-tag>部门名称</el-tag>
            </td>
            <td>
              <el-input v-model="dep.name" placeholder="请输入部门名称..."></el-input>
            </td>
          </tr>
        </table>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="doAddDep">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>

export default {
  name: "DepMana",
  data(){
    return {
      dialogVisible: false,
      filterText: '',
      dep: {
        name: '',
        parentId: -1
      },
      pname: '',
      deps: [],
      defaultProps: {
        children: 'children',
        label: 'name'
      }
    }
  },
  mounted() {
    this.initDeps();
  },
  watch:{
    /*当搜索框中filterText的值发生变化的时候，就会触发下面的方法*/
    filterText(val){
      this.$refs.tree.filter(val);
    }
  },
  methods:{
    /*每次添加完就把框中数据清空*/
    initDep() {
      this.dep = {
        name: '',
        parentId: -1
      }
      this.pname = '';
    },
    addDepToDeps(deps, dep) {
      for (let i = 0; i < deps.length; i++) {
        let d = deps[i];
        if (d.id == dep.parentId) {
          d.children = d.children.concat(dep);
          if (d.children.length > 0) {
            d.parent = true;
          }
          return;
        } else {
          this.addDepToDeps(d.children, dep);
        }
      }
    },
    doAddDep() {
      this.postRequest("/system/basic/department/", this.dep).then(resp => {
        if (resp) {
          /*动态地往tree中添加一个数据，添加完整个tree不用收起来*/
          this.addDepToDeps(this.deps, resp.obj);
          this.dialogVisible = false;

          //初始化变量，使对话框input框里面数据为空
          this.initDep();
        }
      })
    },
    removeDepFromDeps(p,deps, id) {
      for(let i=0;i<deps.length;i++){
        let d = deps[i];
        if (d.id == id) {
          deps.splice(i, 1);
          if (deps.length == 0) {
            p.parent = false;
          }
          return;
        }else{
          this.removeDepFromDeps(d,d.children, id);
        }
      }
    },
    deleteDep(data) {
      if (data.parent) {
        this.$message.error("父部门删除失败");
      } else {
        this.$confirm('此操作将永久删除【' + data.name + '】部门, 是否继续?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.deleteRequest("/system/basic/department/"+data.id).then(resp=>{
            if (resp) {
              this.removeDepFromDeps(null,this.deps,data.id);
            }
          })
        }).catch(() => {
          this.$message({
            type: 'info',
            message: '已取消删除'
          });
        });
      }
    },
    showAddDepView(data) {
      this.pname = data.name;
      this.dep.parentId = data.id;
      this.dialogVisible = true;
    },
    /*data就是存的一条条的json对象，value就是搜索框的内容*/
    filterNode(value, data) {
      if (!value) return true;
      return data.name.indexOf(value) !== -1;
    },
    initDeps(){
      this.getRequest("/system/basic/department/").then(resp => {
        if(resp){
          this.deps = resp;
        }
      })
    },
  }
}
</script>

<style scoped>
.depBtn {
  padding: 2px;
}
</style>