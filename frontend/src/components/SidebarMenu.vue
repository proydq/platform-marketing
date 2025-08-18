<script setup>
import { ref, computed, onMounted, nextTick } from "vue";
import { useRouter, useRoute } from "vue-router";
import { hasPermission } from "../composables/permission";
import { useI18n } from "vue-i18n";
import { useUserStore } from "@/store";
import { ElMessage } from "element-plus";

const router = useRouter();
const route = useRoute();
const userStore = useUserStore();

const activeMenu = ref(route.name || "Dashboard");
const { t } = useI18n();

// 确保用户信息被正确初始化 - 在组件创建时立即初始化
if (!userStore.currentUser) {
  console.log('立即初始化默认用户...');
  userStore.setDefaultUser();
}

// 确保有token
if (!userStore.token) {
  userStore.setToken('dev-token-123456');
}

onMounted(async () => {
  console.log('组件挂载完成');
  
  // 确保用户数据已初始化
  if (!userStore.currentUser) {
    userStore.setDefaultUser();
  }
  
  // 等待下一个tick确保响应式更新
  await nextTick();
  
  console.log('当前用户:', userStore.currentUser);
  console.log('用户权限数量:', userStore.currentUser?.permissions?.length || 0);
  console.log('可见菜单项数量:', visibleItems.value.length);
});

const menuItems = [
  // 1. 产品内容中心 - 集中管理产品信息、素材、多语言版本
  {
    name: "ProductCenter",
    label: "menu.productContentCenter",
    icon: "📦",
    permission: "product:dir",
    description: "产品内容中心"
  },
  
  // 2. 数据采集模块 - 多源数据导入、去重、验证、清洗
  {
    name: "DataImport",
    label: "menu.dataCollectionModule",
    icon: "🔍",
    permission: "customer-collect:dir",
    description: "数据采集模块"
  },
  
  // 2.1 爬虫数据管理 - 查看和管理爬虫采集的数据
  {
    name: "CrawlData",
    label: "menu.crawlDataManagement", 
    icon: "📊",
    permission: "crawl:data:view",
    description: "爬虫数据管理"
  },
  
  // 3. 客户管理模块 - 客户信息、行为数据、画像、评分
  {
    name: "CustomerManage", 
    label: "menu.customerManagementModule",
    icon: "👥",
    permission: "customer:dir",
    description: "客户管理模块"
  },
  
  // 4. AI营销助手 - 内容生成、关键词研究、智能推荐、策略优化
  {
    name: "ContentGenerate",
    label: "menu.aiMarketingAssistant",
    icon: "🤖",
    permission: "content-generation:dir",
    description: "AI营销助手"
  },
  
  // 5. 渠道触达模块 - 多渠道发送、统一排程、频率控制
  {
    name: "CampaignCenter", 
    label: "menu.channelReachModule", 
    icon: "📡",
    permission: "campaign:dir",
    description: "渠道触达模块"
  },
  
  // 7. 行为跟踪与分析 - 全渠道记录、转化分析、ROI计算
  {
    name: "BehaviorTrack",
    label: "menu.behaviorTrackingAnalysis",
    icon: "📈",
    permission: "behavior-log:dir",
    description: "行为跟踪与分析"
  },
  
  // 8. 系统设置 - 系统配置
  {
    name: "Settings",
    label: "menu.systemSettings",
    icon: "⚙️",
    permission: "settings:dir",
    description: "系统设置"
  },

  // 控制台
  {
    name: "Dashboard",
    label: "menu.dataOverview",
    icon: "📊",
    permission: "dashboard:dir",
    description: "数据控制台"
  },
  {
    name: "CustomerCrawl",
    label: "menu.customerAcquisition",
    icon: "🔍",
    permission: "customer:crawl",
    hidden: true
  },
  {
    name: "EmailMarketing",
    label: "menu.email", 
    icon: "📧",
    permission: "email:send",
    hidden: true
  },
  {
    name: "SocialMedia",
    label: "menu.social",
    icon: "📱", 
    permission: "social:manage",
    hidden: true
  },
  {
    name: "WhatsApp",
    label: "menu.whatsapp",
    icon: "💬",
    permission: "whatsapp:send",
    hidden: true
  },
  {
    name: "TaskSchedule",
    label: "menu.task",
    icon: "⏰",
    permission: "task:schedule",
    hidden: true
  },
  {
    name: "CustomerTags",
    label: "menu.customerTags",
    icon: "🏷️",
    permission: "customer:tags",
    hidden: true
  },
  {
    name: "Reports",
    label: "menu.reports",
    icon: "📋",
    permission: "report:view",
    hidden: true
  },
  // 权限管理
  {
    name: "Permission",
    label: "menu.permission",
    icon: "🔐",
    permission: "permission:dir",
    description: "权限管理"
  },
  
  // 菜单管理
  {
    name: "MenuManagement",
    label: "menu.menuManage",
    icon: "📋",
    permission: "menu:dir",
    description: "菜单管理"
  },
  {
    name: "NotificationCenter", 
    label: "menu.notification", 
    icon: "🔔",
    hidden: true
  },
  { 
    name: "HelpCenter", 
    label: "menu.help", 
    icon: "❓",
    hidden: true
  },
];

