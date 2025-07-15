<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../store'

const router = useRouter()
const store = useUserStore()

const form = ref({
  username: '',
  password: '',
  remember: false
})

function submit() {
  store.login({ name: form.value.username, permissions: ['customer:crawl', 'system:permission'] })
  router.push('/dashboard')
}
</script>

<template>
  <el-form class="login-form" @submit.prevent="submit">
    <el-form-item prop="username">
      <el-input v-model="form.username" placeholder="用户名" prefix-icon="User" />
    </el-form-item>
    <el-form-item prop="password">
      <el-input v-model="form.password" type="password" placeholder="密码" prefix-icon="Lock" />
    </el-form-item>
    <div class="login-options">
      <el-checkbox v-model="form.remember">记住我</el-checkbox>
      <a href="#" class="forgot-password">忘记密码？</a>
    </div>
    <el-button class="login-btn" type="primary" native-type="submit">登录</el-button>
  </el-form>
</template>
