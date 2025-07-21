<template>
  <div class="system-container">
    <div class="sidebar">
      <div class="logo">🌍 {{ t('sidebar.brand') }}</div>
      <el-menu :default-active="activeMenu" @select="handleSelect">
        <el-menu-item
          v-for="item in otherMenus"
          :key="item.path"
          :index="item.path"
        >
          <el-icon><component :is="item.icon" /></el-icon>
          <span>{{ item.title }}</span>
        </el-menu-item>
        <el-sub-menu index="system">
          <template #title>
            <el-icon><Setting /></el-icon>
            <span>系统管理</span>
          </template>
          <el-menu-item
            v-for="item in systemMenus"
            :key="item.path"
            :index="item.path"
          >
            <el-icon><component :is="item.icon" /></el-icon>
            <span>{{ item.title }}</span>
          </el-menu-item>
        </el-sub-menu>
      </el-menu>
    </div>
    <div class="main-content">
      <HeaderBar class="header" />
      <div class="content-area">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useI18n } from 'vue-i18n'
import HeaderBar from '../components/HeaderBar.vue'
import {
  Odometer,
  Promotion,
  Bell,
  QuestionFilled,
  Search,
  User,
  Message,
  Share,
  Clock,
  View,
  DataLine,
  Lock,
  UserFilled,
  Setting,
  EditPen,
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const { t } = useI18n()

const menuList = [
  { path: '/dashboard', title: '控制台', icon: Odometer },
  { path: '/campaign-center', title: '营销活动', icon: Promotion },
  { path: '/notifications', title: '通知中心', icon: Bell },
  { path: '/help-center', title: '帮助中心', icon: QuestionFilled },
  { path: '/customer-crawl', title: '客户采集', icon: Search },
  { path: '/customer-manage', title: '客户管理', icon: User },
  { path: '/email-marketing', title: '邮件营销', icon: Message },
  { path: '/social-media', title: '社交营销', icon: Share },
  { path: '/task-schedule', title: '任务调度', icon: Clock },
  { path: '/behavior-track', title: '行为追踪', icon: View },
  { path: '/reports', title: '报表统计', icon: DataLine },
  { path: '/permission', title: '权限管理', icon: Lock },
  { path: '/roles', title: '角色管理', icon: UserFilled },
  { path: '/users', title: '用户管理', icon: User },
  { path: '/settings', title: '系统设置', icon: Setting },
  { path: '/content-generate', title: '内容生成', icon: EditPen },
]

const systemPaths = ['/permission', '/roles', '/users', '/settings']
const systemMenus = menuList.filter((m) => systemPaths.includes(m.path))
const otherMenus = menuList.filter((m) => !systemPaths.includes(m.path))

const activeMenu = ref(route.path)
watch(
  () => route.path,
  (val) => {
    activeMenu.value = val
  }
)

function handleSelect(index) {
  router.push(index)
}
</script>
