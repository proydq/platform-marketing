<script setup>
const props = defineProps(['items'])
</script>

<template>
  <el-table :data="props.items" style="width:100%">
    <el-table-column prop="name" label="任务名称" width="200" />
    <el-table-column prop="website" label="平台" width="120" />
    <el-table-column prop="status" label="状态" width="120">
      <template #default="scope">
        <span :class="'status-badge status-' + scope.row.status">
          {{ scope.row.status }}
        </span>
      </template>
    </el-table-column>
    <el-table-column prop="progress" label="进度" width="100">
      <template #default="scope">
        <div class="progress-ring">{{ scope.row.progress }}%</div>
      </template>
    </el-table-column>
    <el-table-column prop="createTime" label="创建时间" width="180" />
  </el-table>
</template>
