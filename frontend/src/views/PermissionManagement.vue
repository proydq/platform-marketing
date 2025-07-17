<template>
  <div class="page-wrapper">
    <el-tabs v-model="active" class="mb-3">
      <el-tab-pane label="用户模块" name="user">
        <UserPermissionTab v-if="active === 'user'" />
      </el-tab-pane>
      <el-tab-pane label="系统模块" name="system">
        <SystemPermissionTab v-if="active === 'system'" />
      </el-tab-pane>
      <el-tab-pane label="内容模块" name="content">
        <ContentPermissionTab v-if="active === 'content'" />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import UserPermissionTab from './permission/UserPermissionTab.vue'
import SystemPermissionTab from './permission/SystemPermissionTab.vue'
import ContentPermissionTab from './permission/ContentPermissionTab.vue'

const active = ref('user')
</script>

<style scoped>
.page-wrapper {
  padding: 20px;
}
.mb-3 {
  margin-bottom: 15px;
}
</style>
