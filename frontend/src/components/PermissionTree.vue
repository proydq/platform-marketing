<script setup>
const props = defineProps(['data'])
</script>

<template>
  <div class="permission-tree">
    <el-tree :data="props.data" node-key="id" show-checkbox default-expand-all />
  </div>
</template>
