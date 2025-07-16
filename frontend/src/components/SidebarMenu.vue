<script setup>
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { hasPermission } from '../composables/permission'

const router = useRouter()
const route = useRoute()

const activeMenu = ref(route.name || 'Dashboard')

const menuItems = [
  { name: 'Dashboard', label: '控制台', icon: '📊' },
  { name: 'CustomerCrawl', label: '客户抓取', icon: '🔍', permission: 'customer:crawl' },
  { name: 'CustomerManage', label: '客户管理', icon: '👥', permission: 'customer:manage' },
  { name: 'ContentGenerate', label: '内容生成', icon: '✍️', permission: 'content:generate' },
  { name: 'EmailMarketing', label: '邮件营销', icon: '📧', permission: 'email:send' },
  { name: 'SocialMedia', label: '社交媒体', icon: '📱', permission: 'social:manage' },
  { name: 'TaskSchedule', label: '任务调度', icon: '⏰', permission: 'task:schedule' },
  { name: 'BehaviorTrack', label: '行为追踪', icon: '📈', permission: 'behavior:track' },
  { name: 'Reports', label: '报表分析', icon: '📋', permission: 'report:view' },
  { name: 'Permission', label: '权限管理', icon: '🔐', permission: 'system:permission' },
  { name: 'Settings', label: '系统设置', icon: '⚙️', permission: 'system:setting' }
]

const visibleItems = menuItems.filter(i => !i.permission || hasPermission(i.permission))

function handleMenuSelect(index) {
  activeMenu.value = index
  router.push({ name: index })
}
</script>

<template>
  <div class="sidebar">
    <div class="logo">🌍 海外营销系统</div>
    <el-menu :default-active="activeMenu" @select="handleMenuSelect">
      <el-menu-item v-for="item in visibleItems" :key="item.name" :index="item.name">
        <span class="icon">{{ item.icon }}</span>
        <span>{{ item.label }}</span>
      </el-menu-item>
    </el-menu>
  </div>
</template>
