<script setup>
defineProps(['title', 'value'])
</script>

<template>
  <el-card class="stat-card">
    <div>{{ title }}</div>
    <div>{{ value }}</div>
  </el-card>
</template>
