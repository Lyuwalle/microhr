<template>
  <div>
    <div>
      <!--input输入框-->
      <!--prefix-icon表示添加职位的图标
      v-model表示要添加的数据，以json的形式上传-->
      <el-input
          size="small"
          class="addPosInput"
          element-loading-text="正在加载..."
          element-loading-spinner="el-icon-loading"
          element-loading-background="rgba(0, 0, 0, 0.8)"
          placeholder="添加职位..."
          prefix-icon="el-icon-plus"
          @keydown.enter.native="addPosition"
          v-model="pos.name"
          >
      </el-input>
      <el-button icon="el-icon-plus" size="small" type="primary" @click="addPosition">添加</el-button>
    </div >

    <div class="posManaMain">
      <!--表格展示数据,展示的是从服务端返回的数据positions-->
      <el-table
            :data="positions"
            border
            size="small"
            stripe
            style="width: 70%"
            @selection-change="handleSelectionChange">
<!--        选择框selection-->
        <el-table-column type="selection" width="55"></el-table-column>

        <el-table-column prop="id" label="编号" width="55"></el-table-column>
        <el-table-column prop="name" label="职位名称" width="180"></el-table-column>
        <el-table-column prop="createDate" label="创建时间" width="150"></el-table-column>
        <el-table-column  label="是否启用" width="120">
          <template slot-scope="scope">
            <el-tag size="small" type="success" v-if="scope.row.enabled">已启用</el-tag>
            <el-tag size="small" type="danger" v-else>未启用</el-tag>
          </template>
        </el-table-column>

        <el-table-column  label="操作" width="180">
<!--          slot-scope表示一个占位符，scope.$index表示是第几行的数据，scope.row表示是这一行的json数据-->
          <template slot-scope="scope">
            <el-button size="mini" @click="showEditView(scope.$index, scope.row)">编辑</el-button>
            <el-button size="mini" type="danger" @click="handleDelete(scope.$index, scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-button size="small" type="danger" style="margin-top: 8px" :disabled="multipleSelection.length==0"
                 @click="deleteMany">
        批量删除</el-button>

<!--      弹出编辑对话框-->
      <el-dialog
          title="修改职位"
          :visible.sync="dialogVisible"
          width="30%">
        <div>
          <div>
            <el-tag>职位名称</el-tag>
            <el-input class="updatePosInput" size="small" v-model="updatePos.name"></el-input>
          </div>
          <div>
            <el-tag style="margin-top: 8px">是否启用</el-tag>
            <el-switch class="PosEnabled" v-model="updatePos.enabled" active-text="启用" inactive-text="禁用"></el-switch>
          </div>
        </div>
        <span slot="footer" class="dialog-footer">
          <el-button size="small" @click="dialogVisible = false">取 消</el-button>
          <el-button size="small" type="primary" @click="doUpdate">确 定</el-button>
      </span>
      </el-dialog>
    </div>
  </div>
</template>

<script>
export default {
  name: "PosMana",

  /*根据Vue实例的生命周期，当点击“职位管理”这个选项卡时，应该去初始化这个表格，那么把initPositions这个方法放在mounted()里面就可以实现*/
  mounted() {
    this.initPositions();
  },

  data(){
    return{
      pos:{
        name:'',
      },
      dialogVisible: false,
      positions:[],
      /*来到showEditView页面的时候，会把原来的数据data赋值给updatePos*/
      updatePos:{
        name:'',
        enabled:false
      },
      /*这个属性表示的是多选框中选中的项*/
      multipleSelection: [],

    }
  },
  methods:{
    addPosition(){
      if(this.pos.name){
        this.postRequest("/system/basic/pos/", this.pos).then(resp => {
          if(resp){
            this.initPositions();
            this.pos.name=''
          }
        })
      }else{
        this.$message.error('职位名称不可以为空！');
      }
    },
    initPositions(){
      this.getRequest("/system/basic/pos/").then(resp => {
        console.log(resp)
        if(resp){
          this.positions = resp;
        }
      })
    },
    showEditView(index, data){
      /*来到showEditView页面的时候，会把原来的数据data赋值给updatePos*/
      Object.assign(this.updatePos, data);
      this.dialogVisible = true;
    },
    handleDelete(index, data){
      this.$confirm('此操作将删除【' + data.name + '】职位, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(()=>{
        this.deleteRequest("/system/basic/pos/" + data.id).then(resp => {
          if (resp) {
            this.initPositions();
          }
        })
      }).catch(()=>{
        this.$message({
          type: 'info',
          message: '已取消删除'
        });
      });
    },
    doUpdate(){
        this.putRequest("/system/basic/pos/", this.updatePos).then(resp => {
          if(resp){
            this.initPositions();
            this.updatePos.name='';
            this.dialogVisible = false;
          }
        })
    },
    /*点了多选框之后就会触发这个点击事件*/
    handleSelectionChange(val) {
      this.multipleSelection = val;
    },
    deleteMany(){
      this.$confirm('此操作将永久删除【' + this.multipleSelection.length + '】条记录, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        let ids = '?';
        this.multipleSelection.forEach(item => {
          ids += 'ids=' + item.id + '&';
        })
        this.deleteRequest("/system/basic/pos/" + ids).then(resp => {
          if (resp) {
            this.initPositions();
          }
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        });
      });
    },
  }
}
</script>

<style scoped>
.addPosInput {
  width: 300px;
  margin-right: 8px
}
.posManaMain {
  margin-top: 10px;
}
.updatePosInput {
  width: 200px;
  margin-left: 8px;
}
.PosEnabled{
  margin-left: 18px;
}
</style>