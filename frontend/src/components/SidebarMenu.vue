<script setup>
import { ref, computed } from "vue";
import { useRouter, useRoute } from "vue-router";
import { hasPermission } from "../composables/permission";
import { useI18n } from "vue-i18n";

const router = useRouter();
const route = useRoute();

const activeMenu = ref(route.name || "Dashboard");
const { t } = useI18n();

// 现代化菜单项配置 - 使用统一的科技风格设计
const menuItems = [
  { 
    name: "Dashboard", 
    label: "menu.dashboard", 
    icon: "💻",
    color: "neon-blue",
    description: "系统总览与核心数据"
  },
  {
    name: "CustomerCrawl",
    label: "menu.lead",
    icon: "🎯",
    color: "neon-green",
    description: "智能化客户获取",
    permission: "customer:crawl",
  },
  {
    name: "CustomerManage",
    label: "menu.customer",
    icon: "👥",
    color: "neon-purple",
    description: "客户关系管理",
    permission: "customer:manage",
  },
  {
    name: "CampaignCenter", 
    label: "menu.campaign", 
    icon: "🚀",
    color: "neon-pink",
    description: "营销活动管理"
  },
  {
    name: "NotificationCenter", 
    label: "menu.notification", 
    icon: "📡",
    color: "neon-yellow",
    description: "消息通知中心"
  },
  {
    name: "EmailMarketing",
    label: "menu.email",
    icon: "📬",
    color: "neon-orange",
    description: "邮件营销系统",
    permission: "email:send",
  },
  {
    name: "SocialMedia",
    label: "menu.social",
    icon: "🌐",
    color: "neon-blue",
    description: "社交媒体营销",
    permission: "social:manage",
  },
  {
    name: "TaskSchedule",
    label: "menu.task",
    icon: "⚡",
    color: "neon-green",
    description: "任务调度管理",
    permission: "task:schedule",
  },
  {
    name: "BehaviorTrack",
    label: "menu.behavior",
    icon: "📊",
    color: "neon-purple",
    description: "用户行为分析",
    permission: "behavior:track",
  },
  {
    name: "ContentGenerate",
    label: "menu.content",
    icon: "🎨",
    color: "neon-pink",
    description: "AI内容生成",
    permission: "content:generate",
  },
  {
    name: "Reports",
    label: "menu.reports",
    icon: "📈",
    color: "neon-yellow",
    description: "数据报表分析",
    permission: "report:view",
  },
  {
    name: "Permission",
    label: "menu.permission",
    icon: "🔒",
    color: "neon-orange",
    description: "权限管理系统",
    permission: "system:permission",
  },
  {
    name: "Settings",
    label: "menu.system",
    icon: "⚙️",
    color: "neon-blue",
    description: "系统配置管理",
    permission: "system:setting",
  },
  { 
    name: "HelpCenter", 
    label: "menu.help", 
    icon: "💡",
    color: "neon-green",
    description: "帮助与支持"
  },
];

const visibleItems = computed(() => 
  menuItems.filter(item => !item.permission || hasPermission(item.permission))
);

function handleMenuSelect(menuName) {
  activeMenu.value = menuName;
  router.push({ name: menuName });
}

// 获取颜色类名
function getColorClass(color) {
  return `sidebar-menu__item--${color}`;
}

// 检查是否为当前活跃菜单
function isActive(menuName) {
  return activeMenu.value === menuName || route.name === menuName;
}
</script>

<template>
  <div class="sidebar-menu">
    <!-- 品牌标识区域 -->
    <div class="sidebar-menu__brand">
      <div class="sidebar-menu__logo">
        <span class="sidebar-menu__logo-icon">🌐</span>
        <span class="sidebar-menu__logo-text">{{ t("sidebar.brand") }}</span>
      </div>
      <div class="sidebar-menu__version">v2.0</div>
    </div>

    <!-- 导航菜单区域 -->
    <div class="sidebar-menu__nav">
      <div 
        v-for="item in visibleItems" 
        :key="item.name"
        :class="[
          'sidebar-menu__item',
          getColorClass(item.color),
          { 'sidebar-menu__item--active': isActive(item.name) }
        ]"
        @click="handleMenuSelect(item.name)"
      >
        <div class="sidebar-menu__item-content">
          <div class="sidebar-menu__item-icon">
            <span>{{ item.icon }}</span>
            <div class="sidebar-menu__item-glow"></div>
          </div>
          <div class="sidebar-menu__item-text">
            <div class="sidebar-menu__item-title">{{ t(item.label) }}</div>
            <div class="sidebar-menu__item-desc">{{ item.description }}</div>
          </div>
        </div>
        <div class="sidebar-menu__item-indicator"></div>
      </div>
    </div>

    <!-- 底部信息区域 -->
    <div class="sidebar-menu__footer">
      <div class="sidebar-menu__stats">
        <div class="sidebar-menu__stat-item">
          <span class="sidebar-menu__stat-value">{{ visibleItems.length }}</span>
          <span class="sidebar-menu__stat-label">模块</span>
        </div>
        <div class="sidebar-menu__stat-item">
          <span class="sidebar-menu__stat-value">100%</span>
          <span class="sidebar-menu__stat-label">在线</span>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* === 侧边栏菜单深色科技风样式 === */
