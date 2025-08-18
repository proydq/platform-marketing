<template>
  <div class="page-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="page-title">
        <span class="page-icon">🔐</span>
        权限管理
      </div>
      <div class="page-subtitle">
        管理系统用户角色、权限配置，确保系统安全和访问控制
      </div>
      <div class="page-actions">
        <el-button class="btn-secondary" @click="refreshData">
          <el-icon><Refresh /></el-icon>
          刷新数据
        </el-button>
      </div>
    </div>

    <!-- 标签页内容 -->
    <div class="data-table-wrapper">
      <el-tabs v-model="activeTab" class="enhanced-tabs">
        <el-tab-pane name="roles">
          <template #label>
            <span class="tab-label">
              <el-icon><UserFilled /></el-icon>
              角色管理
            </span>
          </template>
          <RoleManagement />
        </el-tab-pane>
        <el-tab-pane name="users">
          <template #label>
            <span class="tab-label">
              <el-icon><User /></el-icon>
              用户管理
            </span>
          </template>
          <UserManagement />
        </el-tab-pane>
        <el-tab-pane name="permission-setting">
          <template #label>
            <span class="tab-label">
              <el-icon><Setting /></el-icon>
              权限配置
            </span>
          </template>
          <PermissionConfig />
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { Refresh, UserFilled, User, Setting } from "@element-plus/icons-vue";
import RoleManagement from "@/views/permission/RoleManagement.vue";
import UserManagement from "@/views/permission/UserManagement.vue";
import PermissionConfig from "@/views/permission/PermissionConfig.vue";

const activeTab = ref("roles");

// 刷新数据功能
function refreshData() {
  // 这里可以添加刷新逻辑，或者触发子组件的刷新
  location.reload();
}
</script>

<style scoped>
.tab-label {
  display: flex;
  align-items: center;
  gap: var(--spacing-2);
}

.tab-label .el-icon {
  font-size: 14px;
}
</style>
