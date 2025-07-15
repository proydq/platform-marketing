<script setup>
const props = defineProps(['role'])
</script>

<template>
  <el-card class="role-card">
    <div class="role-header">
      <div>
        <div class="role-name">{{ props.role.name }}</div>
        <div class="role-description">{{ props.role.description }}</div>
      </div>
    </div>
  </el-card>
</template>