.sidebar-menu {
  width: 280px;
  height: 100vh;
  background: var(--om-bg-gradient-primary);
  border-right: 1px solid var(--om-glass-border);
  backdrop-filter: var(--om-glass-backdrop-strong);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  position: relative;
}

.sidebar-menu::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: var(--om-bg-gradient-overlay);
  z-index: -1;
  pointer-events: none;
}

/* 品牌标识区域 */
.sidebar-menu__brand {
  padding: var(--om-space-6);
  border-bottom: 1px solid var(--om-glass-border);
  background: var(--om-glass-bg-strong);
  backdrop-filter: var(--om-glass-backdrop);
}

.sidebar-menu__logo {
  display: flex;
  align-items: center;
  gap: var(--om-space-3);
  margin-bottom: var(--om-space-2);
}

.sidebar-menu__logo-icon {
  font-size: var(--om-font-2xl);
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  background: var(--om-color-brand-gradient);
  border-radius: var(--om-radius-lg);
  box-shadow: var(--om-shadow-neon-blue);
}

.sidebar-menu__logo-text {
  font-size: var(--om-font-lg);
  font-weight: var(--om-font-bold);
  background: var(--om-color-brand-gradient);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.sidebar-menu__version {
  font-size: var(--om-font-xs);
  color: var(--om-text-tertiary);
  font-weight: var(--om-font-medium);
  text-transform: uppercase;
  letter-spacing: 1px;
}

/* 导航菜单区域 */
.sidebar-menu__nav {
  flex: 1;
  padding: var(--om-space-4);
  overflow-y: auto;
  scrollbar-width: thin;
}

.sidebar-menu__item {
  position: relative;
  margin-bottom: var(--om-space-3);
  border-radius: var(--om-radius-xl);
  background: var(--om-glass-bg);
  border: 1px solid var(--om-glass-border);
  backdrop-filter: var(--om-glass-backdrop);
  transition: all var(--om-transition-normal);
  cursor: pointer;
  overflow: hidden;
}

.sidebar-menu__item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: transparent;
  transition: all var(--om-transition-normal);
}

.sidebar-menu__item-content {
  display: flex;
  align-items: center;
  gap: var(--om-space-4);
  padding: var(--om-space-4);
  position: relative;
  z-index: 1;
}

.sidebar-menu__item-icon {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 48px;
  height: 48px;
  background: var(--om-glass-bg-strong);
  border-radius: var(--om-radius-lg);
  font-size: var(--om-font-xl);
  transition: all var(--om-transition-normal);
}

.sidebar-menu__item-glow {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  border-radius: inherit;
  opacity: 0;
  transition: all var(--om-transition-normal);
}

.sidebar-menu__item-text {
  flex: 1;
  min-width: 0;
}

.sidebar-menu__item-title {
  font-size: var(--om-font-md);
  font-weight: var(--om-font-semibold);
  color: var(--om-text-primary);
  margin-bottom: var(--om-space-1);
  transition: color var(--om-transition-normal);
}

.sidebar-menu__item-desc {
  font-size: var(--om-font-xs);
  color: var(--om-text-tertiary);
  line-height: var(--om-line-tight);
  transition: color var(--om-transition-normal);
}

.sidebar-menu__item-indicator {
  position: absolute;
  right: var(--om-space-4);
  top: 50%;
  transform: translateY(-50%);
  width: 6px;
  height: 6px;
  border-radius: var(--om-radius-full);
  background: var(--om-text-tertiary);
  opacity: 0;
  transition: all var(--om-transition-normal);
}

/* 悬停效果 */
.sidebar-menu__item:hover {
  background: var(--om-glass-bg-strong);
  border-color: var(--om-glass-border-strong);
  transform: translateX(4px) scale(1.02);
  box-shadow: var(--om-shadow-lg);
}

.sidebar-menu__item:hover .sidebar-menu__item-icon {
  transform: scale(1.1);
}

.sidebar-menu__item:hover .sidebar-menu__item-indicator {
  opacity: 1;
}

/* 激活状态 */
.sidebar-menu__item--active {
  background: var(--om-color-dark-600);
  border-color: var(--om-glass-border-neon);
  box-shadow: var(--om-shadow-neon-blue);
  transform: translateX(8px);
}

.sidebar-menu__item--active::before {
  background: var(--om-color-brand-gradient);
}

