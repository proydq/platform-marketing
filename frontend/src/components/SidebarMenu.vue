<script setup>
import { useRouter } from 'vue-router'
import { hasPermission } from '../composables/permission'

const router = useRouter()

const menuItems = [
  { path: '/dashboard', label: 'Dashboard' },
  { path: '/customer-crawl', label: 'Customer Crawl', permission: 'crawl' },
  { path: '/customer-manage', label: 'Customer Manage' },
  { path: '/permission', label: 'Permission', permission: 'admin' },
  { path: '/settings', label: 'Settings' },
  { path: '/content-generate', label: 'Content Generate' },
  { path: '/email-marketing', label: 'Email Marketing' },
  { path: '/social-media', label: 'Social Media' },
  { path: '/task-schedule', label: 'Task Schedule' },
  { path: '/behavior-track', label: 'Behavior Track' },
  { path: '/reports', label: 'Reports' },
]

const visibleItems = menuItems.filter(i => !i.permission || hasPermission(i.permission))

function navigate(path) {
  router.push(path)
}
</script>

<template>
  <el-menu class="el-menu-vertical-demo">
    <el-menu-item v-for="item in visibleItems" :key="item.path" @click="navigate(item.path)">
      {{ item.label }}
    </el-menu-item>
  </el-menu>
</template>
