import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    currentUser: null,
  }),
  getters: {
    isLoggedIn: (state) => !!state.token,
  },
  actions: {
    setToken(token) {
      this.token = token
      localStorage.setItem('token', token)
      
      // 如果没有用户信息，设置默认用户（开发用）
      if (!this.currentUser && token) {
        this.setDefaultUser()
      }
    },
    setUser(user) {
      this.currentUser = user
    },
    setDefaultUser() {
      // 开发环境默认用户权限
      this.currentUser = {
        id: 'user-001',
        username: 'admin',
        roleName: '超级管理员',
        permissions: [
          // 目录级权限（用于菜单显示）
          'dashboard:dir',
          'product:dir', 
          'customer-collect:dir',
          'customer:dir',
          'content-generation:dir',
          'campaign:dir',
          'behavior-log:dir',
          'permission:dir',
          'menu:dir',
          'settings:dir',
          
          // 具体权限
          'permission:list',
          'user:add',
          'role:view',
          'user:list',
          'user:update',
          'role:list',
          'user:create',
          'role:create',
          'role:update',
          'role:delete',
          'user:delete',
          'user:detail',
          'customer:list',
          'customer:view',
          'customer:create',
          'customer:update',
          'customer:delete',
          'customer:status',
          'customer-collect:list',
          'customer-collect:view',
          'customer-collect:create',
          'customer-collect:delete',
          'customer-collect:batch-delete',
          'product:upload',
          'product:view',
          'product:create',
          'product:edit',
          'content-generation:list',
          'content-generation:view',
          'content-generation:create',
          'content-generation:update',
          'content-generation:delete',
          'behavior-log:list',
          'behavior-log:view',
          // 爬虫数据管理权限
          'crawl:data:view',
          'crawl:data:export',
          'crawl:data:delete',
          'crawl:data:manage',
          // 渠道相关权限
          'campaign:create',
          'campaign:edit',
          'campaign:delete',
          'campaign:publish',
          'campaign:view',
          'channel:manage',
          // AI助手权限
          'ai:assistant',
          'ai:content:generate',
          'ai:keywords:analyze'
        ]
      }
    },
    logout() {
      this.token = ''
      this.currentUser = null
      localStorage.removeItem('token')
    },
  },
})
