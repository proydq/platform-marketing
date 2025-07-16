<template>
  <el-select v-model="model" style="width:100%">
    <el-option label="每天" value="daily" />
    <el-option label="每周" value="weekly" />
    <el-option label="每月" value="monthly" />
  </el-select>
</template>
<script setup>
const model = defineModel()
</script>
