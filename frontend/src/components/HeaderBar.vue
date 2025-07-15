<script setup>
import { useUserStore } from '../store'
const store = useUserStore()
</script>

<template>
  <el-header class="header">
    <div class="user-info">
      {{ store.currentUser?.name }}
    </div>
  </el-header>
</template>

