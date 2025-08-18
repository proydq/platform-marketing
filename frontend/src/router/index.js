import { createRouter, createWebHistory } from "vue-router";
import { useUserStore } from "../store";

import LoginView from "../views/LoginView.vue";
import MainLayout from "../layouts/MainLayout.vue";
import DashboardView from "../views/dashboard/DashboardView.vue";
import PermissionView from "../views/system/PermissionView.vue";
import SettingsView from "../views/system/SettingsView.vue";
import ContentGenerateView from "../views/content/ContentGenerateView.vue";
import EmailMarketingView from "../views/marketing/EmailMarketingView.vue";
import SocialMediaView from "../views/marketing/SocialMediaView.vue";
import TaskScheduleView from "../views/scheduler/TaskScheduleView.vue";
import BehaviorTrackView from "../views/analytics/BehaviorTrackView.vue";
import ReportsView from "../views/report/ReportsView.vue";
import HelpCenterView from "../views/support/HelpCenterView.vue";
import NotificationCenterView from "../views/notification/NotificationCenterView.vue";
import CampaignCenterView from "../views/marketing/CampaignCenterView.vue";
import MenuManagement from "../views/system/MenuManagement.vue";

// 客户模块组件 - 使用懒加载
const CustomerListView = () => import("../views/customer/CustomerListView.vue");
const CustomerAcquisitionView = () =>
  import("../views/customer/CustomerAcquisitionView.vue");
const CustomerAnalyticsView = () =>
  import("../views/customer/CustomerAnalyticsView.vue");
const DataImportView = () => import("../views/customer/DataImportView.vue");
const CustomerTagsView = () => import("../views/customer/CustomerTagsView.vue");
const CrawlDataView = () => import("../views/customer/CrawlDataView.vue");
const WhatsAppView = () => import("../views/marketing/WhatsAppView.vue");
const ProductCenterView = () => import("../views/product/ProductCenterView.vue");
const ProductEditView = () => import("../views/product/ProductEditView.vue");
const AIMarketingAssistantView = () => import("../views/ai/AIMarketingAssistantView.vue");
const routes = [
  { path: "/login", name: "Login", component: LoginView },
  {
    path: "/",
    component: MainLayout,
    children: [
      { path: "", redirect: "/dashboard" },
      { path: "dashboard", name: "Dashboard", component: DashboardView },

      // 客户管理模块 - 重构后的嵌套路由
      {
        path: "customer",
        name: "CustomerManage",
        redirect: "/customer/list",
        children: [
          {
            path: "list",
            name: "CustomerList",
            component: CustomerListView,
            meta: {
              title: "customer.title",
              requiresAuth: true,
              permissions: ["customer:view"],
            },
          },
          {
            path: "acquisition",
            name: "CustomerAcquisition",
            component: CustomerAcquisitionView,
            meta: {
              title: "customer.acquisition.title",
              requiresAuth: true,
              permissions: ["customer:acquisition"],
            },
          },
          {
            path: "analytics",
            name: "CustomerAnalytics",
            component: CustomerAnalyticsView,
            meta: {
              title: "customer.analytics.title",
              requiresAuth: true,
              permissions: ["customer:analytics"],
            },
          },
          {
            path: "import",
            name: "DataImport",
            component: DataImportView,
            meta: {
              title: "dataImport.title",
              requiresAuth: true,
              permissions: ["customer:import"],
            },
          },
          {
            path: "tags",
            name: "CustomerTags",
            component: CustomerTagsView,
            meta: {
              title: "customerTags.title",
              requiresAuth: true,
              permissions: ["customer:tags"],
            },
          },
          {
            path: "crawl-data",
            name: "CrawlData",
            component: CrawlDataView,
            meta: {
              title: "crawlData.title",
              requiresAuth: true,
              permissions: ["crawl:data:view"],
            },
          },
        ],
      },

      // 系统管理模块
      { path: "permission", name: "Permission", component: PermissionView },
      { path: "settings", name: "Settings", component: SettingsView },
      {
        path: "system/menu",
        name: "MenuManagement",
        component: MenuManagement,
      },

      // 营销模块
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
        path: "campaign-center",
        name: "CampaignCenter",
        component: CampaignCenterView,
      },
      {
        path: "whatsapp",
        name: "WhatsApp",
        component: WhatsAppView,
        meta: {
          title: "whatsapp.title",
          requiresAuth: true,
          permissions: ["whatsapp:send"],
        },
      },

      // 产品内容中心模块
      {
        path: "product",
        name: "Product",
        redirect: "/product/center",
        children: [
          {
            path: "center",
            name: "ProductCenter",
            component: ProductCenterView,
            meta: {
              title: "product.center",
              requiresAuth: true,
              permissions: ["product:view"],
            },
          },
          {
            path: "edit",
            name: "ProductCreate",
            component: ProductEditView,
            meta: {
              title: "product.newProduct",
              requiresAuth: true,
              permissions: ["product:create"],
            },
          },
          {
            path: "edit/:id",
            name: "ProductEdit",
            component: ProductEditView,
            meta: {
              title: "product.editProduct",
              requiresAuth: true,
              permissions: ["product:edit"],
            },
          },
        ],
      },
      // 其他功能模块
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

      // 渠道管理模块
      {
        path: "channels",
        name: "ChannelManage",
        component: () => import("../views/channel/ChannelManageView.vue"),
        meta: {
          title: "channels.management",
          requiresAuth: true,
          permissions: ["channel:manage"],
        },
      },

      // AI营销助手模块
      {
        path: "ai-assistant",
        name: "AIMarketingAssistant",
        component: AIMarketingAssistantView,
        meta: {
          title: "AI营销助手",
          requiresAuth: true,
          permissions: ["ai:assistant"],
        },
      },

      // 向下兼容的路由重定向
      { path: "customer-manage", redirect: "/customer/list" },
      { path: "customer-crawl", redirect: "/customer/acquisition" },
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
