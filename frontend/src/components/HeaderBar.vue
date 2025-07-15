<script setup>
import { useRoute } from 'vue-router'
import { useUserStore } from '../store'

const store = useUserStore()
const route = useRoute()

const titleMap = {
  '/dashboard': '控制台',
  '/customer-crawl': '客户抓取',
  '/customer-manage': '客户管理',
  '/content-generate': '内容生成',
  '/email-marketing': '邮件营销',
  '/social-media': '社交媒体',
  '/task-schedule': '任务调度',
  '/behavior-track': '行为追踪',
  '/reports': '报表分析',
  '/permission': '权限管理',
  '/settings': '系统设置'
}
</script>

<template>
  <div class="header">
    <h2 style="margin:0;color:#333">{{ titleMap[route.path] || '' }}</h2>
    <div class="user-info">
      <el-badge :value="3" type="primary">
        <el-button type="text" size="large"><span class="icon">🔔</span></el-button>
      </el-badge>
      <el-dropdown>
        <div style="display:flex;align-items:center;gap:8px;cursor:pointer;">
          <el-avatar :src="store.currentUser?.avatar" />
          <span>{{ store.currentUser?.name }}</span>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item>个人设置</el-dropdown-item>
            <el-dropdown-item @click="store.logout(); $router.push('/login')">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

