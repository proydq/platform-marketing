<template>
  <el-dialog title="Create User" v-model="visible">
    <p>Create user form here</p>
  </el-dialog>
</template>

<script setup>
import { ref } from 'vue'
const visible = ref(false)
</script>
