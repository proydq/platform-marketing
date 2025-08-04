<template>
  <el-table :data="records" style="width:100%">
    <el-table-column prop="title" label="Subject" />
    <el-table-column prop="time" label="Time" width="180" />
    <el-table-column prop="recipients" label="Recipients" width="120">
      <template #default="{ row }">{{ row.recipients?.length || 0 }}</template>
    </el-table-column>
    <el-table-column prop="status" label="Status" width="120" />
  </el-table>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { getEmailRecords } from '@/api/email'

const records = ref([])

onMounted(async () => {
  try {
    const res = await getEmailRecords()
    records.value = res.data?.rows || res.data || []
  } catch (e) {
    records.value = []
  }
})
</script>
