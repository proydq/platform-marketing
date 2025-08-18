<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import { useUserStore } from "../store";
import { useI18n } from "vue-i18n";
import { ElMessage } from "element-plus";
import { login as loginApi, getCurrentUser } from "../api/auth";
// 移除图标导入，使用emoji代替

const router = useRouter();
const store = useUserStore();
const { t } = useI18n();

const loading = ref(false);
const form = ref({
  username: "",
  password: "",
  remember: false,
});

async function submit() {
  if (!form.value.username || !form.value.password) {
    ElMessage.warning("请输入用户名和密码");
    return;
  }
  
  loading.value = true;
  try {
    const res = await loginApi({
      username: form.value.username,
      password: form.value.password,
    });
    const token = res.data.token;
    store.setToken(token);
    const userRes = await getCurrentUser();
    store.setUser(userRes.data);
    ElMessage.success("登录成功");
    router.push("/dashboard");
  } catch (e) {
    ElMessage.error("登录失败，请检查用户名和密码");
  } finally {
    loading.value = false;
  }
}

function fillDemoAccount() {
  form.value.username = "admin";
  form.value.password = "admin123";
  ElMessage.info("已填入演示账户信息");
}
</script>

<template>
  <el-form class="login-form" @submit.prevent="submit" :loading="loading">
    <el-form-item prop="username">
      <el-input
        v-model="form.username"
        :placeholder="$t('login.username')"
        prefix-icon="User"
        size="large"
        clearable
      />
    </el-form-item>
    <el-form-item prop="password">
      <el-input
        v-model="form.password"
        type="password"
        :placeholder="$t('login.password')"
        prefix-icon="Lock"
        size="large"
        show-password
        clearable
      />
    </el-form-item>
    <div class="login-options">
      <el-checkbox v-model="form.remember">{{
        $t("login.remember")
      }}</el-checkbox>
      <a href="#" class="forgot-password">{{ $t("login.forgot") }}</a>
    </div>
    <el-button 
      class="login-btn" 
      type="primary" 
      native-type="submit"
      size="large"
      :loading="loading"
      :disabled="!form.username || !form.password"
    >
      <template v-if="!loading">
        <span class="btn-icon">→</span>
        {{ $t("login.submit") }}
      </template>
      <template v-else>
        登录中...
      </template>
    </el-button>
    
    <!-- 演示账户信息 -->
    <div class="demo-info">
      <el-divider>演示账户</el-divider>
      <div class="demo-account" @click="fillDemoAccount">
        <span>👤</span>
        <span>点击填入演示账户</span>
      </div>
    </div>
  </el-form>
</template>

<style scoped>
.login-form {
  width: 100%;
}

:deep(.el-form-item) {
  margin-bottom: 1.5rem;
}

:deep(.el-input__wrapper) {
  border-radius: 12px;
  padding: 0 15px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  border: 1px solid #e2e8f0;
  transition: all 0.3s ease;
}

:deep(.el-input__wrapper:hover) {
  border-color: #667eea;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.15);
}

:deep(.el-input__wrapper.is-focus) {
  border-color: #667eea;
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.2);
}

:deep(.el-input__inner) {
  font-size: 1rem;
  height: 50px;
  line-height: 50px;
}

:deep(.el-input__prefix) {
  color: #94a3b8;
}

.login-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
  font-size: 0.9rem;
}

:deep(.el-checkbox) {
  color: #64748b;
}

:deep(.el-checkbox__label) {
  color: #64748b;
  font-weight: 400;
}

.forgot-password {
  color: #667eea;
  text-decoration: none;
  font-weight: 500;
  transition: color 0.3s ease;
}

.forgot-password:hover {
  color: #5a67d8;
  text-decoration: underline;
}

.login-btn {
  width: 100%;
  height: 50px;
  border-radius: 12px;
  font-size: 1rem;
  font-weight: 600;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  transition: all 0.3s ease;
  margin-bottom: 1.5rem;
}

.login-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.4);
}

.login-btn:active:not(:disabled) {
  transform: translateY(0);
}

.login-btn:disabled {
  background: #e2e8f0;
  color: #94a3b8;
  cursor: not-allowed;
}

.btn-icon {
  margin-right: 8px;
}

.demo-info {
  margin-top: 1.5rem;
}

:deep(.el-divider__text) {
  font-size: 0.85rem;
  color: #94a3b8;
  background: rgba(255, 255, 255, 0.95);
}

.demo-account {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 12px;
  border: 1px dashed #d1d5db;
  border-radius: 8px;
  cursor: pointer;
  color: #64748b;
  font-size: 0.9rem;
  transition: all 0.3s ease;
}

.demo-account:hover {
  border-color: #667eea;
  color: #667eea;
  background: rgba(102, 126, 234, 0.05);
}

/* 加载状态样式 */
:deep(.el-loading-mask) {
  border-radius: 12px;
}
</style>
