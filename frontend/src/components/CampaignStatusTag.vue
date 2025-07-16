<template>
  <el-tag :type="typeMap[status] || 'info'" class="campaign-card">{{ status }}</el-tag>
</template>
<script setup>
const props = defineProps({ status: { type: String, default: '' } })
const typeMap = { success: 'success', running: 'primary', pending: 'warning', error: 'danger' }
</script>
