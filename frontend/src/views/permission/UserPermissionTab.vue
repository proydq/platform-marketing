<template>
  <div>
    <el-card>
      <div class="toolbar">
        <div class="left">
          <el-input v-model="search.keyword" placeholder="关键字" clearable prefix-icon="Search" style="width:180px" />
          <el-select v-model="search.type" placeholder="类型" clearable style="width:120px">
            <el-option label="菜单" value="menu" />
            <el-option label="接口" value="api" />
            <el-option label="数据" value="data" />
          </el-select>
          <el-select v-model="search.status" placeholder="状态" clearable style="width:120px">
            <el-option label="启用" :value="true" />
            <el-option label="停用" :value="false" />
          </el-select>
          <el-button type="primary" icon="Search" @click="fetchList">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </div>
        <div class="right">
          <el-button type="primary" icon="Plus" @click="openAdd">新建权限</el-button>
          <el-button icon="Refresh" @click="fetchList">刷新</el-button>
          <el-button type="danger" icon="Delete" :disabled="!multipleSelection.length" @click="removeBatch">批量删除</el-button>
        </div>
      </div>

      <el-table :data="list" border style="width: 100%" @selection-change="onSelectionChange" v-loading="loading" empty-text="暂无数据">
        <el-table-column type="selection" width="55" />
        <el-table-column prop="name" label="权限名称" width="160" />
        <el-table-column prop="code" label="权限编码" width="160" />
        <el-table-column prop="roleName" label="角色名" width="150" />
        <el-table-column prop="status" label="启用" width="80">
          <template #default="{ row }">
            <el-switch v-model="row.status" @change="toggleStatus(row)" />
          </template>
        </el-table-column>
        <el-table-column prop="createdBy" label="创建人" width="100" />
        <el-table-column prop="updatedBy" label="修改人" width="100" />
        <el-table-column prop="createdAt" label="创建时间" width="160">
          <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button type="text" size="small" icon="Edit" @click="openEdit(row)">编辑</el-button>
            <el-button type="text" size="small" icon="Delete" style="color:#f56c6c" @click="remove(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div style="text-align: right; margin-top: 10px;">
        <el-pagination background layout="prev, pager, next" :page-size="size" :total="total" :current-page="page" @current-change="handlePageChange" />
      </div>
    </el-card>

    <el-drawer v-model="drawerVisible" :title="isEdit ? '编辑权限' : '新建权限'" size="480px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="权限名称" prop="name">
              <el-input v-model="form.name" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="权限编码" prop="code">
              <el-input v-model="form.code" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="权限类型" prop="type">
              <el-select v-model="form.type">
                <el-option label="菜单" value="menu" />
                <el-option label="接口" value="api" />
                <el-option label="数据" value="data" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="分组">
              <el-input v-model="form.group" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="启用">
              <el-switch v-model="form.status" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="描述">
              <el-input v-model="form.description" type="textarea" rows="3" />
            </el-form-item>
          </el-col>
          <el-col :span="12" v-if="isEdit">
            <el-form-item label="创建人">
              <el-input v-model="form.createdBy" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12" v-if="isEdit">
            <el-form-item label="修改人">
              <el-input v-model="form.updatedBy" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12" v-if="isEdit">
            <el-form-item label="创建时间">
              <el-input v-model="form.createdAt" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12" v-if="isEdit">
            <el-form-item label="修改时间">
              <el-input v-model="form.updatedAt" disabled />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div style="text-align:right">
          <el-button @click="drawerVisible = false">取消</el-button>
          <el-button type="primary" @click="saveForm">提交</el-button>
        </div>
      </template>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listPermissions, createPermission, updatePermission, deletePermission, deletePermissions } from '../../api/permissionApi'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const page = ref(1)
const size = 10
const search = reactive({ keyword: '', type: '', status: '' })

const drawerVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const form = reactive({
  id: '',
  name: '',
  code: '',
  type: '',
  group: '',
  status: true,
  description: '',
  createdBy: '',
  updatedBy: '',
  createdAt: '',
  updatedAt: ''
})

const rules = {
  name: [{ required: true, message: '请输入权限名称', trigger: 'blur' }],
  code: [{ required: true, message: '请输入权限编码', trigger: 'blur' }],
  type: [{ required: true, message: '请选择权限类型', trigger: 'change' }]
}

const multipleSelection = ref([])

onMounted(fetchList)

function handlePageChange(val) {
  page.value = val
  fetchList()
}
function resetSearch() {
  search.keyword = ''
  search.type = ''
  search.status = ''
  fetchList()
}

function fetchList() {
  loading.value = true
  listPermissions({
    page: page.value - 1,
    size,
    module: 'user',
    keyword: search.keyword,
    type: search.type,
    status: search.status
  })
    .then(res => {
      loading.value = false
      if (res.code === 0) {
        list.value = res.data.list
        total.value = res.data.total
      } else {
        ElMessage.error(res.message || '加载失败')
      }
    })
    .catch(() => {
      loading.value = false
      ElMessage.error('加载失败')
    })
}

function openAdd() {
  isEdit.value = false
  Object.assign(form, { id: '', name: '', code: '', type: '', group: '', status: true, description: '' })
  drawerVisible.value = true
}

function openEdit(row) {
  isEdit.value = true
  Object.assign(form, row)
  drawerVisible.value = true
}

function saveForm() {
  formRef.value.validate(valid => {
    if (!valid) return
    const data = { name: form.name, code: form.code, description: form.description, type: form.type, group: form.group, status: form.status, module: 'user' }
    const req = isEdit.value ? updatePermission(form.id, data) : createPermission(data)
    req.then(res => {
      if (res.code === 0) {
        ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
        drawerVisible.value = false
        fetchList()
      } else {
        ElMessage.error(res.message || '保存失败')
      }
    }).catch(() => ElMessage.error('保存失败'))
  })
}

function remove(row) {
  ElMessageBox.confirm('确定删除该权限吗？', '提示', { type: 'warning' })
    .then(() => {
      deletePermission(row.id).then(res => {
        if (res.code === 0) {
          ElMessage.success('已删除')
          fetchList()
        } else {
          ElMessage.error(res.message || '删除失败')
        }
      }).catch(() => ElMessage.error('删除失败'))
    })
    .catch(() => {})
}

function removeBatch() {
  const ids = multipleSelection.value.map(item => item.id)
  if (!ids.length) return
  ElMessageBox.confirm('确定删除选中的权限吗？', '提示', { type: 'warning' })
    .then(() => {
      deletePermissions(ids).then(res => {
        if (res.code === 0) {
          ElMessage.success('已删除')
          fetchList()
        } else {
          ElMessage.error(res.message || '删除失败')
        }
      }).catch(() => ElMessage.error('删除失败'))
    })
    .catch(() => {})
}

function onSelectionChange(val) {
  multipleSelection.value = val
}

function toggleStatus(row) {
  updatePermission(row.id, { status: row.status }).then(res => {
    if (res.code !== 0) {
      ElMessage.error(res.message || '状态更新失败')
      row.status = !row.status
    }
  }).catch(() => {
    ElMessage.error('状态更新失败')
    row.status = !row.status
  })
}

function formatDate(val) {
  if (!val) return ''
  return new Date(val).toLocaleString()
}
</script>

<style scoped>
.toolbar {
  display: flex;
  justify-content: space-between;
  margin-bottom: 15px;
}
.toolbar .left,
.toolbar .right {
  display: flex;
  align-items: center;
}
.toolbar .left > * {
  margin-right: 10px;
}
.toolbar .right > * {
  margin-left: 10px;
}
</style>