// 使用 computed 确保响应式更新
const visibleItems = computed(() => {
  // 通过访问 userStore.currentUser 确保响应性
  const currentUser = userStore.currentUser;
  
  // 确保用户已经初始化
  if (!currentUser) {
    console.log('菜单计算: 用户未初始化，返回空菜单');
    return [];
  }
  
  const filtered = menuItems.filter(
    (i) => !i.hidden && (!i.permission || hasPermission(i.permission))
  );
  
  console.log('菜单过滤结果:', {
    totalMenuItems: menuItems.length,
    hiddenItems: menuItems.filter(i => i.hidden).length,
    visibleItems: filtered.length,
    visibleNames: filtered.map(i => i.name),
    userPermissions: currentUser?.permissions?.length || 0
  });
  
  return filtered;
});

// 分类菜单项 - 使用 computed
const dashboardItems = computed(() => 
  visibleItems.value.filter(item => 
    ['Dashboard'].includes(item.name)
  )
);

const coreItems = computed(() => 
  visibleItems.value.filter(item => 
    ['ProductCenter', 'DataImport', 'CrawlData', 'CustomerManage', 'ContentGenerate', 'CampaignCenter'].includes(item.name)
  ).map(item => ({
    ...item,
    badge: item.name === 'ContentGenerate' ? 'AI' : null
  }))
);

const analyticsItems = computed(() => 
  visibleItems.value.filter(item => 
    ['BehaviorTrack'].includes(item.name)
  )
);

const systemItems = computed(() => 
  visibleItems.value.filter(item => 
    ['Permission', 'MenuManagement', 'Settings'].includes(item.name)
  )
);


function handleMenuSelect(index) {
  activeMenu.value = index;
  router.push({ name: index });
}

// 退出登录功能
function handleLogout() {
  try {
    userStore.logout();
    ElMessage.success("退出登录成功");
    router.push("/login");
  } catch (error) {
    ElMessage.error("退出登录失败");
  }
}
</script>

<template>
  <div class="enhanced-sidebar">
    <!-- 品牌标识 -->
    <div class="sidebar-brand">
      <div class="brand-icon">🌍</div>
      <div class="brand-info">
        <div class="brand-name">{{ t("sidebar.brand") }}</div>
        <div class="brand-subtitle">海外营销系统</div>
      </div>
    </div>

    <!-- 导航菜单 -->
    <div class="sidebar-menu">
      <el-menu 
        :default-active="activeMenu" 
        @select="handleMenuSelect"
        class="nav-menu"
        :collapse="false"
      >
        <div class="menu-section">
          <div class="section-title">数据总览 ({{ dashboardItems.length }})</div>
          <el-menu-item
            v-for="item in dashboardItems"
            :key="item.name"
            :index="item.name"
            class="menu-item"
          >
            <div class="item-icon">{{ item.icon }}</div>
            <span class="item-text">{{ t(item.label) }}</span>
            <div class="item-badge" v-if="item.badge">{{ item.badge }}</div>
          </el-menu-item>
        </div>

        <div class="menu-section">
          <div class="section-title">核心功能 ({{ coreItems.length }})</div>
          <el-menu-item
            v-for="item in coreItems"
            :key="item.name"
            :index="item.name"
            class="menu-item"
          >
            <div class="item-icon">{{ item.icon }}</div>
            <span class="item-text">{{ t(item.label) }}</span>
            <div class="item-badge" v-if="item.badge">{{ item.badge }}</div>
          </el-menu-item>
        </div>

        <div class="menu-section">
          <div class="section-title">分析工具 ({{ analyticsItems.length }})</div>
          <el-menu-item
            v-for="item in analyticsItems"
            :key="item.name"
            :index="item.name"
            class="menu-item"
          >
            <div class="item-icon">{{ item.icon }}</div>
            <span class="item-text">{{ t(item.label) }}</span>
            <div class="item-badge" v-if="item.badge">{{ item.badge }}</div>
          </el-menu-item>
        </div>

        <div class="menu-section">
          <div class="section-title">系统设置 ({{ systemItems.length }})</div>
          <el-menu-item
            v-for="item in systemItems"
            :key="item.name"
            :index="item.name"
            class="menu-item"
          >
            <div class="item-icon">{{ item.icon }}</div>
            <span class="item-text">{{ t(item.label) }}</span>
            <div class="item-badge" v-if="item.badge">{{ item.badge }}</div>
          </el-menu-item>
        </div>
      </el-menu>
    </div>

    <!-- 用户信息 -->
    <div class="sidebar-user">
      <div class="user-avatar">
        <div class="avatar-circle">👤</div>
      </div>
      <div class="user-info">
        <div class="user-name">营销专员</div>
        <div class="user-role">管理员</div>
      </div>
      <div class="user-actions">
        <el-dropdown>
          <span class="dropdown-trigger">⚙️</span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item>个人设置</el-dropdown-item>
              <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">

