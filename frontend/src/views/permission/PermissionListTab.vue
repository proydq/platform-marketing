<template>
  <el-card class="page-card">
    <div class="toolbar mb-4 flex justify-between items-center gap-2">
      <div class="filters flex items-center gap-2">
        <el-input v-model="searchKeyword" placeholder="权限名称/编码" clearable style="width: 220px" />
        <el-select v-model="searchType" placeholder="类型" clearable style="width: 120px">
          <el-option label="目录" value="目录" />
          <el-option label="菜单" value="菜单" />
          <el-option label="按钮" value="按钮" />
        </el-select>
        <el-select v-model="searchStatus" placeholder="状态" clearable style="width: 120px">
          <el-option label="启用" :value="true" />
          <el-option label="禁用" :value="false" />
        </el-select>
        <el-button type="primary" icon="Search" @click="fetchData">搜索</el-button>
      </div>
      <div class="actions flex gap-2">
        <el-button type="danger" icon="Delete" :disabled="!selection.length" @click="handleBatchDelete">批量删除</el-button>
        <el-button type="primary" icon="Plus" @click="openDialog(false)">新增权限</el-button>
      </div>
    </div>

    <el-table
      :data="permissionList"
      border
      size="small"
      v-loading="loading"
      style="width: 100%"
      @selection-change="selection = $event"
    >
      <el-table-column type="selection" width="50" />
      <el-table-column prop="name" label="权限名称" />
      <el-table-column prop="code" label="权限编码" />
      <el-table-column prop="type" label="类型" width="80" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-switch
            v-model="row.status"
            :active-value="true"
            :inactive-value="false"
            inline-prompt
            active-text="启"
            inactive-text="禁"
            @change="toggleStatus(row)"
          />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button size="small" @click="openDialog(true, row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
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

    <el-drawer class="page-dialog" v-model="drawerVisible" size="400px" direction="rtl">
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
            :data="treeOptions"
            :props="{ label: 'name', children: 'children' }"
            clearable
            check-strictly
            placeholder="请选择"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" :active-value="true" :inactive-value="false" inline-prompt active-text="启" inactive-text="禁" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="drawerVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="save">保存</el-button>
      </template>
    </el-drawer>
  </el-card>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  fetchPermissions,
  createPermission,
  updatePermission,
  deletePermission,
  fetchPermissionTree,
  updatePermissionStatus,
  deletePermissionsBatch
} from '../../api/permission'
import '@/assets/css/permission-ui-enhanced.css'

const permissionList = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const searchKeyword = ref('')
const searchType = ref('')
const searchStatus = ref()
const selection = ref([])
const loading = ref(false)
const saving = ref(false)
const drawerVisible = ref(false)
const isEdit = ref(false)
const treeOptions = ref([])

const form = reactive({
  id: '',
  name: '',
  code: '',
  type: '',
  parent_id: '',
  status: true
})

onMounted(() => {
  fetchData()
  loadTree()
})

function fetchData() {
  loading.value = true
  fetchPermissions({
    page: page.value - 1,
    size: size.value,
    keyword: searchKeyword.value,
    type: searchType.value,
    status: searchStatus.value
  })
    .then(res => {
      permissionList.value = res.data.rows || []
      total.value = res.data.total
    })
    .finally(() => (loading.value = false))
}

function loadTree() {
  fetchPermissionTree().then(res => {
    treeOptions.value = res.data || []
  })
}

function openDialog(edit = false, data = null) {
  isEdit.value = edit
  if (edit && data) {
    Object.assign(form, data)
  } else {
    Object.assign(form, {
      id: '',
      name: '',
      code: '',
      type: '',
      parent_id: '',
      status: true
    })
  }
  drawerVisible.value = true
}

function save() {
  saving.value = true
  const fn = isEdit.value ? updatePermission : createPermission
  fn(form)
    .then(() => {
      ElMessage.success('保存成功')
      drawerVisible.value = false
      fetchData()
    })
    .finally(() => (saving.value = false))
}

function handleDelete(id) {
  ElMessageBox.confirm('确认删除该权限吗？', '提示', { type: 'warning' })
    .then(() => deletePermission(id))
    .then(() => {
      ElMessage.success('删除成功')
      fetchData()
    })
}

function handleBatchDelete() {
  const ids = selection.value.map(i => i.id)
  if (!ids.length) return
  ElMessageBox.confirm(`确认批量删除选中项？`, '警告', { type: 'warning' })
    .then(() => deletePermissionsBatch(ids))
    .then(() => {
      ElMessage.success('删除成功')
      fetchData()
    })
}

function toggleStatus(row) {
  updatePermissionStatus(row.id, row.status)
    .then(() => ElMessage.success('状态更新成功'))
    .catch(() => ElMessage.error('状态更新失败'))
}
</script>