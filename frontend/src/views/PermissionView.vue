<template>
  <div class="page-wrapper">
    <el-card>
      <div class="toolbar" style="margin-bottom:10px; display:flex; justify-content:space-between;">
        <el-space>
          <el-input v-model="search.keyword" placeholder="关键字" prefix-icon="Search" style="width:180px" />
          <el-select v-model="search.type" placeholder="类型" clearable style="width:120px">
            <el-option label="菜单" value="menu" />
            <el-option label="接口" value="api" />
            <el-option label="数据" value="data" />
          </el-select>
          <el-select v-model="search.status" placeholder="状态" clearable style="width:120px">
            <el-option label="启用" :value="true" />
            <el-option label="禁用" :value="false" />
          </el-select>
          <el-button type="primary" @click="fetchList">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-space>
        <el-space>
          <el-button type="primary" @click="openAdd">新增</el-button>
          <el-button type="danger" :disabled="!multipleSelection.length" @click="removeBatch">删除</el-button>
          <el-button @click="fetchList">刷新</el-button>
        </el-space>
      </div>
      <el-table :data="list" border @selection-change="val => multipleSelection = val" style="width:100%">
        <el-table-column type="selection" width="55" />
        <el-table-column prop="name" label="名称" width="120" />
        <el-table-column prop="code" label="编码" width="120" />
        <el-table-column prop="type" label="类型" width="80" />
        <el-table-column prop="group" label="分组" width="120" />
        <el-table-column prop="status" label="启用" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status ? 'success' : 'info'">{{ row.status ? '启用' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" />
        <el-table-column prop="createdBy" label="创建人" width="100" />
        <el-table-column prop="updatedBy" label="修改人" width="100" />
        <el-table-column prop="createdAt" label="创建时间" width="160" />
        <el-table-column prop="updatedAt" label="修改时间" width="160" />
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button type="text" size="small" @click="openEdit(row)">编辑</el-button>
            <el-button type="text" size="small" style="color:#f56c6c" @click="remove(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div style="text-align:right;margin-top:10px;">
        <el-pagination background layout="prev, pager, next" :total="total" :page-size="size" :current-page="page" @current-change="val => { page = val; fetchList() }" />
      </div>
    </el-card>

    <el-drawer v-model="drawerVisible" :title="isEdit ? '编辑权限' : '新增权限'" size="500px" :destroy-on-close="true">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item prop="name" label="名称">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item prop="code" label="编码">
          <el-input v-model="form.code" />
        </el-form-item>
        <el-form-item prop="type" label="类型">
          <el-radio-group v-model="form.type">
            <el-radio label="menu">菜单</el-radio>
            <el-radio label="api">接口</el-radio>
            <el-radio label="data">数据</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="分组">
          <el-input v-model="form.group" />
        </el-form-item>
        <el-form-item label="启用">
          <el-switch v-model="form.status" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" />
        </el-form-item>
        <el-form-item v-if="isEdit" label="创建人">
          <el-input v-model="form.createdBy" disabled />
        </el-form-item>
        <el-form-item v-if="isEdit" label="修改人">
          <el-input v-model="form.updatedBy" disabled />
        </el-form-item>
        <el-form-item v-if="isEdit" label="创建时间">
          <el-input v-model="form.createdAt" disabled />
        </el-form-item>
        <el-form-item v-if="isEdit" label="修改时间">
          <el-input v-model="form.updatedAt" disabled />
        </el-form-item>
      </el-form>
      <template #footer>
        <div style="text-align:right; width:100%">
          <el-button @click="drawerVisible = false">取消</el-button>
          <el-button type="primary" @click="saveForm">确定</el-button>
        </div>
      </template>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listPermissions, createPermission, updatePermission, deletePermission, deletePermissions } from '../api/permissionApi'

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
  type: 'menu',
  group: '',
  status: true,
  description: '',
  createdBy: '',
  updatedBy: '',
  createdAt: '',
  updatedAt: ''
})

const rules = {
  name: [{ required: true, message: '请输入名称', trigger: 'blur' }],
  code: [{ required: true, message: '请输入编码', trigger: 'blur' }],
  type: [{ required: true, message: '请选择类型', trigger: 'change' }]
}

const multipleSelection = ref([])

onMounted(() => {
  fetchList()
})

function resetSearch() {
  search.keyword = ''
  search.type = ''
  search.status = ''
  fetchList()
}

function fetchList() {
  listPermissions({
    page: page.value - 1,
    size,
    keyword: search.keyword,
    type: search.type,
    status: search.status
  }).then(res => {
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
  Object.assign(form, { id: '', name: '', code: '', description: '', type: 'menu', group: '', status: true })
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
    const data = { name: form.name, code: form.code, description: form.description, type: form.type, group: form.group, status: form.status }
    if (isEdit.value) {
      updatePermission(form.id, data).then(res => {
        if (res.code === 0) {
          ElMessage.success('更新成功')
          drawerVisible.value = false
          fetchList()
        } else {
          ElMessage.error(res.message || '更新失败')
        }
      }).catch(() => ElMessage.error('更新失败'))
    } else {
      createPermission(data).then(res => {
        if (res.code === 0) {
          ElMessage.success('创建成功')
          drawerVisible.value = false
          fetchList()
        } else {
          ElMessage.error(res.message || '创建失败')
        }
      }).catch(() => ElMessage.error('创建失败'))
    }
  })
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
