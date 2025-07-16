<script setup>
import ProgressRing from './ProgressRing.vue'
const props = defineProps(['items'])
</script>

<template>
  <el-table :data="props.items">
    <el-table-column prop="id" label="ID" width="80" />
    <el-table-column prop="name" label="Task" min-width="160" show-overflow-tooltip />
    <el-table-column prop="status" label="Status" width="120" align="center">
      <template #default="scope">
        <span :class="'status-badge status-' + scope.row.status">
          {{ scope.row.status }}
        </span>
      </template>
    </el-table-column>
    <el-table-column prop="progress" label="Progress" width="120" align="center">
      <template #default="scope">
        <ProgressRing :percentage="scope.row.progress" />
      </template>
    </el-table-column>
  </el-table>
</template>
