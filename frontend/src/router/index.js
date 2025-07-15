import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../store'

import LoginView from '../views/LoginView.vue'
import MainLayout from '../layouts/MainLayout.vue'
import DashboardView from '../views/DashboardView.vue'
import CustomerCrawlView from '../views/CustomerCrawlView.vue'
import PermissionView from '../views/PermissionView.vue'
import ComingSoon from '../views/ComingSoon.vue'

const routes = [
  { path: '/login', name: 'Login', component: LoginView },
  {
    path: '/',
    component: MainLayout,
    children: [
      { path: 'dashboard', name: 'Dashboard', component: DashboardView },
      { path: 'customer-crawl', name: 'CustomerCrawl', component: CustomerCrawlView },
      { path: 'customer-manage', name: 'CustomerManage', component: ComingSoon },
      { path: 'permission', name: 'Permission', component: PermissionView },
      { path: 'settings', name: 'Settings', component: ComingSoon },
      { path: 'content-generate', name: 'ContentGenerate', component: ComingSoon },
      { path: 'email-marketing', name: 'EmailMarketing', component: ComingSoon },
      { path: 'social-media', name: 'SocialMedia', component: ComingSoon },
      { path: 'task-schedule', name: 'TaskSchedule', component: ComingSoon },
      { path: 'behavior-track', name: 'BehaviorTrack', component: ComingSoon },
      { path: 'reports', name: 'Reports', component: ComingSoon },
    ],
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.beforeEach((to, from, next) => {
  const store = useUserStore()
  if (to.path !== '/login' && !store.isLoggedIn) {
    next('/login')
  } else {
    next()
  }
})

export default router
