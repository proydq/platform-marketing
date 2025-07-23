<template>
  <div class="system-container">
    <div class="sidebar">
      <div class="logo">🌍 {{ t("sidebar.brand") }}</div>
      <el-menu :default-active="activeMenu" @select="handleSelect">
        <el-menu-item
          v-for="item in otherMenus"
          :key="item.path"
          :index="item.path"
        >
          <el-icon><component :is="item.icon" /></el-icon>
          <span>{{ item.title }}</span>
        </el-menu-item>
        <el-sub-menu index="system">
          <template #title>
            <el-icon><Setting /></el-icon>
            <span>系统管理</span>
          </template>
          <el-menu-item
            v-for="item in systemMenus"
            :key="item.path"
            :index="item.path"
          >
            <el-icon><component :is="item.icon" /></el-icon>
            <span>{{ item.title }}</span>
          </el-menu-item>
        </el-sub-menu>
      </el-menu>
    </div>
    <div class="main-content">
      <HeaderBar class="header" />
      <div class="content-area">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from "vue";
import { useRouter, useRoute } from "vue-router";
import { useI18n } from "vue-i18n";
import HeaderBar from "../components/HeaderBar.vue";
import { fetchMenuTree } from "@/api/menu";
import * as Icons from "@element-plus/icons-vue";

const router = useRouter();
const route = useRoute();
const { t } = useI18n();

const activeMenu = ref(route.path);
watch(
  () => route.path,
  (val) => (activeMenu.value = val)
);

// 动态菜单列表
const otherMenus = ref([]);
const systemMenus = ref([]);

onMounted(async () => {
  const res = await fetchMenuTree();
  const all = flatten(res.data || []);

  // 自动识别“系统管理”菜单
  systemMenus.value = all.filter(
    (i) =>
      i.path?.startsWith("/permission") ||
      i.path?.startsWith("/settings") ||
      i.path?.startsWith("/system/")
  );
  otherMenus.value = all.filter((i) => !systemMenus.value.includes(i));
});

// 将树结构拍平为一维菜单数组
function flatten(tree) {
  const result = [];
  const walk = (nodes) => {
    nodes.forEach((node) => {
      result.push({
        path: node.path,
        title: node.name,
        icon: Icons[node.icon] || Icons.Menu,
      });
      if (node.children?.length) walk(node.children);
    });
  };
  walk(tree);
  return result;
}

function handleSelect(index) {
  router.push(index);
}
</script>
