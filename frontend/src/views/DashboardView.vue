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
      <StatCard title="Tasks" :value="stats.tasks" />
      <StatCard title="Users" :value="stats.users" />
      <StatCard title="Roles" :value="stats.roles" />
    </div>
    <div class="task-list">
      <TaskTable :items="taskList" />
    </div>
  </div>
</template>