.enhanced-sidebar {
  width: 240px; // 使用风格指南标准宽度
  height: 100vh;
  background: #FFFFFF;
  border-right: 1px solid #DCDFE6;
  display: flex;
  flex-direction: column;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  position: relative;
}

/* 品牌标识 */
.sidebar-brand {
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 15px;
  border-bottom: 1px solid #E4E7ED;
  background: #409EFF;
  height: 60px; // 与风格指南header高度一致
}

.brand-icon {
  width: 32px;
  height: 32px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 4px; // 使用风格指南圆角
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
}

.brand-info {
  flex: 1;
}

.brand-name {
  font-size: 16px;
  font-weight: 600;
  color: white;
  margin: 0 0 2px 0;
}

.brand-subtitle {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.8);
  font-weight: 400;
  margin: 0;
}

/* 导航菜单 */
.sidebar-menu {
  flex: 1;
  padding: 15px 0;
  overflow-y: auto;
  background: #FFFFFF;
}

.nav-menu {
  background: transparent;
  border: none;
}

.menu-section {
  margin-bottom: 30px;
  
  &:first-child {
    margin-top: 15px;
  }
}

.section-title {
  font-size: 12px;
  font-weight: 600;
  color: #909399;
  text-transform: uppercase;
  letter-spacing: 1px;
  padding: 0 20px;
  margin-bottom: 10px;
}

.menu-item {
  margin: 0 15px 5px;
  border-radius: 4px;
  transition: all 0.3s;
  background: transparent;
  color: #606266;
  height: 40px;
  display: flex;
  align-items: center;
  padding: 0 20px;
  cursor: pointer;
  position: relative;
  
  &:hover {
    background: #ecf5ff;
    color: #409EFF;
  }
  
  &.is-active {
    background: #409EFF;
    color: white;
    
    &::before {
      content: '';
      position: absolute;
      left: 0;
      top: 50%;
      transform: translateY(-50%);
      width: 3px;
      height: 20px;
      background: white;
      border-radius: 0 2px 2px 0;
    }
  }
}

.item-icon {
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 10px;
  font-size: 13px;
  flex-shrink: 0;
}

.item-text {
  flex: 1;
  font-size: 13px;
  font-weight: 400;
}

.item-badge {
  background: #E6A23C;
  color: white;
  font-size: 10px;
  font-weight: 600;
  padding: 2px 6px;
  border-radius: 2px;
  margin-left: 5px;
}

/* 用户信息 */
.sidebar-user {
  padding: 20px;
  border-top: 1px solid #E4E7ED;
  display: flex;
  align-items: center;
  gap: 10px;
  background: #F0F2F5;
}

.user-avatar {
  flex-shrink: 0;
}

.avatar-circle {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: #409EFF;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  color: white;
}

.user-info {
  flex: 1;
  min-width: 0;
}

.user-name {
  font-size: 13px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 2px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.user-role {
  font-size: 12px;
  color: #909399;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.user-actions {
  flex-shrink: 0;
}

.dropdown-trigger {
  cursor: pointer;
  padding: 5px;
  border-radius: 4px;
  transition: all 0.3s;
  font-size: 13px;
  color: #909399;
  
  &:hover {
    background: #F2F6FC;
    color: #409EFF;
  }
}

/* 覆盖Element Plus菜单样式 */
.nav-menu .el-menu-item {
  background: transparent !important;
  border: none !important;
  color: inherit !important;
  height: auto !important;
  line-height: inherit !important;
  padding: 0 !important;
  margin: 0 !important;
}

.nav-menu .el-menu-item:hover {
  background: transparent !important;
  color: inherit !important;
}

.nav-menu .el-menu-item.is-active {
  background: transparent !important;
  color: inherit !important;
}

/* 滚动条样式 */
.sidebar-menu::-webkit-scrollbar {
  width: 8px;
}

.sidebar-menu::-webkit-scrollbar-track {
  background: #F2F6FC;
}

.sidebar-menu::-webkit-scrollbar-thumb {
  background: #E4E7ED;
  border-radius: 4px;
  
  &:hover {
    background: #DCDFE6;
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .enhanced-sidebar {
    width: 200px;
  }
  
  .sidebar-brand {
    padding: 15px;
  }
  
  .brand-name {
    font-size: 13px;
  }
  
  .brand-subtitle {
    display: none;
  }
  
  .sidebar-user {
    padding: 15px;
  }
  
  .user-name {
    font-size: 12px;
  }
  
  .user-role {
    display: none;
  }
  
  .item-text {
    font-size: 12px;
  }
  
  .section-title {
    font-size: 10px;
  }
}
</style>
