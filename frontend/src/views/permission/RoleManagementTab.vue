<template>
  <div>
    <div class="toolbar mb-3">
      <el-button type="primary" icon="Plus" @click="openDialog">新建角色</el-button>
    </div>
    <el-row :gutter="20">
      <el-col :span="8" v-for="role in roles" :key="role.id">
        <RoleCard :role="role" />
        <div style="text-align:right; margin-top:6px;">
          <el-button type="primary" size="small" icon="Edit" @click="openDialog(role)">编辑</el-button>
          <el-button type="danger" size="small" icon="Delete" @click="remove(role)">删除</el-button>
        </div>
      </el-col>
    </el-row>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑角色' : '创建角色'" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="名称">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" />
        </el-form-item>
      </el-form>
      <el-tree
        ref="treeRef"
        class="permission-tree"
        :data="treeData"
        node-key="id"
        show-checkbox
        :default-expanded-keys="expandedKeys"
      />
      <template #footer>
        <el-button @click="dialogVisible=false">取消</el-button>
        <el-button type="primary" icon="Check" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import RoleCard from '../../components/RoleCard.vue'
import { getRoleList, createRole, updateRole, deleteRole } from '../../api/roleApi'
import { getPermissionTree } from '../../api/permissionApi'

const roles = ref([])
const treeData = ref([])
const expandedKeys = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const form = reactive({ id:'', name:'', description:'' })
const treeRef = ref()

onMounted(loadRoles)

function loadRoles(){
  getRoleList().then(res => {
    if(res.code === 0) roles.value = res.data
  })
}

function openDialog(role){
  if(role){
    isEdit.value = true
    Object.assign(form, role)
    loadTree(role.id)
  }else{
    isEdit.value = false
    Object.assign(form,{ id:'', name:'', description:'' })
    loadTree()
  }
  dialogVisible.value = true
}

function loadTree(roleId){
  getPermissionTree({ roleId }).then(res => {
    if(res.code === 0){
      treeData.value = res.data
      expandedKeys.value = treeData.value.slice(0,4).map(n=>n.id)
    }
  })
}

function save(){
  const permissions = treeRef.value?.getCheckedKeys() || []
  const data = { name: form.name, description: form.description, permissions }
  const api = isEdit.value ? updateRole(form.id, data) : createRole(data)
  api.then(res => {
    if(res.code === 0){
      ElMessage.success('保存成功')
      dialogVisible.value = false
      loadRoles()
    }
  })
}

function remove(role){
  deleteRole(role.id).then(res => {
    if(res.code === 0){
      ElMessage.success('已删除')
      loadRoles()
    }
  })
}
</script>

<style scoped>
.toolbar {
  display: flex;
  justify-content: space-between;
  margin-bottom: 15px;
}
.mb-3 { margin-bottom: 15px; }
</style>

