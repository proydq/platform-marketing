<script setup>
import { ref, onMounted } from 'vue'
import scheduleData from '../mock/schedules.json'
import ScheduleSelector from '../components/ScheduleSelector.vue'
const form = ref({ name: '', frequency: '每天', startTime: '' })
const schedules = ref([])

onMounted(() => {
  schedules.value = scheduleData
})

function addTask() {
  schedules.value.push({ ...form.value })
  form.value = { name: '', frequency: '每天', startTime: '' }
}
</script>

<template>
  <el-card>
    <h2>📄 任务调度页面</h2>
    <el-form :model="form" inline class="action-buttons">
      <el-form-item label="任务名称">
        <el-input v-model="form.name" />
      </el-form-item>
      <el-form-item label="频率">
        <ScheduleSelector v-model="form.frequency" />
      </el-form-item>
      <el-form-item label="开始时间">
        <el-input v-model="form.startTime" placeholder="例如 08:00" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="addTask">添加</el-button>
      </el-form-item>
    </el-form>
    <el-table :data="schedules" style="width:100%">
      <el-table-column prop="name" label="任务名称" />
      <el-table-column prop="frequency" label="频率" width="120" />
      <el-table-column prop="startTime" label="开始时间" width="160" />
    </el-table>
  </el-card>
</template>