.sidebar-menu__item--active .sidebar-menu__item-title {
  background: var(--om-color-brand-gradient);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.sidebar-menu__item--active .sidebar-menu__item-desc {
  color: var(--om-text-secondary);
}

.sidebar-menu__item--active .sidebar-menu__item-indicator {
  opacity: 1;
  background: var(--om-color-neon-blue);
  box-shadow: var(--om-shadow-neon-blue);
}

/* 颜色主题变体 */
.sidebar-menu__item--neon-blue:hover {
  border-color: var(--om-color-neon-blue);
  box-shadow: var(--om-shadow-neon-blue);
}

.sidebar-menu__item--neon-blue:hover .sidebar-menu__item-glow {
  opacity: 0.2;
  box-shadow: inset 0 0 20px var(--om-color-neon-blue);
}

.sidebar-menu__item--neon-green:hover {
  border-color: var(--om-color-neon-green);
  box-shadow: var(--om-shadow-neon-green);
}

.sidebar-menu__item--neon-green:hover .sidebar-menu__item-glow {
  opacity: 0.2;
  box-shadow: inset 0 0 20px var(--om-color-neon-green);
}

.sidebar-menu__item--neon-purple:hover {
  border-color: var(--om-color-neon-purple);
  box-shadow: var(--om-shadow-neon-purple);
}

.sidebar-menu__item--neon-purple:hover .sidebar-menu__item-glow {
  opacity: 0.2;
  box-shadow: inset 0 0 20px var(--om-color-neon-purple);
}

.sidebar-menu__item--neon-pink:hover {
  border-color: var(--om-color-neon-pink);
  box-shadow: var(--om-shadow-neon-pink);
}

.sidebar-menu__item--neon-pink:hover .sidebar-menu__item-glow {
  opacity: 0.2;
  box-shadow: inset 0 0 20px var(--om-color-neon-pink);
}

.sidebar-menu__item--neon-yellow:hover {
  border-color: var(--om-color-neon-yellow);
  box-shadow: var(--om-shadow-neon-yellow);
}

.sidebar-menu__item--neon-yellow:hover .sidebar-menu__item-glow {
  opacity: 0.2;
  box-shadow: inset 0 0 20px var(--om-color-neon-yellow);
}

.sidebar-menu__item--neon-orange:hover {
  border-color: var(--om-color-neon-orange);
  box-shadow: 0 0 20px rgba(255, 140, 66, 0.4);
}

.sidebar-menu__item--neon-orange:hover .sidebar-menu__item-glow {
  opacity: 0.2;
  box-shadow: inset 0 0 20px var(--om-color-neon-orange);
}

/* 底部信息区域 */
.sidebar-menu__footer {
  padding: var(--om-space-4);
  border-top: 1px solid var(--om-glass-border);
  background: var(--om-glass-bg-strong);
  backdrop-filter: var(--om-glass-backdrop);
}

.sidebar-menu__stats {
  display: flex;
  gap: var(--om-space-4);
}

.sidebar-menu__stat-item {
  flex: 1;
  text-align: center;
  padding: var(--om-space-3);
  background: var(--om-glass-bg);
  border-radius: var(--om-radius-lg);
  border: 1px solid var(--om-glass-border);
}

.sidebar-menu__stat-value {
  display: block;
  font-size: var(--om-font-lg);
  font-weight: var(--om-font-bold);
  color: var(--om-color-neon-green);
  margin-bottom: var(--om-space-1);
}

.sidebar-menu__stat-label {
  font-size: var(--om-font-xs);
  color: var(--om-text-tertiary);
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .sidebar-menu {
    width: 100%;
    height: auto;
  }
  
  .sidebar-menu__nav {
    padding: var(--om-space-3);
  }
  
  .sidebar-menu__item {
    margin-bottom: var(--om-space-2);
  }
  
  .sidebar-menu__item-content {
    padding: var(--om-space-3);
  }
  
  .sidebar-menu__item-icon {
    width: 40px;
    height: 40px;
  }
}

/* 动画增强 */
.sidebar-menu__item {
  animation: fadeInUp 0.6s var(--om-ease-cyber);
}

.sidebar-menu__item:nth-child(1) { animation-delay: 0ms; }
.sidebar-menu__item:nth-child(2) { animation-delay: 50ms; }
.sidebar-menu__item:nth-child(3) { animation-delay: 100ms; }
.sidebar-menu__item:nth-child(4) { animation-delay: 150ms; }
.sidebar-menu__item:nth-child(5) { animation-delay: 200ms; }
.sidebar-menu__item:nth-child(6) { animation-delay: 250ms; }
.sidebar-menu__item:nth-child(7) { animation-delay: 300ms; }
.sidebar-menu__item:nth-child(8) { animation-delay: 350ms; }
.sidebar-menu__item:nth-child(9) { animation-delay: 400ms; }
.sidebar-menu__item:nth-child(10) { animation-delay: 450ms; }
.sidebar-menu__item:nth-child(11) { animation-delay: 500ms; }
.sidebar-menu__item:nth-child(12) { animation-delay: 550ms; }
.sidebar-menu__item:nth-child(13) { animation-delay: 600ms; }
.sidebar-menu__item:nth-child(14) { animation-delay: 650ms; }

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 减少动画偏好支持 */
@media (prefers-reduced-motion: reduce) {
  .sidebar-menu__item {
    animation: none;
    transition: none;
  }
  
  .sidebar-menu__item:hover {
    transform: none;
  }
  
  .sidebar-menu__item--active {
    transform: none;
  }
}
</style>
