<template>
  <div class="main-layout">
    <SidebarMenu class="sidebar" />
    <div class="content">
      <HeaderBar />
      <router-view />
    </div>
  </div>
</template>

<script setup>
import SidebarMenu from '../components/SidebarMenu.vue'
import HeaderBar from '../components/HeaderBar.vue'
</script>
