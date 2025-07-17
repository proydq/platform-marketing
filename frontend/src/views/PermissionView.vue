<template>
  <div class="page-wrapper">
    <el-breadcrumb separator="/" class="mb-3">
      <el-breadcrumb-item>系统设置</el-breadcrumb-item>
      <el-breadcrumb-item>权限管理</el-breadcrumb-item>
    </el-breadcrumb>
    <el-tabs v-model="active" type="card">
      <el-tab-pane label="角色管理" name="roles">
        <RoleManagementTab v-if="active==='roles'" />
      </el-tab-pane>
      <el-tab-pane label="用户管理" name="users">
        <UserManagementTab v-if="active==='users'" />
      </el-tab-pane>
      <el-tab-pane label="权限配置" name="permissions">
        <PermissionTreeTab v-if="active==='permissions'" />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import RoleManagementTab from './permission/RoleManagementTab.vue'
import UserManagementTab from './permission/UserManagementTab.vue'
import PermissionTreeTab from './permission/PermissionTreeTab.vue'

const active = ref('roles')
</script>

<style scoped>
.page-wrapper {
  padding: 20px;
}
.mb-3 {
  margin-bottom: 15px;
}
</style>

