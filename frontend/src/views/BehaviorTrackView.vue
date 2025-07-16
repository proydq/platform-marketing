<script setup>
import { ref, onMounted } from 'vue'
import behaviors from '../mock/behaviorLog.json'
const list = ref([])

onMounted(() => {
  list.value = behaviors
})
</script>

<template>
  <el-card>
    <h2>📄 行为追踪页面</h2>
    <el-table :data="list" style="width:100%">
      <el-table-column prop="customerId" label="客户ID" width="100" />
      <el-table-column prop="action" label="操作" width="120" />
      <el-table-column prop="time" label="时间" width="180" />
      <el-table-column prop="ip" label="IP" width="150" />
      <el-table-column prop="page" label="来源页面" />
    </el-table>
  </el-card>
</template>
