<template>
  <div style="padding: 20px">
    <el-tabs v-model="activeTab">
      <el-tab-pane label="角色管理" name="roles">
        <RoleManagement />
      </el-tab-pane>
      <el-tab-pane label="用户管理" name="users">
        <UserManagement />
      </el-tab-pane>
      <el-tab-pane label="权限配置" name="permission-setting">
        <PermissionConfig />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref } from "vue";
import RoleManagement from "@/views/permission/RoleManagement.vue";
import UserManagement from "@/views/permission/UserManagement.vue";
import PermissionConfig from "@/views/permission/PermissionConfig.vue";

const activeTab = ref("roles");
</script>

<style scoped></style>
