<template>
  <el-dialog title="Create Crawl Task" v-model="visible">
    <p>New crawl task form here</p>
  </el-dialog>
</template>

<script setup>
import { ref } from 'vue'
const visible = ref(false)
</script>
