import { createRouter, createWebHistory } from "vue-router";
import { useUserStore } from "@/store";

import LoginView from "@/views/LoginView.vue";
import MainLayout from "@/layouts/MainLayout.vue";
import DashboardView from "@/views/dashboard/DashboardView.vue";
import CustomerCrawlView from "@/views/customer/CustomerCrawlView.vue";
import PermissionView from "@/views/system/PermissionView.vue";
import CustomerManageView from "@/views/customer/CustomerManageView.vue";
import SettingsView from "@/views/system/SettingsView.vue";
import ContentGenerateView from "@/views/content/ContentGenerateView.vue";
import EmailMarketingView from "@/views/marketing/EmailMarketingView.vue";
import SocialMediaView from "@/views/marketing/SocialMediaView.vue";
import TaskScheduleView from "@/views/scheduler/TaskScheduleView.vue";
import BehaviorTrackView from "@/views/analytics/BehaviorTrackView.vue";
import ReportsView from "@/views/report/ReportsView.vue";
import HelpCenterView from "@/views/support/HelpCenterView.vue";
import NotificationCenterView from "@/views/notification/NotificationCenterView.vue";
import CampaignCenterView from "@/views/CampaignCenterView.vue";
import MenuManagement from "@/views/system/MenuManagement.vue";
const routes = [
  { path: "/login", name: "Login", component: LoginView },
  {
    path: "/",
    component: MainLayout,
    children: [
      { path: "dashboard", name: "Dashboard", component: DashboardView },
      {
        path: "customer-crawl",
        name: "CustomerCrawl",
        component: CustomerCrawlView,
      },
      {
        path: "customer-manage",
        name: "CustomerManage",
        component: CustomerManageView,
      },
      { path: "permission", name: "Permission", component: PermissionView },
      { path: "settings", name: "Settings", component: SettingsView },
      {
        path: "system/menu",
        name: "MenuManagement",
        component: MenuManagement,
      },
      {
        path: "content-generate",
        name: "ContentGenerate",
        component: ContentGenerateView,
      },
      {
        path: "email-marketing",
        name: "EmailMarketing",
        component: EmailMarketingView,
      },
      { path: "social-media", name: "SocialMedia", component: SocialMediaView },
      {
        path: "task-schedule",
        name: "TaskSchedule",
        component: TaskScheduleView,
      },
      {
        path: "behavior-track",
        name: "BehaviorTrack",
        component: BehaviorTrackView,
      },
      { path: "reports", name: "Reports", component: ReportsView },
      { path: "help-center", name: "HelpCenter", component: HelpCenterView },
      {
        path: "notifications",
        name: "NotificationCenter",
        component: NotificationCenterView,
      },
      {
        path: "campaign-center",
        name: "CampaignCenter",
        component: CampaignCenterView,
      },
    ],
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

router.beforeEach((to, from, next) => {
  const store = useUserStore();
  const loggedIn = store.isLoggedIn;
  if (!loggedIn && to.path !== "/login") {
    next("/login");
  } else if (loggedIn && to.path === "/login") {
    next("/");
  } else {
    next();
  }
});

export default router;
