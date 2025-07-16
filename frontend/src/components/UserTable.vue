<script setup>
const props = defineProps(['items'])
</script>

<template>
  <el-table :data="props.items" style="width:100%">
    <el-table-column prop="name" label="用户名" width="150" show-overflow-tooltip>
      <template #default="scope">
        <div style="display:flex;align-items:center;gap:10px;">
          <el-avatar :size="30" :src="scope.row.avatar">{{ scope.row.name.charAt(0) }}</el-avatar>
          <span>{{ scope.row.name }}</span>
        </div>
      </template>
    </el-table-column>
    <el-table-column prop="email" label="邮箱" min-width="200" show-overflow-tooltip />
    <el-table-column prop="role" label="角色" width="120">
      <template #default="scope">
        <el-tag type="primary" size="small">{{ scope.row.role }}</el-tag>
      </template>
    </el-table-column>
    <el-table-column prop="department" label="部门" width="120" />
    <el-table-column prop="status" label="状态" width="100">
      <template #default="scope">
        <el-tag :type="scope.row.status === '在线' ? 'success' : 'info'" size="small">{{ scope.row.status }}</el-tag>
      </template>
    </el-table-column>
    <el-table-column prop="lastLogin" label="最后登录" width="180" />
  </el-table>
</template>
