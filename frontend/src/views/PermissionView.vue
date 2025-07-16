<template>
  <div class="page-wrapper">
    <el-row class="action-buttons" justify="space-between" align="middle" style="margin-bottom:10px;">
      <el-space>
        <el-button type="primary" @click="openForm(false)">
          <el-icon><CirclePlusFilled/></el-icon>新建权限
        </el-button>
        <el-button type="danger" :disabled="!selectedIds.length" @click="handleDeleteBatch">批量删除</el-button>
      </el-space>
      <el-input v-model="keyword" placeholder="搜索名称或编码" prefix-icon="Search" style="width:200px" clearable @keyup.enter.native="fetchList" />
    </el-row>

    <el-card class="section-card">
      <el-table :data="list" row-key="id" @selection-change="val => selectedIds = val.map(i => i.id)">
        <el-table-column type="selection" width="55" />
        <el-table-column prop="name" label="名称" min-width="120" />
        <el-table-column prop="code" label="编码" min-width="120" />
        <el-table-column prop="description" label="描述" />
        <el-table-column label="操作" width="120" align="center">
          <template #default="{ row }">
            <el-button type="text" @click="openForm(true, row)">编辑</el-button>
            <el-button type="text" style="color:#f56c6c" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div style="text-align:right;margin-top:10px;">
        <el-pagination background layout="prev, pager, next" :total="total" :page-size="size" :current-page="page" @current-change="handlePageChange" />
      </div>
    </el-card>

    <el-dialog v-model="formVisible" :title="isEdit ? '编辑权限' : '新建权限'" width="500px">
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
        <el-button @click="formVisible = false">取消</el-button>
        <el-button type="primary" @click="saveForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Edit, Delete, CirclePlusFilled, InfoFilled } from '@element-plus/icons-vue'
import { getRoleList, getPermissionTree, savePermissions } from '../api/permissionApi'
import permsData from '../mock/permissions.json'
import usersData from '../mock/users.json'

const roles = ref([])
const permissions = ref([])
const users = ref([])
const loadingRoles = ref(false)
const loadingPerms = ref(false)

const activeRoleId = ref(null)
const activeTab = ref('perm')
const checkedKeys = ref([])
const roleFormVisible = ref(false)
const isEditRole = ref(false)
const roleForm = reactive({ id: null, name: '', description: '' })

const list = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const keyword = ref('')
const selectedIds = ref([])

const formVisible = ref(false)
const isEdit = ref(false)
const form = reactive({ id: '', name: '', code: '', description: '' })

onMounted(fetchList)

function fetchList() {
  listPermissions({ page: page.value - 1, size: size.value, keyword: keyword.value }).then(res => {
    if (res.code === 0) {
      const data = res.data
      list.value = data.content || data.items || []
      total.value = data.totalElements ?? data.total ?? 0
    } else {
      ElMessage.error(res.message || '获取权限失败')
    }
  }).catch(() => {
    ElMessage.error('获取权限失败')
  })
}

function handlePageChange(val) {
  page.value = val
  fetchList()
}

function openForm(edit, row) {
  isEdit.value = edit
  if (edit && row) {
    form.id = row.id
    form.name = row.name
    form.code = row.code
    form.description = row.description
  } else {
    form.id = ''
    form.name = ''
    form.code = ''
    form.description = ''
  }
  formVisible.value = true
}

function saveForm() {
  if (!form.name || !form.code) {
    ElMessage.error('名称和编码不能为空')
    return
  }
  const api = isEdit.value ? updatePermission(form.id, form) : createPermission(form)
  api.then(res => {
    if (res.code === 0) {
      ElMessage.success('保存成功')
      formVisible.value = false
      fetchList()
    } else {
      ElMessage.error(res.message || '保存失败')
    }
  }).catch(() => {
    ElMessage.error('保存失败')
  })
}

function handleDelete(row) {
  ElMessageBox.confirm('确定删除该权限吗？', '提示', { type: 'warning' })
    .then(() => deletePermission(row.id))
    .then(res => {
      if (res.code === 0) {
        ElMessage.success('已删除')
        fetchList()
      } else {
        ElMessage.error(res.message || '删除失败')
      }
    })
    .catch(() => {})
}

function handleDeleteBatch() {
  if (!selectedIds.value.length) return
  ElMessageBox.confirm('确定删除选中的权限吗？', '提示', { type: 'warning' })
    .then(() => batchDeletePermission(selectedIds.value))
    .then(res => {
      if (res.code === 0) {
        ElMessage.success('已删除')
        selectedIds.value = []
        fetchList()
      } else {
        ElMessage.error(res.message || '删除失败')
      }
    })
    .catch(() => {})
}
</script>

<style scoped>
.section-card {
  margin-top: 10px;
}
</style>
