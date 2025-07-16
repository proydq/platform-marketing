<script setup>
import { ref, onMounted, computed } from 'vue'
import customersData from '../mock/customers.json'

const customers = ref([])
const page = ref(1)
const pageSize = 5

onMounted(() => {
  customers.value = customersData
})

const total = computed(() => customers.value.length)
const pageData = computed(() => {
  const start = (page.value - 1) * pageSize
  return customers.value.slice(start, start + pageSize)
})

function handleImport() {
  console.log('import customers')
}
function handleExport() {
  console.log('export customers')
}
function handleClean() {
  console.log('clean data')
}
</script>

<template>
  <el-card>
    <h2>📄 客户管理页面</h2>
    <div class="action-buttons">
      <el-button type="primary" @click="handleImport">导入客户</el-button>
      <el-button type="success" @click="handleExport">导出客户</el-button>
      <el-button type="warning" @click="handleClean">数据清洗</el-button>
    </div>
    <el-table :data="pageData" style="width:100%">
      <el-table-column prop="name" label="姓名" width="120" />
      <el-table-column prop="email" label="邮箱" width="200" />
      <el-table-column prop="source" label="来源" width="120" />
      <el-table-column prop="tags" label="标签">
        <template #default="scope">
          <el-tag v-for="tag in scope.row.tags" :key="tag" size="small" type="info">{{ tag }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.status === '活跃' ? 'success' : 'info'" size="small">{{ scope.row.status }}</el-tag>
        </template>
      </el-table-column>
    </el-table>
    <div style="margin-top:10px;text-align:right;">
      <el-pagination
        background
        layout="prev, pager, next"
        :total="total"
        :page-size="pageSize"
        :current-page="page"
        @current-change="val => page.value = val"
      />
    </div>
  </el-card>
</template>

