<template>
  <div class="system-container">
    <div class="sidebar">
      <div class="logo">🌍 {{ t("sidebar.brand") }}</div>
      <el-menu :default-active="activeMenu" @select="handleSelect">
        <!-- 左侧非系统菜单 -->
        <el-menu-item
          v-for="item in otherMenus"
          :key="item.path"
          :index="item.path"
        >
          <el-icon><component :is="item.icon" /></el-icon>
          <span>{{ t(item.i18nKey) }}</span>
        </el-menu-item>

        <!-- 系统菜单 -->
        <el-sub-menu index="system">
          <template #title>
            <el-icon><Setting /></el-icon>
            <span>{{ t("menu.system") }}</span>
          </template>
          <el-menu-item
            v-for="item in systemMenus"
            :key="item.path"
            :index="item.path"
          >
            <el-icon><component :is="item.icon" /></el-icon>
            <span>{{ t(item.i18nKey) }}</span>
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

// 路由控制
const router = useRouter();
const route = useRoute();
const { t } = useI18n();
const activeMenu = ref(route.path);
watch(
  () => route.path,
  (val) => (activeMenu.value = val)
);

// 菜单数据
const otherMenus = ref([]);
const systemMenus = ref([]);

// 中文 → 国际化 key 映射表
const nameMap = {
  控制台: "menu.dashboard",
  营销活动: "menu.campaign",
  通知中心: "menu.notification",
  客户采集: "menu.lead",
  客户管理: "menu.customer",
  邮件营销: "menu.email",
  社交营销: "menu.social",
  任务调度: "menu.task",
  行为追踪: "menu.behavior",
  内容生成: "menu.content",
  系统设置: "menu.system",
  权限管理: "menu.permission",
  菜单管理: "sidebar.menuManage",
};

// 生命周期挂载时获取菜单
onMounted(async () => {
  const res = await fetchMenuTree();
  const all = flatten(res.data || []);

  systemMenus.value = all.filter(
    (i) =>
      i.path?.startsWith("/permission") ||
      i.path?.startsWith("/settings") ||
      i.path?.startsWith("/system/")
  );
  otherMenus.value = all.filter((i) => !systemMenus.value.includes(i));
});

// 扁平化 + 增加 i18nKey 支持
function flatten(tree) {
  const result = [];
  const walk = (nodes) => {
    nodes.forEach((node) => {
      const i18nKey = nameMap[node.name] || node.name;
      result.push({
        path: node.path,
        i18nKey,
        icon: Icons[node.icon] || Icons.Menu,
      });
      if (node.children?.length) walk(node.children);
    });
  };
  walk(tree);
  return result;
}

// 菜单点击跳转
function handleSelect(index) {
  router.push(index);
}
</script>

<style scoped>
.system-container {
  display: flex;
  height: 100vh;
}

.sidebar {
  width: 220px;
  background-color: #fff;
  border-right: 1px solid #eee;
}

.logo {
  padding: 20px;
  font-weight: bold;
  text-align: center;
  font-size: 16px;
}

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.content-area {
  flex: 1;
  overflow: auto;
  padding: 20px;
}
</style>
