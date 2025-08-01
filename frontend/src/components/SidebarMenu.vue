<script setup>
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { hasPermission } from '../composables/permission'
import { useI18n } from 'vue-i18n'

const router = useRouter()
const route = useRoute()

const activeMenu = ref(route.name || 'Dashboard')
const { t } = useI18n()

const menuItems = [
  { name: 'Dashboard', label: 'menu.dashboard', icon: '📊' },
  { name: 'CustomerCrawl', label: 'menu.lead', icon: '🔍', permission: 'customer:crawl' },
  { name: 'CustomerManage', label: 'menu.customer', icon: '👥', permission: 'customer:manage' },
  { name: 'ContentGenerate', label: 'menu.content', icon: '✍️', permission: 'content:generate' },
  { name: 'EmailMarketing', label: 'menu.email', icon: '📧', permission: 'email:send' },
  { name: 'SocialMedia', label: 'menu.social', icon: '📱', permission: 'social:manage' },
  { name: 'TaskSchedule', label: 'menu.task', icon: '⏰', permission: 'task:schedule' },
  { name: 'BehaviorTrack', label: 'menu.behavior', icon: '📈', permission: 'behavior:track' },
  { name: 'Reports', label: 'menu.reports', icon: '📋', permission: 'report:view' },
  { name: 'Permission', label: 'menu.permission', icon: '🔐', permission: 'system:permission' },
  { name: 'Settings', label: 'menu.system', icon: '⚙️', permission: 'system:setting' },
  { name: 'CampaignCenter', label: 'menu.campaign', icon: '🎯' },
  { name: 'NotificationCenter', label: 'menu.notification', icon: '🔔' },
  { name: 'HelpCenter', label: 'menu.help', icon: '❓' }
]

const visibleItems = menuItems.filter(i => !i.permission || hasPermission(i.permission))

function handleMenuSelect(index) {
  activeMenu.value = index
  router.push({ name: index })
}
</script>

<template>
  <div class="sidebar">
    <div class="logo">🌍 {{ t('sidebar.brand') }}</div>
    <el-menu :default-active="activeMenu" @select="handleMenuSelect">
      <el-menu-item v-for="item in visibleItems" :key="item.name" :index="item.name">
        <span class="icon">{{ item.icon }}</span>
        <span>{{ t(item.label) }}</span>
      </el-menu-item>
    </el-menu>
  </div>
</template>
