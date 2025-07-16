<script setup>
defineProps(['title', 'value'])
</script>

<template>
  <el-card class="stat-card">
    <div class="stat-number">{{ value }}</div>
    <div class="stat-label">{{ title }}</div>
  </el-card>
</template>
