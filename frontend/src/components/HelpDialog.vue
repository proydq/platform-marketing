<template>
  <el-dialog title="Help" v-model="visible">
    <p>System help info</p>
  </el-dialog>
</template>

<script setup>
import { ref } from 'vue'
const visible = ref(false)
</script>
