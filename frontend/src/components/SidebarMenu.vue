<script setup>
import { useRouter, useRoute } from 'vue-router'
import { hasPermission } from '../composables/permission'

const router = useRouter()
const route = useRoute()

const menuItems = [
  { path: '/dashboard', label: '控制台', icon: '📊' },
  { path: '/customer-crawl', label: '客户抓取', icon: '🔍', permission: 'customer:crawl' },
  { path: '/customer-manage', label: '客户管理', icon: '👥', permission: 'customer:manage' },
  { path: '/content-generate', label: '内容生成', icon: '✍️', permission: 'content:generate' },
  { path: '/email-marketing', label: '邮件营销', icon: '📧', permission: 'email:send' },
  { path: '/social-media', label: '社交媒体', icon: '📱', permission: 'social:manage' },
  { path: '/task-schedule', label: '任务调度', icon: '⏰', permission: 'task:schedule' },
  { path: '/behavior-track', label: '行为追踪', icon: '📈', permission: 'behavior:track' },
  { path: '/reports', label: '报表分析', icon: '📋', permission: 'report:view' },
  { path: '/permission', label: '权限管理', icon: '🔐', permission: 'system:permission' },
  { path: '/settings', label: '系统设置', icon: '⚙️', permission: 'system:setting' }
]

const visibleItems = menuItems.filter(i => !i.permission || hasPermission(i.permission))

function navigate(path) {
  router.push(path)
}
</script>

<template>
  <el-menu :default-active="route.path" @select="navigate" class="sidebar">
    <el-menu-item v-for="item in visibleItems" :key="item.path" :index="item.path">
      <span class="icon">{{ item.icon }}</span>
      <span>{{ item.label }}</span>
    </el-menu-item>
  </el-menu>
</template>
