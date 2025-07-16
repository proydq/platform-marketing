<template>
  <div class="page-wrapper">

    <el-row class="action-buttons" justify="space-between" align="middle">
      <el-space>
        <el-button type="primary" @click="openAdd"><span class="icon">➕</span>新增客户</el-button>
        <el-button @click="handleImport"><span class="icon">📥</span>导入</el-button>
        <el-button type="success" @click="handleExport"><span class="icon">📤</span>导出</el-button>
      </el-space>
    </el-row>

    <el-card class="chart-container">
      <el-form inline>
        <el-form-item>
          <el-input v-model="keyword" placeholder="搜索名称或邮箱" prefix-icon="Search" />
        </el-form-item>
        <el-form-item>
          <el-select v-model="statusFilter" placeholder="状态" style="width:120px">
            <el-option label="全部" value="" />
            <el-option label="活跃" value="活跃" />
            <el-option label="停用" value="停用" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-select v-model="tagFilter" multiple placeholder="标签" style="width:200px">
            <el-option label="潜在" value="潜在" />
            <el-option label="VIP" value="VIP" />
            <el-option label="普通" value="普通" />
          </el-select>
        </el-form-item>
      </el-form>

      <el-table :data="pageData" style="width:100%; margin-top:10px;">
        <el-table-column prop="name" label="姓名" min-width="120" show-overflow-tooltip />
        <el-table-column prop="company" label="公司" min-width="160" show-overflow-tooltip />
        <el-table-column prop="email" label="邮箱" min-width="200" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <span :class="'status-badge status-' + (row.status === '活跃' ? 'success' : 'error')">{{ row.status }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="tags" label="标签" min-width="160">
          <template #default="{ row }">
            <el-tag v-for="tag in row.tags" :key="tag" size="small" style="margin-right:4px">{{ tag }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" align="center">
          <template #default="{ row }">
            <el-button type="text" @click="view(row)">查看</el-button>
            <el-button type="text" @click="openEdit(row)">编辑</el-button>
            <el-button type="text" style="color:#f56c6c" @click="remove(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div style="text-align:right;margin-top:10px;">
        <el-pagination background layout="prev, pager, next" :total="total" :page-size="pageSize" :current-page="page" @current-change="val => page.value = val" />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑客户' : '新增客户'" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="姓名">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="公司">
          <el-input v-model="form.company" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status">
            <el-option label="活跃" value="活跃" />
            <el-option label="停用" value="停用" />
          </el-select>
        </el-form-item>
        <el-form-item label="标签">
          <el-select v-model="form.tags" multiple style="width:100%">
            <el-option label="潜在" value="潜在" />
            <el-option label="VIP" value="VIP" />
            <el-option label="普通" value="普通" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveForm">确定</el-button>
      </template>
    </el-dialog>

    <el-drawer v-model="drawerVisible" title="客户详情" size="40%">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="姓名">{{ detail.name }}</el-descriptions-item>
        <el-descriptions-item label="公司">{{ detail.company }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ detail.email }}</el-descriptions-item>
        <el-descriptions-item label="来源">{{ detail.source }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ detail.status }}</el-descriptions-item>
        <el-descriptions-item label="标签">
          <el-tag v-for="t in detail.tags" :key="t" size="small" style="margin-right:4px">{{ t }}</el-tag>
        </el-descriptions-item>
      </el-descriptions>
      <h4 style="margin:20px 0 10px;">行为记录</h4>
      <el-table :data="[]" size="small">
        <el-table-column prop="time" label="时间" />
        <el-table-column prop="action" label="动作" />
      </el-table>
    </el-drawer>

  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import data from '../mock/customers.json'

const customers = ref([])
const keyword = ref('')
const statusFilter = ref('')
const tagFilter = ref([])
const dialogVisible = ref(false)
const drawerVisible = ref(false)
const isEdit = ref(false)
const form = ref({ name: '', company: '', email: '', status: '活跃', tags: [], source: '' })
const detail = ref({})

const page = ref(1)
const pageSize = 5

onMounted(() => {
  customers.value = data.map((c, idx) => ({ id: idx + 1, company: `Company ${idx+1}`, ...c }))
})

const filtered = computed(() => {
  let result = customers.value
  if (keyword.value) {
    result = result.filter(c => c.name.includes(keyword.value) || c.email.includes(keyword.value) || (c.company && c.company.includes(keyword.value)))
  }
  if (statusFilter.value) {
    result = result.filter(c => c.status === statusFilter.value)
  }
  if (tagFilter.value.length) {
    result = result.filter(c => tagFilter.value.every(t => c.tags.includes(t)))
  }
  return result
})

const total = computed(() => filtered.value.length)
const pageData = computed(() => {
  const start = (page.value - 1) * pageSize
  return filtered.value.slice(start, start + pageSize)
})

function openAdd() {
  isEdit.value = false
  form.value = { name: '', company: '', email: '', status: '活跃', tags: [], source: '' }
  dialogVisible.value = true
}

function openEdit(row) {
  isEdit.value = true
  form.value = { ...row }
  dialogVisible.value = true
}

function saveForm() {
  if (isEdit.value) {
    const idx = customers.value.findIndex(c => c.id === form.value.id)
    customers.value.splice(idx, 1, { ...form.value })
    ElMessage.success('更新成功')
  } else {
    const id = customers.value.length ? Math.max(...customers.value.map(c => c.id)) + 1 : 1
    customers.value.push({ ...form.value, id })
    ElMessage.success('创建成功')
  }
  dialogVisible.value = false
}

function remove(row) {
  ElMessageBox.confirm('确定删除该客户吗?', '提示', { type: 'warning' })
    .then(() => {
      customers.value = customers.value.filter(c => c.id !== row.id)
      ElMessage.success('已删除')
    })
    .catch(() => {})
}

function view(row) {
  detail.value = row
  drawerVisible.value = true
}

function handleImport() {
  ElMessage.info('导入功能开发中...')
}
function handleExport() {
  ElMessage.info('导出功能开发中...')
}
</script>
