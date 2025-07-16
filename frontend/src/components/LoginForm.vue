<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../store'
import { useI18n } from 'vue-i18n'

const router = useRouter()
const store = useUserStore()
const { t } = useI18n()

const form = ref({
  username: '',
  password: '',
  remember: false
})

function submit() {
  store.login({ name: form.value.username, permissions: ['*'] })
  router.push('/dashboard')
}
</script>

<template>
  <el-form class="login-form" @submit.prevent="submit">
    <el-form-item prop="username">
      <el-input v-model="form.username" :placeholder="$t('login.username')" prefix-icon="User" />
    </el-form-item>
    <el-form-item prop="password">
      <el-input v-model="form.password" type="password" :placeholder="$t('login.password')" prefix-icon="Lock" />
    </el-form-item>
    <div class="login-options">
      <el-checkbox v-model="form.remember">{{ $t('login.remember') }}</el-checkbox>
      <a href="#" class="forgot-password">{{ $t('login.forgot') }}</a>
    </div>
    <el-button class="login-btn" type="primary" native-type="submit">{{ $t('login.submit') }}</el-button>
  </el-form>
</template>
