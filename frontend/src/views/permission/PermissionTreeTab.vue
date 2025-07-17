<template>
  <el-card class="page-card">
    <div class="toolbar mb-4 text-right gap-2">
      <el-button type="primary" icon="Plus" @click="openAddDialog">新增权限</el-button>
    </div>

    <el-tree
      :data="treeData"
      :props="defaultProps"
      node-key="id"
      :expand-on-click-node="false"
      ref="treeRef"
      highlight-current
      default-expand-all
    >
      <template #default="{ node, data }">
        <span class="tree-node">
          {{ data.name }}（{{ data.code }}）
          <span class="actions">
            <el-button type="text" size="small" @click.stop="openEditDialog(data)">编辑</el-button>
            <el-button type="text" size="small" style="color:red" @click.stop="remove(data.id)">删除</el-button>
          </span>
        </span>
      </template>
    </el-tree>

    <el-dialog class="page-dialog" v-model="dialogVisible" width="500px">
      <template #title>
        <strong>{{ isEdit ? '编辑权限' : '新增权限' }}</strong>
      </template>
      <el-form class="dialog-form" :model="form" label-width="100px">
        <el-form-item label="权限名称">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="权限编码">
          <el-input v-model="form.code" />
        </el-form-item>
        <el-form-item label="权限类型">
          <el-select v-model="form.type" placeholder="请选择">
            <el-option label="目录" value="目录" />
            <el-option label="菜单" value="菜单" />
            <el-option label="按钮" value="按钮" />
          </el-select>
        </el-form-item>
        <el-form-item label="上级权限">
          <el-tree-select
            v-model="form.parent_id"
            :data="treeData"
            :props="defaultProps"
            check-strictly
            clearable
            placeholder="请选择上级"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  fetchPermissionTree,
  createPermission,
  updatePermission,
  deletePermission
} from '../../api/permission'
import '@/assets/css/permission-ui-enhanced.css'

const treeData = ref([])
const defaultProps = { label: 'name', children: 'children' }
const dialogVisible = ref(false)
const isEdit = ref(false)
const saving = ref(false)
const treeRef = ref()

const form = reactive({
  id: '',
  name: '',
  code: '',
  type: '',
  parent_id: ''
})

onMounted(loadTree)

function loadTree() {
  fetchPermissionTree().then(res => {
    treeData.value = res.data || []
  })
}

function openAddDialog() {
  isEdit.value = false
  Object.assign(form, { id: '', name: '', code: '', type: '', parent_id: '' })
  dialogVisible.value = true
}

function openEditDialog(data) {
  isEdit.value = true
  Object.assign(form, data)
  dialogVisible.value = true
}

function save() {
  saving.value = true
  const fn = isEdit.value ? updatePermission : createPermission
  fn(form.id, form)
    .then(() => {
      ElMessage.success('保存成功')
      dialogVisible.value = false
      loadTree()
    })
    .finally(() => (saving.value = false))
}

function remove(id) {
  ElMessageBox.confirm('确认删除该权限？', '提示', { type: 'warning' })
    .then(() => deletePermission(id))
    .then(() => {
      ElMessage.success('删除成功')
      loadTree()
    })
}
</script>

<style scoped>
.tree-node {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>