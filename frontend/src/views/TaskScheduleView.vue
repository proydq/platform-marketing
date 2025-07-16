<script setup>
import { ref, onMounted } from 'vue'
import scheduleJson from '../mock/schedules.json'

const schedules = ref([])
const dialogVisible = ref(false)
const form = ref({ name: '', frequency: '每天', startTime: '' })

onMounted(() => { schedules.value = scheduleJson })

function openDialog() {
  form.value = { name: '', frequency: '每天', startTime: '' }
  dialogVisible.value = true
}
function addTask() {
  schedules.value.push({ ...form.value, status: 'pending' })
  dialogVisible.value = false
}
</script>

<template>
  <div class="page-wrapper">
    <div class="action-buttons">
      <el-button type="primary" @click="openDialog"><span class="icon">➕</span>新建任务</el-button>
    </div>

    <el-card class="chart-container">
      <el-table :data="schedules" style="width:100%">
        <el-table-column prop="name" label="任务名称" />
        <el-table-column prop="frequency" label="频率" width="120" />
        <el-table-column prop="startTime" label="开始时间" width="160" />
        <el-table-column prop="status" label="状态" width="120">
          <template #default="scope">
            <span :class="'status-badge status-' + (scope.row.status || 'pending')">{{ scope.row.status || 'pending' }}</span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" title="创建任务" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="任务名称">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="频率">
          <el-select v-model="form.frequency">
            <el-option label="每天" value="每天" />
            <el-option label="每周" value="每周" />
            <el-option label="每月" value="每月" />
          </el-select>
        </el-form-item>
        <el-form-item label="开始时间">
          <el-input v-model="form.startTime" placeholder="08:00" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="addTask">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>
