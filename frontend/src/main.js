import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import './styles/globals.scss'
import './assets/styles/variables.scss'
import './assets/styles/utils.scss'
import i18n from './i18n'
import { useUserStore } from './store'

const app = createApp(App)
const pinia = createPinia()
app.use(pinia)

// 初始化用户状态
const userStore = useUserStore()

// 开发环境自动设置token和用户信息
if (import.meta.env.DEV && !userStore.token) {
  userStore.setToken('dev-token-123456')
  userStore.setDefaultUser()
} else if (userStore.token && !userStore.currentUser) {
  userStore.setDefaultUser()
}

app.use(router)
app.use(ElementPlus)
app.use(i18n)
app.mount('#app')
