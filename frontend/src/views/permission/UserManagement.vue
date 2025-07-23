<template>
  <el-card class="page-card">
    <div class="toolbar mb-4 flex justify-between items-center gap-2">
      <el-input v-model="searchKeyword" placeholder="搜索用户" clearable style="width: 240px" />
      <el-button
        v-if="hasPermission('user:create')"
        type="primary"
        icon="Plus"
        @click="openCreateDialog"
      >新增用户</el-button>
    </div>

    <el-table :data="userList" border size="small" v-loading="loading" style="width: 100%">
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="email" label="邮箱" />
      <el-table-column prop="roleName" label="角色" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-switch v-model="row.status" inline-prompt active-text="启" inactive-text="禁" @change="toggleStatus(row)" />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="220">
        <template #default="{ row }">
          <el-button
            v-if="hasPermission('user:update')"
            size="small"
            @click="openEditDialog(row)"
          >编辑</el-button>
          <el-button
            v-if="hasPermission('user:update')"
            size="small"
            type="primary"
            @click="openRoleDialog(row)"
          >分配角色</el-button>
          <el-button size="small" type="warning" @click="resetPassword(row.id)">重置密码</el-button>
          <el-button size="small" type="danger" @click="remove(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="text-right mt-4">
      <el-pagination
        background
        v-model:current-page="page"
        v-model:page-size="size"
        :total="total"
        layout="total, prev, pager, next"
        @current-change="fetchData"
      />
    </div>

    <UserRoleDialog ref="roleDialog" @saved="fetchData" />
    <UserFormDialog ref="createDialog" @saved="fetchData" />
    <UserFormDialog ref="editDialog" mode="edit" @saved="fetchData" />
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { hasPermission } from '../../composables/permission'
import UserRoleDialog from '../@/components/user/UserRoleDialog.vue'
import UserFormDialog from '../@/components/user/UserFormDialog.vue'
import {
  fetchUsers, deleteUser,
  resetUserPassword, updateUserStatus
} from '../../api/user'
import '@/assets/css/permission-ui-enhanced.css'

const userList = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const searchKeyword = ref('')
const loading = ref(false)
const roleDialog = ref()
const createDialog = ref()
const editDialog = ref()


onMounted(() => {
  fetchData()
})

function fetchData() {
  loading.value = true
  fetchUsers({ page: page.value - 1, size: size.value, keyword: searchKeyword.value })
    .then(res => {
      userList.value = res.data.rows || []
      total.value = res.data.total
    })
    .finally(() => (loading.value = false))
}


function remove(id) {
  ElMessageBox.confirm('确认删除该用户吗？', '警告', { type: 'warning' })
    .then(() => deleteUser(id))
    .then(() => {
      ElMessage.success('删除成功')
      fetchData()
    })
}

function toggleStatus(row) {
  updateUserStatus(row.id, row.status)
    .then(() => ElMessage.success('状态更新成功'))
    .catch(() => ElMessage.error('更新失败'))
}

function resetPassword(id) {
  ElMessageBox.confirm('确认重置该用户密码吗？', '警告', { type: 'warning' })
    .then(() => resetUserPassword(id))
    .then(() => ElMessage.success('密码已重置'))
}

function openCreateDialog() {
  createDialog.value.open()
}

function openEditDialog(row) {
  editDialog.value.open(row)
}

function openRoleDialog(row) {
  roleDialog.value.open(row.id)
}
</script>