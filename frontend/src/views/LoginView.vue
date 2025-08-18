<template>
  <div class="login-container">
    <!-- 背景动画 -->
    <div class="background-animation">
      <div class="floating-shape shape1"></div>
      <div class="floating-shape shape2"></div>
      <div class="floating-shape shape3"></div>
      <div class="floating-shape shape4"></div>
    </div>
    
    <!-- 左侧品牌区域 -->
    <div class="brand-section">
      <div class="brand-content">
        <div class="logo-section">
          <div class="logo-icon">
            <div class="logo-text">📊</div>
          </div>
          <h1 class="brand-title">海外营销平台</h1>
          <p class="brand-subtitle">Overseas Marketing Platform</p>
        </div>
        <div class="feature-list">
          <div class="feature-item">
            <div class="feature-icon-text">📈</div>
            <span>智能数据分析</span>
          </div>
          <div class="feature-item">
            <div class="feature-icon-text">🔗</div>
            <span>多渠道整合</span>
          </div>
          <div class="feature-item">
            <div class="feature-icon-text">🤖</div>
            <span>AI营销助手</span>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 右侧登录区域 -->
    <div class="login-section">
      <div class="login-card">
        <div class="login-header">
          <h2 class="login-title">欢迎回来</h2>
          <p class="login-subtitle">请登录您的账户以继续</p>
        </div>
        <LoginForm />
        <div class="login-footer">
          <p class="footer-text">© 2025 海外营销平台. 保留所有权利.</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import LoginForm from '../components/LoginForm.vue'
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  position: relative;
  overflow: hidden;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

/* 背景动画 */
.background-animation {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 1;
}

.floating-shape {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: float 6s ease-in-out infinite;
}

.shape1 {
  width: 120px;
  height: 120px;
  top: 10%;
  left: 10%;
  animation-delay: 0s;
}

.shape2 {
  width: 80px;
  height: 80px;
  top: 60%;
  left: 20%;
  animation-delay: 2s;
}

.shape3 {
  width: 100px;
  height: 100px;
  top: 20%;
  right: 15%;
  animation-delay: 4s;
}

.shape4 {
  width: 60px;
  height: 60px;
  bottom: 20%;
  right: 25%;
  animation-delay: 1s;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px) rotate(0deg);
    opacity: 0.5;
  }
  50% {
    transform: translateY(-20px) rotate(180deg);
    opacity: 0.8;
  }
}

/* 左侧品牌区域 */
.brand-section {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  z-index: 2;
  padding: 2rem;
}

.brand-content {
  text-align: center;
  color: white;
  max-width: 400px;
}

.logo-section {
  margin-bottom: 3rem;
}

.logo-icon {
  margin-bottom: 1rem;
}

.logo-text {
  font-size: 4rem;
  line-height: 1;
}

.brand-title {
  font-size: 2.5rem;
  font-weight: 700;
  margin: 0 0 0.5rem 0;
  background: linear-gradient(45deg, #fff, #e3f2fd);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.brand-subtitle {
  font-size: 1.2rem;
  opacity: 0.9;
  margin: 0;
  font-weight: 300;
}

.feature-list {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.feature-item {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 1rem;
  font-size: 1.1rem;
  opacity: 0.9;
}

.feature-icon-text {
  font-size: 1.5rem;
  line-height: 1;
}

/* 右侧登录区域 */
.login-section {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  z-index: 2;
  padding: 2rem;
}

.login-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  padding: 3rem;
  width: 100%;
  max-width: 420px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.login-header {
  text-align: center;
  margin-bottom: 2rem;
}

.login-title {
  font-size: 2rem;
  font-weight: 700;
  color: #2c3e50;
  margin: 0 0 0.5rem 0;
}

.login-subtitle {
  color: #64748b;
  margin: 0;
  font-size: 0.95rem;
}

.login-footer {
  text-align: center;
  margin-top: 2rem;
  padding-top: 1.5rem;
  border-top: 1px solid #f1f5f9;
}

.footer-text {
  color: #94a3b8;
  font-size: 0.85rem;
  margin: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .login-container {
    flex-direction: column;
  }
  
  .brand-section {
    flex: none;
    min-height: 40vh;
    padding: 1rem;
  }
  
  .brand-title {
    font-size: 2rem;
  }
  
  .feature-list {
    flex-direction: row;
    justify-content: center;
    gap: 2rem;
  }
  
  .feature-item {
    flex-direction: column;
    gap: 0.5rem;
    font-size: 0.9rem;
  }
  
  .login-section {
    flex: 1;
    padding: 1rem;
  }
  
  .login-card {
    padding: 2rem;
  }
}

@media (max-width: 480px) {
  .brand-title {
    font-size: 1.8rem;
  }
  
  .login-card {
    padding: 1.5rem;
    margin: 0 1rem;
  }
  
  .login-title {
    font-size: 1.5rem;
  }
}
</style>
