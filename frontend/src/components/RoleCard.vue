<script setup>
import { More, Edit, Delete } from '@element-plus/icons-vue'

const props = defineProps({
  role: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['edit', 'delete'])

function handleCommand(cmd) {
  if (cmd === 'edit') emit('edit', props.role)
  if (cmd === 'delete') emit('delete', props.role)
}
</script>

<template>
  <el-card class="role-card">
    <div class="role-header">
      <div class="info">
        <div class="role-name">{{ props.role.name }}</div>
        <div class="role-description">{{ props.role.description }}</div>
      </div>
      <el-dropdown trigger="click" @command="handleCommand">
        <el-button text class="more-btn">
          <el-icon><More /></el-icon>
        </el-button>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="edit"><el-icon><Edit /></el-icon> 编辑</el-dropdown-item>
            <el-dropdown-item command="delete"><el-icon><Delete /></el-icon> 删除</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
    <div class="permission-tags">
      <el-tag v-for="p in props.role.permissions" :key="p" size="small" type="info">{{ p }}</el-tag>
    </div>
    <div class="user-avatar-list">
      <el-tooltip v-for="u in props.role.users" :key="u.id" :content="u.name" placement="top">
        <el-avatar :src="u.avatar" :size="30">{{ u.name?.charAt(0) }}</el-avatar>
      </el-tooltip>
    </div>
  </el-card>
</template>

<style scoped>
.role-card {
  margin-bottom: 15px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}
.role-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}
.role-name {
  font-weight: 500;
  font-size: 16px;
}
.role-description {
  font-size: 13px;
  color: #666;
}
.permission-tags {
  margin-bottom: 8px;
}
.user-avatar-list :deep(.el-avatar) {
  margin-right: 4px;
}
.more-btn {
  padding: 0;
}
</style>
