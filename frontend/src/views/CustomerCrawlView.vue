<script setup>
import { ref, onMounted } from 'vue'
import QuickCrawlForm from '../components/QuickCrawlForm.vue'
import CrawlTaskTable from '../components/CrawlTaskTable.vue'
import preview from '../mock/previewData.json'
import tasks from '../mock/crawlTasks.json'

const previewData = ref([])
const taskList = ref([])

onMounted(() => {
  previewData.value = preview
  taskList.value = tasks
})
</script>

<template>
  <QuickCrawlForm :preview-data="previewData" />
  <CrawlTaskTable :items="taskList" />
</template>
