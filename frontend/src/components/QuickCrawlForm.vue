<script setup>
const props = defineProps(['previewData'])
</script>

<template>
  <el-card>
    <div>Quick Crawl Form (preview: {{ props.previewData.length }})</div>
  </el-card>
</template>
