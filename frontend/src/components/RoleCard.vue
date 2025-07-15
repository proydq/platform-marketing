<script setup>
const props = defineProps(['role'])
</script>

<template>
  <el-card class="role-card">
    <div class="role-header">
      <div>
        <div class="role-name">{{ props.role.name }}</div>
        <div class="role-description">{{ props.role.description }}</div>
      </div>
    </div>
    <div class="permission-tags">
      <el-tag v-for="p in props.role.permissions" :key="p" size="small" type="info">{{ p }}</el-tag>
    </div>
    <div class="user-avatar-list">
      <el-avatar v-for="u in props.role.users" :key="u.id" :src="u.avatar" :size="30" />
    </div>
  </el-card>
</template>
