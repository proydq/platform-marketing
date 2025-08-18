<script setup>
import { useRoute } from "vue-router";
import { useUserStore } from "../store";
import { useI18n } from "vue-i18n";

const store = useUserStore();
const route = useRoute();
const { t, locale } = useI18n();

const titleMap = {
  "/dashboard": "sidebar.dashboard",
  "/campaign-center": "sidebar.campaignCenter",
  "/notifications": "sidebar.notificationCenter",
  "/customer-crawl": "sidebar.customerCrawl",
  "/customer-manage": "sidebar.customerManage",
  "/content-generate": "sidebar.contentGenerate",
  "/email-marketing": "sidebar.emailMarketing",
  "/social-media": "sidebar.socialMedia",
  "/task-schedule": "sidebar.taskSchedule",
  "/behavior-track": "sidebar.behaviorTrack",
  "/reports": "sidebar.reports",
  "/permission": "sidebar.permission",
  "/system/menu": "sidebar.menuManage",
  "/settings": "sidebar.settings",
};
function changeLang(lang) {
  locale.value = lang;
  localStorage.setItem("lang", lang);
}
</script>

<template>
  <div class="header">
    <h2 style="margin: 0; color: #333">{{ t(titleMap[route.path] || "") }}</h2>
    <div class="user-info">
      <el-badge :value="3" type="primary">
        <el-button type="text" size="large"
          ><span class="icon">🔔</span></el-button
        >
      </el-badge>
      <el-dropdown @command="changeLang">
        <span class="lang-select">{{
          locale === "en" ? "English" : "中文"
        }}</span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="zh">中文</el-dropdown-item>
            <el-dropdown-item command="en">English</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
      <el-dropdown>
        <div
          style="display: flex; align-items: center; gap: 8px; cursor: pointer"
        >
          <el-avatar :src="store.currentUser?.avatar" />
          <span>{{ store.currentUser?.name }}</span>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item>{{
              t("header.personalSettings")
            }}</el-dropdown-item>
            <el-dropdown-item
              @click="
                store.logout();
                $router.push('/login');
              "
              >{{ t("header.logout") }}</el-dropdown-item
            >
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>
