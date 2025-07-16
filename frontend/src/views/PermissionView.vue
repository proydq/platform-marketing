<template>
  <div class="page-wrapper">
    <el-card>
      <div class="action-buttons" style="margin-bottom:10px; display:flex; justify-content:space-between;">
        <el-space>
          <el-input v-model="keyword" placeholder="搜索名称或编码" prefix-icon="Search" style="width:200px" />
          <el-button type="primary" @click="fetchList">搜索</el-button>
        </el-space>
        <el-space>
          <el-button type="primary" @click="openAdd">新增</el-button>
          <el-button type="danger" :disabled="!multipleSelection.length" @click="removeBatch">删除</el-button>
        </el-space>
      </div>
      <el-table :data="list" border @selection-change="val => multipleSelection = val">
        <el-table-column type="selection" width="55" />
        <el-table-column prop="name" label="名称" />
        <el-table-column prop="code" label="编码" />
        <el-table-column prop="description" label="描述" />
        <el-table-column prop="createdAt" label="创建时间" width="160" />
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button type="text" size="small" @click="openEdit(row)">编辑</el-button>
            <el-button type="text" size="small" style="color:#f56c6c" @click="remove(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div style="text-align:right;margin-top:10px;">
        <el-pagination background layout="prev, pager, next" :total="total" :page-size="size" :current-page="page" @current-change="val => { page = val; fetchList(); }" />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑权限' : '新增权限'" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="名称">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="编码">
          <el-input v-model="form.code" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible=false">取消</el-button>
        <el-button type="primary" @click="saveForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listPermissions, createPermission, updatePermission, deletePermission, deletePermissions } from '../api/permissionApi'

const list = ref([])
const total = ref(0)
const page = ref(1)
const size = 10
const keyword = ref('')

const dialogVisible = ref(false)
const isEdit = ref(false)
const form = ref({ id: '', name: '', code: '', description: '' })
const multipleSelection = ref([])

onMounted(() => {
  fetchList()
})

function fetchList() {
  listPermissions({ page: page.value - 1, size, keyword: keyword.value }).then(res => {
    if (res.code === 0) {
      list.value = res.data.list
      total.value = res.data.total
    } else {
      ElMessage.error(res.message || '加载失败')
    }
  }).catch(() => {
    ElMessage.error('加载失败')
  })
}

function openAdd() {
  isEdit.value = false
  form.value = { id: '', name: '', code: '', description: '' }
  dialogVisible.value = true
}

function openEdit(row) {
  isEdit.value = true
  form.value = { ...row }
  dialogVisible.value = true
}

function saveForm() {
  const data = { name: form.value.name, code: form.value.code, description: form.value.description }
  if (isEdit.value) {
    updatePermission(form.value.id, data).then(res => {
      if (res.code === 0) {
        ElMessage.success('更新成功')
        dialogVisible.value = false
        fetchList()
      } else {
        ElMessage.error(res.message || '更新失败')
      }
    }).catch(() => ElMessage.error('更新失败'))
  } else {
    createPermission(data).then(res => {
      if (res.code === 0) {
        ElMessage.success('创建成功')
        dialogVisible.value = false
        fetchList()
      } else {
        ElMessage.error(res.message || '创建失败')
      }
    }).catch(() => ElMessage.error('创建失败'))
  }
}

function remove(row) {
  ElMessageBox.confirm('确定删除该权限吗?', '提示', { type: 'warning' }).then(() => {
    deletePermission(row.id).then(res => {
      if (res.code === 0) {
        ElMessage.success('已删除')
        fetchList()
      } else {
        ElMessage.error(res.message || '删除失败')
      }
    }).catch(() => ElMessage.error('删除失败'))
  }).catch(() => {})
}

function removeBatch() {
  const ids = multipleSelection.value.map(item => item.id)
  if (!ids.length) return
  ElMessageBox.confirm('确定删除选中的权限吗?', '提示', { type: 'warning' }).then(() => {
    deletePermissions(ids).then(res => {
      if (res.code === 0) {
        ElMessage.success('已删除')
        fetchList()
      } else {
        ElMessage.error(res.message || '删除失败')
      }
    }).catch(() => ElMessage.error('删除失败'))
  }).catch(() => {})
}
</script>
