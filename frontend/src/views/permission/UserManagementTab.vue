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
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'

const roles = ref([
  { id: 1, name: '管理员' },
  { id: 2, name: '编辑' }
])

const list = ref([
  { id: 1, name: '张三', email: 'zhangsan@example.com', role: '管理员', department: '技术部', status: 'online', avatar: '' },
  { id: 2, name: '李四', email: 'lisi@example.com', role: '编辑', department: '内容部', status: 'offline', avatar: '' }
])
const total = ref(list.value.length)
const page = ref(1)
const size = 10

const dialogVisible = ref(false)
const isEdit = ref(false)
const form = reactive({ id: '', name: '', email: '', role: '', department: '' })

function fetchList() {
  ElMessage.success('已刷新')
}

function handlePageChange(val) {
  page.value = val
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

function save() {
  if (isEdit.value) {
    const index = list.value.findIndex(u => u.id === form.id)
    if (index !== -1) list.value[index] = { ...form }
    ElMessage.success('保存成功')
  } else {
    list.value.push({ ...form, id: Date.now(), status: 'offline', avatar: '' })
    ElMessage.success('新增成功')
  }
  total.value = list.value.length
  dialogVisible.value = false
}

function remove(row) {
  list.value = list.value.filter(u => u.id !== row.id)
  total.value = list.value.length
  ElMessage.success('已删除')
}

function resetPwd() {
  ElMessage.success('已重置')
}
</script>

<style scoped>
.toolbar { display:flex; gap:10px; margin-bottom:15px; }
.mb-3 { margin-bottom: 15px; }
</style>

