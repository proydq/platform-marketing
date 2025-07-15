<script setup>
import { ref, onMounted } from 'vue'
import StatCard from '../components/StatCard.vue'
import TaskTable from '../components/TaskTable.vue'
import statsData from '../mock/stats.json'
import tasks from '../mock/crawlTasks.json'

const stats = ref({})
const taskList = ref([])

onMounted(() => {
  stats.value = statsData
  taskList.value = tasks
})
</script>

<template>
  <div>
    <div class="dashboard-cards">
      <StatCard title="总客户数" :value="stats.totalCustomers" />
      <StatCard title="今日邮件发送" :value="stats.emailsSent" />
      <StatCard title="邮件打开率" :value="stats.openRate + '%'" />
      <StatCard title="运行中任务" :value="stats.runningTasks" />
    </div>
    <div class="task-list">
      <TaskTable :items="taskList" />
    </div>
  </div>
</template>
