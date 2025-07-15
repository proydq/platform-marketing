<script setup>
const props = defineProps(['items'])
</script>

<template>
  <el-table :data="props.items">
    <el-table-column prop="id" label="ID" />
    <el-table-column prop="name" label="User" />
  </el-table>
</template>
