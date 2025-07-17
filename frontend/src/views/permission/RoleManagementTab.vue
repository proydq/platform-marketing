<template>
  <el-card class="page-card">
    <div class="toolbar mb-4 flex gap-2">
      <el-button type="primary" icon="Plus" @click="openDialog(false)">新建角色</el-button>
    </div>

    <el-table :data="roles" border size="small" v-loading="loading" style="width: 100%">
      <el-table-column prop="name" label="角色名称" />
      <el-table-column prop="description" label="角色描述" />
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="openDialog(true, row)">编辑</el-button>
          <el-button type="danger" size="small" @click="remove(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-drawer class="page-dialog" v-model="drawerVisible" direction="rtl" size="400px">
      <template #title>
        <strong>{{ isEdit ? '编辑角色' : '新增角色' }}</strong>
      </template>
      <el-form class="dialog-form" :model="form" label-width="80px">
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
        <el-button @click="drawerVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="save">保存</el-button>
      </template>
    </el-drawer>
  </el-card>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  fetchRoles, createRole, updateRole, deleteRole,
  fetchRolePermissions, bindPermissions
} from '../../api/role'
import { fetchPermissionTree } from '../../api/permission'
import '@/assets/css/permission-ui-enhanced.css'

const roles = ref([])
const loading = ref(false)
const saving = ref(false)
const treeData = ref([])
const checkedKeys = ref([])
const drawerVisible = ref(false)
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

function openDialog(edit = false, data = null) {
  isEdit.value = edit
  if (edit && data) {
    Object.assign(form, data)
    fetchRolePermissions(data.id).then(res => {
      checkedKeys.value = res.data || []
    })
  } else {
    Object.assign(form, { id: '', name: '', description: '' })
    checkedKeys.value = []
  }
  drawerVisible.value = true
}

function save() {
  saving.value = true
  const handler = isEdit.value ? updateRole : createRole
  const payload = { ...form }

  handler(payload).then(res => {
    const roleId = res.data.id || form.id
    const permissionIds = treeRef.value.getCheckedKeys()
    return bindPermissions(roleId, permissionIds)
  }).then(() => {
    ElMessage.success('保存成功')
    drawerVisible.value = false
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