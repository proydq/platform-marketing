<template>
  <div class="system-container">
    <SidebarMenu class="sidebar" />
    <div class="main-content">
      <HeaderBar class="header" />
      <div class="content-area">
        <router-view />
      </div>

    </div>
  </div>
</template>

<script setup>
import SidebarMenu from '../components/SidebarMenu.vue'
import HeaderBar from '../components/HeaderBar.vue'
</script>
