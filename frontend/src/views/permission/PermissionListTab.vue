<template>
  <div class="permission-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>权限列表</span>
          <div class="toolbar">
            <el-button type="primary" icon="Plus" @click="openDialog()">新增</el-button>
            <el-button type="danger" icon="Delete" :disabled="!multipleSelection.length" @click="removeBatch">删除选中</el-button>
            <el-button icon="Refresh" @click="fetchList">刷新</el-button>
          </div>
        </div>
        <el-form :inline="true" :model="search" class="mt-2" @submit.prevent>
          <el-form-item label="关键词">
            <el-input v-model="search.keyword" placeholder="名称/编码" clearable />
          </el-form-item>
          <el-form-item label="类型">
            <el-select v-model="search.type" placeholder="全部" clearable style="width:120px">
              <el-option label="目录" value="目录" />
              <el-option label="菜单" value="菜单" />
              <el-option label="按钮" value="按钮" />
            </el-select>
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="search.status" placeholder="全部" clearable style="width:120px">
              <el-option label="启用" :value="true" />
              <el-option label="禁用" :value="false" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="Search" @click="fetchList">搜索</el-button>
            <el-button @click="resetSearch">重置</el-button>
          </el-form-item>
        </el-form>
      </template>

      <el-table
        :data="list"
        border
        style="width: 100%"
        v-loading="loading"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="name" label="名称" width="160" />
        <el-table-column prop="code" label="编码" width="160" />
        <el-table-column prop="type" label="类型" width="100" />
        <el-table-column prop="url" label="路由" />
        <el-table-column prop="method" label="方法" width="100" />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-switch v-model="row.status" @change="val => toggleStatus(row)" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160">
          <template #default="{ row }">
            <el-button type="text" size="small" icon="Edit" @click="openDialog(row)">编辑</el-button>
            <el-button type="text" size="small" icon="Delete" style="color:#f56c6c" @click="remove(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div style="text-align:right;margin-top:10px;">
        <el-pagination
          background
          layout="prev, pager, next"
          :current-page="page"
          :page-size="size"
          :total="total"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="编码" prop="code">
          <el-input v-model="form.code" />
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择">
            <el-option label="目录" value="目录" />
            <el-option label="菜单" value="菜单" />
            <el-option label="按钮" value="按钮" />
          </el-select>
        </el-form-item>
        <el-form-item label="路由">
          <el-input v-model="form.url" />
        </el-form-item>
        <el-form-item label="方法">
          <el-select v-model="form.method" placeholder="请选择">
            <el-option label="GET" value="GET" />
            <el-option label="POST" value="POST" />
            <el-option label="PUT" value="PUT" />
            <el-option label="DELETE" value="DELETE" />
          </el-select>
        </el-form-item>
        <el-form-item label="启用">
          <el-switch v-model="form.status" />
        </el-form-item>
        <el-form-item label="说明">
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
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  listPermissions,
  createPermission,
  updatePermission,
  deletePermission,
  deletePermissions
} from '../../api/permission'


const list = ref([])
const total = ref(0)
const page = ref(1)
const size = 10
const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const multipleSelection = ref([])
const formRef = ref()

const form = reactive({
  id: '',
  name: '',
  code: '',
  type: '',
  url: '',
  method: '',
  status: true,
  description: ''
})

const rules = {
  name: [{ required: true, message: '请输入名称', trigger: 'blur' }],
  code: [{ required: true, message: '请输入编码', trigger: 'blur' }],
  type: [{ required: true, message: '请选择类型', trigger: 'change' }]
}

const search = reactive({ keyword: '', type: '', status: '' })

const dialogTitle = computed(() => (isEdit.value ? '编辑权限' : '新增权限'))

onMounted(fetchList)

function fetchList() {
  loading.value = true
  listPermissions({
    page: page.value - 1,
    size,
    keyword: search.keyword,
    type: search.type,
    status: search.status
  })
    .then(res => {
      if (res.code === 0) {
        list.value = res.data.list
        total.value = res.data.total
      } else {
        ElMessage.error(res.message || '加载失败')
      }
    })
    .catch(() => {
      ElMessage.error('加载失败')
    })
    .finally(() => {
      loading.value = false
    })
}

function resetSearch() {
  search.keyword = ''
  search.type = ''
  search.status = ''
  fetchList()
}

function handlePageChange(p) {
  page.value = p
  fetchList()
}

function handleSelectionChange(val) {
  multipleSelection.value = val
}

function openDialog(row) {
  if (row) {
    isEdit.value = true
    Object.assign(form, row)
  } else {
    isEdit.value = false
    Object.assign(form, {
      id: '',
      name: '',
      code: '',
      type: '',
      url: '',
      method: '',
      status: true,
      description: ''
    })
  }
  dialogVisible.value = true
}

function save() {
  formRef.value.validate(valid => {
    if (!valid) return
    saving.value = true
    const api = isEdit.value ? updatePermission(form.id, form) : createPermission(form)
    api
      .then(res => {
        if (res.code === 0) {
          ElMessage.success('保存成功')
          dialogVisible.value = false
          fetchList()
        } else {
          ElMessage.error(res.message || '保存失败')
        }
      })
      .catch(() => {
        ElMessage.error('保存失败')
      })
      .finally(() => {
        saving.value = false
      })
  })
}

function remove(row) {
  ElMessageBox.confirm('确认删除该权限?', '提示', { type: 'warning' })
    .then(() => {
      deletePermission(row.id)
        .then(res => {
          if (res.code === 0) {
            ElMessage.success('已删除')
            fetchList()
          } else {
            ElMessage.error(res.message || '删除失败')
          }
        })
        .catch(() => {
          ElMessage.error('删除失败')
        })
    })
}

function removeBatch() {
  const ids = multipleSelection.value.map(item => item.id)
  if (!ids.length) return
  ElMessageBox.confirm('确认删除选中权限?', '提示', { type: 'warning' }).then(() => {
    deletePermissions(ids)
      .then(res => {
        if (res.code === 0) {
          ElMessage.success('已删除')
          fetchList()
        } else {
          ElMessage.error(res.message || '删除失败')
        }
      })
      .catch(() => {
        ElMessage.error('删除失败')
      })
  })
}

function toggleStatus(row) {
  updatePermission(row.id, { ...row, status: row.status })
    .then(res => {
      if (res.code !== 0) {
        ElMessage.error(res.message || '更新失败')
        row.status = !row.status
      }
    })
    .catch(() => {
      ElMessage.error('更新失败')
      row.status = !row.status
    })
</script>

<style scoped>
.permission-list .card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.toolbar {
  display: flex;
  gap: 10px;
}
.mt-2 {
  margin-top: 10px;
}
</style>
