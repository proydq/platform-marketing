<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../store'

const username = ref('')
const password = ref('')
const router = useRouter()
const store = useUserStore()

function submit() {
  store.login({ name: username.value, permissions: ['crawl', 'admin'] })
  router.push('/dashboard')
}
</script>

<template>
  <el-form @submit.prevent="submit">
    <el-form-item label="Username">
      <el-input v-model="username" />
    </el-form-item>
    <el-form-item label="Password">
      <el-input v-model="password" type="password" />
    </el-form-item>
    <el-form-item>
      <el-button type="primary" native-type="submit">Login</el-button>
    </el-form-item>
  </el-form>
</template>
