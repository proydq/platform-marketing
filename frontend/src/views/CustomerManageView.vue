<script setup>
import { ref, onMounted, computed } from 'vue'
import customersJson from '../mock/customers.json'

const customers = ref([])
const page = ref(1)
const pageSize = 5
const drawerVisible = ref(false)
const current = ref({})
const tagFilter = ref('')

onMounted(() => {
  customers.value = customersJson
})

const filtered = computed(() => {
  if (!tagFilter.value) return customers.value
  return customers.value.filter(c => c.tags.includes(tagFilter.value))
})

const total = computed(() => filtered.value.length)
const pageData = computed(() => {
  const start = (page.value - 1) * pageSize
  return filtered.value.slice(start, start + pageSize)
})

function handleImport() { console.log('import') }
function handleExport() { console.log('export') }
function handleClean() { console.log('clean') }
function showDetail(row) { current.value = row; drawerVisible.value = true }
</script>

<template>
  <el-card>
    <h2>📄 客户管理</h2>
    <div class="action-buttons">
      <el-button type="primary" @click="handleImport"><span class="icon">📥</span>导入客户</el-button>
      <el-button type="success" @click="handleExport"><span class="icon">📤</span>导出客户</el-button>
      <el-button type="warning" @click="handleClean"><span class="icon">🧹</span>数据清洗</el-button>
    </div>
    <div style="margin-bottom:10px;">
      <el-select v-model="tagFilter" placeholder="标签筛选" clearable style="width:200px">
        <el-option label="潜在" value="潜在" />
        <el-option label="VIP" value="VIP" />
        <el-option label="普通" value="普通" />
      </el-select>
    </div>
    <el-table :data="pageData" style="width:100%">
      <el-table-column prop="name" label="姓名" width="120" />
      <el-table-column prop="email" label="邮箱" width="200" />
      <el-table-column prop="source" label="来源" width="120" />
      <el-table-column prop="tags" label="标签" width="180">
        <template #default="scope">
          <el-tag v-for="tag in scope.row.tags" :key="tag" size="small" type="info">{{ tag }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.status === '活跃' ? 'success' : 'info'" size="small">{{ scope.row.status }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="80">
        <template #default="scope">
          <el-button type="text" @click="showDetail(scope.row)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>
    <div style="margin-top:10px;text-align:right;">
      <el-pagination background layout="prev, pager, next" :total="total" :page-size="pageSize" :current-page="page" @current-change="val => page.value = val" />
    </div>
  </el-card>

  <el-drawer v-model="drawerVisible" title="客户详情" size="30%">
    <p>姓名：{{ current.name }}</p>
    <p>邮箱：{{ current.email }}</p>
    <p>来源：{{ current.source }}</p>
    <p>标签：<el-tag v-for="t in current.tags" :key="t" size="small" style="margin-right:4px;">{{ t }}</el-tag></p>
  </el-drawer>
</template>
