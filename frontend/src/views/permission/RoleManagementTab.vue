<template>
  <div>
    <div class="toolbar mb-3">
      <el-button type="primary" icon="Plus" @click="openDialog">新建角色</el-button>
    </div>
    <el-row :gutter="20" v-loading="loading">
      <el-col :span="8" v-for="role in roles" :key="role.id">
        <RoleCard :role="role" @edit="openDialog" @delete="remove" />
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
        <el-button type="primary" icon="Check" :loading="saving" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import RoleCard from '../../components/RoleCard.vue'
import { getRoleList, createRole, updateRole, deleteRole, bindRolePermissions } from '../../api/role'
import { getPermissionTree } from '../../api/permission'

const roles = ref([])
const loading = ref(false)
const treeData = ref([])
const expandedKeys = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const saving = ref(false)
const form = reactive({ id: '', name: '', description: '' })
const treeRef = ref()

onMounted(fetchRoles)

function fetchRoles() {
  loading.value = true
  getRoleList()
    .then(res => {
      roles.value = res.data || []
    })
    .catch(() => {
      ElMessage.error('加载失败')
    })
    .finally(() => {
      loading.value = false
    })
}

function openDialog(role) {
  if (role) {
    isEdit.value = true
    Object.assign(form, role)
  } else {
    isEdit.value = false
    Object.assign(form, { id: '', name: '', description: '' })
  }
  loadTree()
  dialogVisible.value = true
}

function loadTree() {
  getPermissionTree({ roleId: form.id }).then(res => {
    treeData.value = res.data.tree || res.data
    expandedKeys.value = treeData.value.map(n => n.id)
    nextTick(() => {
      treeRef.value?.setCheckedKeys(res.data.checked || [])
    })
  })
}

function save() {
  const permissions = treeRef.value?.getCheckedKeys() || []
  const data = { name: form.name, description: form.description }
  saving.value = true
  const req = isEdit.value ? updateRole(form.id, data) : createRole(data)
  req
    .then(res => {
      const roleId = isEdit.value ? form.id : res.data.id
      return bindRolePermissions(roleId, permissions)
    })
    .then(() => {
      ElMessage.success('保存成功')
      dialogVisible.value = false
      fetchRoles()
    })
    .catch(() => {
      ElMessage.error('保存失败')
    })
    .finally(() => {
      saving.value = false
    })
}

function remove(role) {
  deleteRole(role.id)
    .then(() => {
      ElMessage.success('已删除')
      fetchRoles()
    })
    .catch(() => ElMessage.error('删除失败'))

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

