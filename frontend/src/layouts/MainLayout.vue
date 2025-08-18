<template>
  <div class="modern-layout">
    <!-- 使用新的侧边栏组件 -->
    <SidebarMenu class="layout-sidebar" />

    <!-- 主内容区域 -->
    <div class="layout-main">
      <div class="layout-content">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script setup>
import HeaderBar from "../components/HeaderBar.vue";
import SidebarMenu from "../components/SidebarMenu.vue";
</script>

<style scoped>
.modern-layout {
  display: flex;
  height: 100vh;
  background: var(--bg-secondary);
}

.layout-sidebar {
  flex-shrink: 0;
  position: relative;
  z-index: 100;
}

.layout-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.layout-content {
  flex: 1;
  overflow-y: auto;
  background: var(--bg-secondary);
  position: relative;
}

/* 响应式布局 */
@media (max-width: 768px) {
  .layout-sidebar {
    position: fixed;
    top: 0;
    left: 0;
    height: 100vh;
    z-index: 1000;
    transform: translateX(-100%);
    transition: transform var(--transition-duration) var(--transition-timing);
  }

  .layout-sidebar.mobile-open {
    transform: translateX(0);
  }

  .layout-main {
    width: 100%;
  }
}
</style>
