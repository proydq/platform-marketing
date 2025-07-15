<script setup>
import { useUserStore } from '../store'
const store = useUserStore()
</script>

<template>
  <el-header class="header-bar">
    <div>{{ store.currentUser?.name }}</div>
  </el-header>
</template>

<style scoped>
.header-bar {
  background: #f5f5f5;
  padding: 10px;
}
</style>
