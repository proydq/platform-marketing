<template>
  <div>
    <el-card>
      <template #header>
        <div class="card-header">
          <span>权限列表</span>
          <div class="toolbar">
            <el-button type="primary" icon="Plus" @click="openDialog()">新增权限</el-button>
            <el-button icon="Refresh" @click="fetchList">刷新</el-button>
          </div>
        </div>
      </template>
      <el-table :data="list" border style="width: 100%" v-loading="loading">
        <el-table-column prop="name" label="名称" width="160" />
        <el-table-column prop="code" label="编码" width="180" />
        <el-table-column prop="description" label="描述" />
        <el-table-column label="操作" width="160">
          <template #default="{ row }">
            <el-button type="text" size="small" icon="Edit" @click="openDialog(row)">编辑</el-button>
            <el-button type="text" size="small" icon="Delete" style="color:#f56c6c" @click="remove(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div style="text-align:right;margin-top:10px;">
        <el-pagination background layout="prev, pager, next" :current-page="page" :page-size="size" :total="total" @current-change="handlePageChange" />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑权限' : '新增权限'" width="450px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="名称">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="编码">
          <el-input v-model="form.code" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible=false">取消</el-button>
        <el-button type="primary" icon="Check" :loading="saving" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listPermissions, createPermission, updatePermission, deletePermission } from '../../api/permission'

const list = ref([])
const total = ref(0)
const page = ref(1)
const size = 10
const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const form = reactive({ id: '', name: '', code: '', description: '' })

onMounted(fetchList)

function fetchList() {
  loading.value = true
  listPermissions({ page: page.value - 1, size }).then(res => {
    if (res.code === 0) {
      list.value = res.data.list
      total.value = res.data.total
    } else {
      ElMessage.error(res.message || '加载失败')
    }
  }).catch(() => {
    ElMessage.error('加载失败')
  }).finally(() => {
    loading.value = false
  })
}

function handlePageChange(p) {
  page.value = p
  fetchList()
}

function openDialog(row) {
  if (row) {
    isEdit.value = true
    Object.assign(form, row)
  } else {
    isEdit.value = false
    Object.assign(form, { id: '', name: '', code: '', description: '' })
  }
  dialogVisible.value = true
}

function save() {
  saving.value = true
  const api = isEdit.value ? updatePermission(form.id, form) : createPermission(form)
  api.then(res => {
    if (res.code === 0) {
      ElMessage.success('保存成功')
      dialogVisible.value = false
      fetchList()
    } else {
      ElMessage.error(res.message || '保存失败')
    }
  }).catch(() => {
    ElMessage.error('保存失败')
  }).finally(() => {
    saving.value = false
  })
}

function remove(row) {
  ElMessageBox.confirm('确认删除该权限?', '提示', { type: 'warning' }).then(() => {
    deletePermission(row.id).then(res => {
      if (res.code === 0) {
        ElMessage.success('已删除')
        fetchList()
      } else {
        ElMessage.error(res.message || '删除失败')
      }
    }).catch(() => {
      ElMessage.error('删除失败')
    })
  })
}
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.toolbar { display:flex; gap:10px; }
</style>

