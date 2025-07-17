<template>
  <div>
    <div class="toolbar mb-3">
      <el-button type="primary" icon="Plus" @click="openDialog">新增用户</el-button>
      <el-button icon="Refresh" @click="fetchList">刷新</el-button>
    </div>
    <el-table :data="list" style="width:100%" border>
      <el-table-column prop="name" label="用户名" width="160">
        <template #default="{ row }">
          <el-avatar :src="row.avatar" size="small">{{ row.name?.charAt(0) }}</el-avatar>
          <span style="margin-left:8px">{{ row.name }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status==='online'?'success':'info'">{{ row.status }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="role" label="角色" width="120">
        <template #default="{ row }">
          <el-tag type="primary">{{ row.role }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="department" label="部门" width="120" />
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button type="text" size="small" icon="Edit" @click="openDialog(row)">编辑</el-button>
          <el-button type="text" size="small" icon="Refresh" @click="resetPwd(row)">重置密码</el-button>
          <el-button type="text" size="small" icon="Delete" style="color:#f56c6c" @click="remove(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <div style="text-align:right;margin-top:10px;">
      <el-pagination background layout="prev, pager, next" :current-page="page" :page-size="size" :total="total" @current-change="handlePageChange" />
    </div>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑用户' : '新增用户'" width="400px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="用户名">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="form.role">
            <el-option v-for="r in roles" :key="r.id" :label="r.name" :value="r.name" />
          </el-select>
        </el-form-item>
        <el-form-item label="部门">
          <el-input v-model="form.department" />
        </el-form-item>
      </el-form>
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
import { getUserList, updateUser, importUsers } from '../../api/userApi'
import { createUser, deleteUser, resetUserPassword } from '../../api/userApi'
import { getRoleList } from '../../api/roleApi'

const list = ref([])
const total = ref(0)
const page = ref(1)
const size = 10

const roles = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const form = reactive({ id:'', name:'', email:'', role:'', department:'' })

onMounted(() => {
  loadRoles()
  fetchList()
})

function loadRoles(){
  getRoleList().then(res => {
    if(res.code === 0) roles.value = res.data
  })
}

function fetchList(){
  getUserList({ page: page.value - 1, size }).then(res => {
    if(res.code === 0){
      list.value = res.data.list
      total.value = res.data.total
    }
  })
}

function handlePageChange(val){
  page.value = val
  fetchList()
}

function openDialog(user){
  if(user){
    isEdit.value = true
    Object.assign(form, user)
  }else{
    isEdit.value = false
    Object.assign(form,{ id:'', name:'', email:'', role:'', department:'' })
  }
  dialogVisible.value = true
}

function save(){
  const api = isEdit.value ? updateUser(form.id, form) : createUser(form)
  api.then(res => {
    if(res.code === 0){
      ElMessage.success('保存成功')
      dialogVisible.value = false
      fetchList()
    }
  })
}

function remove(row){
  deleteUser(row.id).then(res => {
    if(res.code === 0){
      ElMessage.success('已删除')
      fetchList()
    }
  })
}

function resetPwd(row){
  resetUserPassword(row.id).then(res => {
    if(res.code === 0) ElMessage.success('已重置')
  })
}
</script>

<style scoped>
.toolbar { display:flex; gap:10px; margin-bottom:15px; }
.mb-3 { margin-bottom: 15px; }
</style>

