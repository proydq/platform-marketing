<template>
  <div>
    <div class="toolbar mb-3">
      <el-button type="primary" icon="Plus" @click="openDialog">新建角色</el-button>
    </div>

    <el-table :data="roles" border v-loading="loading" style="width: 100%">
      <el-table-column prop="name" label="角色名称" />
      <el-table-column prop="description" label="角色描述" />
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="openDialog(row)">编辑</el-button>
          <el-button type="danger" size="small" @click="remove(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑角色' : '新建角色'" width="600px">
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
        :data="treeData"
        node-key="id"
        show-checkbox
        :props="{ label: 'name', children: 'children' }"
        :default-checked-keys="checkedKeys"
        class="permission-tree"
      />

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  fetchRoles, createRole, updateRole, deleteRole,
  fetchRolePermissions, bindPermissions
} from '../api/role'
import { fetchPermissionTree } from '../api/permission'

const roles = ref([])
const loading = ref(false)
const saving = ref(false)
const treeData = ref([])
const checkedKeys = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const form = reactive({ id: '', name: '', description: '' })
const treeRef = ref()

onMounted(() => {
  loadRoles()
  loadPermissionTree()
})

function loadRoles() {
  loading.value = true
  fetchRoles().then(res => {
    roles.value = res.data || []
  }).finally(() => loading.value = false)
}

function loadPermissionTree() {
  fetchPermissionTree().then(res => {
    treeData.value = res.data || []
  })
}

function openDialog(role) {
  if (role) {
    isEdit.value = true
    Object.assign(form, role)
    fetchRolePermissions(role.id).then(res => {
      checkedKeys.value = res.data || []
    })
  } else {
    isEdit.value = false
    Object.assign(form, { id: '', name: '', description: '' })
    checkedKeys.value = []
  }
  dialogVisible.value = true
}

function save() {
  saving.value = true
  const handler = isEdit.value ? updateRole : createRole
  const payload = { ...form }

  handler(form.id, payload).then(res => {
    const roleId = res.data.id
    const permissionIds = treeRef.value.getCheckedKeys()
    return bindPermissions(roleId, permissionIds)
  }).then(() => {
    ElMessage.success('保存成功')
    dialogVisible.value = false
    loadRoles()
  }).catch(() => {
    ElMessage.error('保存失败')
  }).finally(() => saving.value = false)
}

function remove(id) {
  deleteRole(id).then(() => {
    ElMessage.success('删除成功')
    loadRoles()
  })
}
</script>

<style scoped>
.permission-tree {
  margin-top: 10px;
  border: 1px solid #ebeef5;
  border-radius: 6px;
  padding: 10px;
  max-height: 300px;
  overflow: auto;
}
</style>