<template>
  <div class="system-container">
    <SidebarMenu />
    <div class="main-content">
      <HeaderBar class="header" />
      <div class="content-area">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script setup>
import HeaderBar from "@/components/HeaderBar.vue";
import SidebarMenu from "@/components/SidebarMenu.vue";
</script>

<style scoped>
.system-container {
  display: flex;
  min-height: 100vh;
  background: var(--om-bg-gradient-primary);
}

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.header {
  background: var(--om-glass-bg-strong);
  border-bottom: 1px solid var(--om-glass-border-strong);
  backdrop-filter: var(--om-glass-backdrop-strong);
  padding: var(--om-space-4) var(--om-space-6);
}

.content-area {
  flex: 1;
  overflow: auto;
  padding: var(--om-space-6);
}
</style>
