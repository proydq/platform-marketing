<script setup>
const props = defineProps(['items'])
</script>

<template>
  <el-table :data="props.items">
    <el-table-column prop="id" label="ID" />
    <el-table-column prop="name" label="Task" />
    <el-table-column prop="status" label="Status">
      <template #default="scope">
        <span :class="'status-badge status-' + scope.row.status">
          {{ scope.row.status }}
        </span>
      </template>
    </el-table-column>
    <el-table-column prop="progress" label="Progress">
      <template #default="scope">
        <div class="progress-ring">{{ scope.row.progress }}%</div>
      </template>
    </el-table-column>
  </el-table>
</template>
