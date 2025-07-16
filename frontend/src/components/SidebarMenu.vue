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
  { name: 'Dashboard', label: 'sidebar.dashboard', icon: '📊' },
  { name: 'CustomerCrawl', label: 'sidebar.customerCrawl', icon: '🔍', permission: 'customer:crawl' },
  { name: 'CustomerManage', label: 'sidebar.customerManage', icon: '👥', permission: 'customer:manage' },
  { name: 'ContentGenerate', label: 'sidebar.contentGenerate', icon: '✍️', permission: 'content:generate' },
  { name: 'EmailMarketing', label: 'sidebar.emailMarketing', icon: '📧', permission: 'email:send' },
  { name: 'SocialMedia', label: 'sidebar.socialMedia', icon: '📱', permission: 'social:manage' },
  { name: 'TaskSchedule', label: 'sidebar.taskSchedule', icon: '⏰', permission: 'task:schedule' },
  { name: 'BehaviorTrack', label: 'sidebar.behaviorTrack', icon: '📈', permission: 'behavior:track' },
  { name: 'Reports', label: 'sidebar.reports', icon: '📋', permission: 'report:view' },
  { name: 'Permission', label: 'sidebar.permission', icon: '🔐', permission: 'system:permission' },
  { name: 'Settings', label: 'sidebar.settings', icon: '⚙️', permission: 'system:setting' },
  { name: 'CampaignCenter', label: 'sidebar.campaignCenter', icon: '🎯' },
  { name: 'NotificationCenter', label: 'sidebar.notificationCenter', icon: '🔔' },
  { name: 'HelpCenter', label: 'sidebar.helpCenter', icon: '❓' }
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
