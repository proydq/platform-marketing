<script setup>
const props = defineProps(['role'])
</script>

<template>
  <el-card class="role-card">
    <div>{{ props.role.name }}</div>
  </el-card>
</template>
