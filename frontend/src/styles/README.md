# 🎨 海外营销平台现代设计系统

**基于现代设计令牌的统一视觉风格系统**

## 📖 概述

这是一个全面重构和现代化的设计系统，专为海外营销平台打造。它结合了最新的设计趋势、无障碍标准和性能优化，提供了统一、美观、可维护的界面解决方案。

### ✨ 设计特色

- **🌈 现代配色方案** - 采用渐变品牌色彩，支持深色主题
- **🪟 玻璃态设计** - backdrop-filter 实现的现代磨砂玻璃效果  
- **🎬 流畅动画** - 基于物理学的缓动函数和分层动画
- **📱 响应式优先** - 移动端和桌面端完美适配
- **♿ 无障碍支持** - 支持高对比度、减少动画等偏好设置
- **🎯 一致性保证** - 基于设计令牌的严格规范

---

## 🎯 快速开始

### 基础引入

```css
/* 在你的主样式文件中引入 */
@import './styles/global.css';
```

### 基础页面结构

```html
<div class="marketing-container marketing-animate-fade-in-up">
  <!-- 页面头部 -->
  <header class="marketing-page-header marketing-animate-slide-in-right">
    <div class="marketing-page-header__content">
      <div class="marketing-page-header__main">
        <h1 class="marketing-page-header__title">页面标题</h1>
        <p class="marketing-page-header__description">页面描述信息</p>
      </div>
      <div class="marketing-page-header__actions">
        <button class="om-button om-button--primary">主要操作</button>
        <button class="om-button om-button--secondary">次要操作</button>
      </div>
    </div>
  </header>

  <!-- 统计卡片 -->
  <div class="marketing-stats-grid om-animate-stagger">
    <div class="marketing-stat-card">
      <div class="marketing-stat-card__icon">📊</div>
      <div class="marketing-stat-card__content">
        <div class="marketing-stat-card__value">1,234</div>
        <div class="marketing-stat-card__label">总客户数</div>
        <div class="marketing-stat-card__trend marketing-stat-card__trend--up">
          +12.3%
        </div>
      </div>
    </div>
    <!-- 更多统计卡片... -->
  </div>

  <!-- 主要内容 -->
  <div class="marketing-card">
    <div class="marketing-card__header">
      <div class="marketing-card__header-icon">⚡</div>
      <div class="marketing-card__header-info">
        <h3>内容标题</h3>
        <p>内容描述</p>
      </div>
    </div>
    <div class="marketing-card__body">
      <!-- 卡片内容 -->
    </div>
  </div>
</div>
```

---

## 🧩 组件系统

### 📄 页面组件

#### 页面容器
```html
<div class="marketing-container">
  <!-- 自带背景渐变和动画效果 -->
</div>
```

#### 页面头部
```html
<div class="marketing-page-header">
  <div class="marketing-page-header__content">
    <div class="marketing-page-header__main">
      <h1 class="marketing-page-header__title">标题</h1>
      <p class="marketing-page-header__description">描述</p>
    </div>
    <div class="marketing-page-header__actions">
      <!-- 操作按钮 -->
    </div>
  </div>
</div>
```

### 📊 统计组件

#### 统计卡片网格
```html
<div class="marketing-stats-grid om-animate-stagger">
  <div class="marketing-stat-card">
    <div class="marketing-stat-card__icon">📈</div>
    <div class="marketing-stat-card__content">
      <div class="marketing-stat-card__value">2,563</div>
      <div class="marketing-stat-card__label">活跃用户</div>
      <div class="marketing-stat-card__trend marketing-stat-card__trend--up">
        +15.2% 相比上月
      </div>
    </div>
  </div>
</div>
```

**可用趋势类：**
- `marketing-stat-card__trend--up` - 上升趋势（绿色）
- `marketing-stat-card__trend--down` - 下降趋势（红色）  
- `marketing-stat-card__trend--neutral` - 中性趋势（灰色）

### 🃏 卡片组件

#### 标准卡片
```html
<div class="marketing-card">
  <div class="marketing-card__header">
    <div class="marketing-card__header-icon">🚀</div>
    <div class="marketing-card__header-info">
      <h3>功能模块</h3>
      <p>模块描述信息</p>
    </div>
  </div>
  <div class="marketing-card__body">
    <!-- 主要内容 -->
  </div>
  <div class="marketing-card__footer">
    <!-- 底部操作区 -->
  </div>
</div>
```

#### 卡片网格
```html
<div class="marketing-cards-grid">
  <div class="marketing-card-item">
    <div class="marketing-card-item__header">标题</div>
    <div class="marketing-card-item__body">内容</div>
    <div class="marketing-card-item__footer">操作</div>
  </div>
</div>
```

### 📋 表格组件

#### 增强表格容器
```html
<div class="marketing-table-container">
  <div class="marketing-table-header">
    <h3 class="marketing-table-header__title">数据列表</h3>
    <div class="marketing-table-header__actions">
      <button class="om-button om-button--sm">导出</button>
      <button class="om-button om-button--primary om-button--sm">新增</button>
    </div>
  </div>
  
  <el-table>
    <!-- Element Plus 表格内容 -->
  </el-table>
</div>
```

#### 表格单元格增强
```html
<!-- 标题+元信息单元格 -->
<div class="marketing-table-cell">
  <div class="marketing-table-cell__title">客户名称</div>
  <div class="marketing-table-cell__meta">
    <span class="marketing-table-cell__tag marketing-status-badge--success">
      活跃
    </span>
    <span class="marketing-table-cell__time">2 小时前</span>
  </div>
</div>

<!-- 进度显示单元格 -->
<div class="marketing-table-cell__progress">
  <el-progress :percentage="75" />
  <span class="marketing-table-cell__progress-text">75%</span>
</div>

<!-- 数字对比单元格 -->
<div class="marketing-table-cell__numbers">
  <span class="marketing-table-cell__number-main">156</span>
  <span class="marketing-table-cell__number-sub">/ 200</span>
</div>

<!-- 操作按钮单元格 -->
<div class="marketing-table-cell__actions">
  <el-button size="small" circle>
    <el-icon><Edit /></el-icon>
  </el-button>
  <el-button size="small" circle type="danger">
    <el-icon><Delete /></el-icon>
  </el-button>
</div>
```

### 🛠️ 工具栏组件

#### 标准工具栏
```html
<div class="marketing-toolbar">
  <div class="marketing-toolbar__left">
    <span>已选择 3 项</span>
  </div>
  <div class="marketing-toolbar__right">
    <div class="marketing-toolbar__filters">
      <el-select placeholder="状态筛选">
        <el-option label="全部" value="all"></el-option>
        <el-option label="活跃" value="active"></el-option>
      </el-select>
    </div>
    <div class="marketing-toolbar__actions">
      <button class="om-button om-button--sm">批量导出</button>
      <button class="om-button om-button--primary om-button--sm">批量操作</button>
    </div>
  </div>
</div>
```

#### 视图控制
```html
<div class="marketing-view-controls">
  <div class="marketing-view-toggles">
    <el-radio-group v-model="viewMode">
      <el-radio-button value="table">列表视图</el-radio-button>
      <el-radio-button value="grid">网格视图</el-radio-button>
    </el-radio-group>
  </div>
  <div class="marketing-view-search">
    <el-input placeholder="搜索..." />
  </div>
</div>
```

---

## 🎨 设计令牌系统

### 🌈 色彩系统

#### 品牌色彩
```css
/* 主品牌色 */
var(--om-color-primary)        /* #4f46e5 - 靛蓝色 */
var(--om-color-primary-light)  /* #7c3aed - 紫色 */
var(--om-color-primary-dark)   /* #3730a3 - 深靛蓝 */

/* 品牌渐变 */
var(--om-color-brand-gradient)      /* 主渐变：靛蓝→紫色→粉色 */
var(--om-color-brand-gradient-soft) /* 柔和渐变（半透明） */
```

#### 语义色彩
```css
/* 成功色 */
var(--om-color-success)          /* #059669 */
var(--om-color-success-light)    /* #10b981 */
var(--om-color-success-gradient) /* 成功色渐变 */

/* 警告色 */
var(--om-color-warning)          /* #d97706 */
var(--om-color-warning-light)    /* #f59e0b */
var(--om-color-warning-gradient) /* 警告色渐变 */

/* 危险色 */
var(--om-color-danger)           /* #dc2626 */
var(--om-color-danger-light)     /* #ef4444 */
var(--om-color-danger-gradient)  /* 危险色渐变 */

/* 信息色 */
var(--om-color-info)             /* #2563eb */
var(--om-color-info-light)       /* #3b82f6 */
var(--om-color-info-gradient)    /* 信息色渐变 */
```

#### 中性色彩
```css
/* 现代灰度色彩 */
var(--om-color-gray-50)   /* #fafbfc - 最浅灰 */
var(--om-color-gray-100)  /* #f4f6f8 */
var(--om-color-gray-200)  /* #eaecf0 */
var(--om-color-gray-300)  /* #d0d5dd */
var(--om-color-gray-400)  /* #98a2b3 */
var(--om-color-gray-500)  /* #667085 */
var(--om-color-gray-600)  /* #475467 */
var(--om-color-gray-700)  /* #344054 */
var(--om-color-gray-800)  /* #1d2939 */
var(--om-color-gray-900)  /* #101828 - 最深灰 */
```

#### 文本色彩
```css
var(--om-text-primary)    /* 主要文本色 */
var(--om-text-secondary)  /* 次要文本色 */
var(--om-text-tertiary)   /* 第三级文本色 */
var(--om-text-disabled)   /* 禁用文本色 */
var(--om-text-inverse)    /* 反色文本（白色） */
var(--om-text-brand)      /* 品牌色文本 */
var(--om-text-success)    /* 成功色文本 */
var(--om-text-warning)    /* 警告色文本 */
var(--om-text-danger)     /* 危险色文本 */
```

### 📏 间距系统

```css
/* 精确间距令牌 */
var(--om-space-0)   /* 0px */
var(--om-space-1)   /* 4px */
var(--om-space-2)   /* 8px */
var(--om-space-3)   /* 12px */
var(--om-space-4)   /* 16px */
var(--om-space-5)   /* 20px */
var(--om-space-6)   /* 24px */
var(--om-space-7)   /* 28px */
var(--om-space-8)   /* 32px */
var(--om-space-10)  /* 40px */
var(--om-space-12)  /* 48px */
var(--om-space-16)  /* 64px */
var(--om-space-20)  /* 80px */
var(--om-space-24)  /* 96px */

/* 兼容别名 */
var(--om-space-xs)  /* = var(--om-space-1) */
var(--om-space-sm)  /* = var(--om-space-2) */
var(--om-space-md)  /* = var(--om-space-4) */
var(--om-space-lg)  /* = var(--om-space-6) */
var(--om-space-xl)  /* = var(--om-space-8) */
var(--om-space-2xl) /* = var(--om-space-12) */
```

### 🔘 圆角系统

```css
var(--om-radius-none)  /* 0 */
var(--om-radius-xs)    /* 4px */
var(--om-radius-sm)    /* 6px */
var(--om-radius-md)    /* 8px */
var(--om-radius-lg)    /* 12px */
var(--om-radius-xl)    /* 16px */
var(--om-radius-2xl)   /* 24px */
var(--om-radius-3xl)   /* 32px */
var(--om-radius-full)  /* 9999px - 完全圆角 */
```

### 🌫️ 阴影系统

#### 标准阴影
```css
var(--om-shadow-xs)   /* 轻微阴影 */
var(--om-shadow-sm)   /* 小阴影 */
var(--om-shadow-md)   /* 中等阴影 */
var(--om-shadow-lg)   /* 大阴影 */
var(--om-shadow-xl)   /* 超大阴影 */
var(--om-shadow-2xl)  /* 特大阴影 */
var(--om-shadow-3xl)  /* 巨大阴影 */
```

#### 语义阴影
```css
var(--om-shadow-brand)   /* 品牌色阴影 */
var(--om-shadow-brand-hover) /* 品牌色悬停阴影 */
var(--om-shadow-success) /* 成功色阴影 */
var(--om-shadow-warning) /* 警告色阴影 */
var(--om-shadow-danger)  /* 危险色阴影 */
```

### 🪟 玻璃态效果

```css
var(--om-glass-bg)           /* 标准玻璃背景 */
var(--om-glass-bg-strong)    /* 更强玻璃背景 */
var(--om-glass-border)       /* 玻璃边框 */
var(--om-glass-border-strong) /* 更强玻璃边框 */
var(--om-glass-blur)         /* 标准模糊效果 */
var(--om-glass-backdrop)     /* 背景模糊 */
var(--om-glass-backdrop-strong) /* 强背景模糊 */
```

### ✍️ 字体系统

#### 字体尺寸
```css
var(--om-font-2xs)  /* 10px */
var(--om-font-xs)   /* 12px */
var(--om-font-sm)   /* 14px */
var(--om-font-md)   /* 16px - 基础大小 */
var(--om-font-lg)   /* 18px */
var(--om-font-xl)   /* 20px */
var(--om-font-2xl)  /* 24px */
var(--om-font-3xl)  /* 30px */
var(--om-font-4xl)  /* 36px */
var(--om-font-5xl)  /* 48px */
var(--om-font-6xl)  /* 60px */
var(--om-font-7xl)  /* 72px */
var(--om-font-8xl)  /* 96px */
var(--om-font-9xl)  /* 128px */
```

#### 字体权重
```css
var(--om-font-thin)      /* 100 */
var(--om-font-light)     /* 300 */
var(--om-font-normal)    /* 400 */
var(--om-font-medium)    /* 500 */
var(--om-font-semibold)  /* 600 */
var(--om-font-bold)      /* 700 */
var(--om-font-extrabold) /* 800 */
var(--om-font-black)     /* 900 */
```

#### 行高
```css
var(--om-line-none)     /* 1 */
var(--om-line-tight)    /* 1.25 */
var(--om-line-snug)     /* 1.375 */
var(--om-line-normal)   /* 1.5 */
var(--om-line-relaxed)  /* 1.625 */
var(--om-line-loose)    /* 2 */
```

---

## 🎭 基础组件类

### 🔘 按钮系统

#### 按钮变体
```html
<!-- 主要按钮 -->
<button class="om-button om-button--primary">主要操作</button>

<!-- 次要按钮 -->
<button class="om-button om-button--secondary">次要操作</button>

<!-- 成功按钮 -->
<button class="om-button om-button--success">成功操作</button>

<!-- 警告按钮 -->
<button class="om-button om-button--warning">警告操作</button>

<!-- 危险按钮 -->
<button class="om-button om-button--danger">危险操作</button>

<!-- 幽灵按钮 -->
<button class="om-button om-button--ghost">幽灵按钮</button>
```

#### 按钮尺寸
```html
<button class="om-button om-button--primary om-button--xs">超小</button>
<button class="om-button om-button--primary om-button--sm">小</button>
<button class="om-button om-button--primary">标准</button>
<button class="om-button om-button--primary om-button--lg">大</button>
<button class="om-button om-button--primary om-button--xl">超大</button>
```

### 📝 输入框系统

```html
<!-- 标准输入框 -->
<input class="om-input" placeholder="请输入内容" />

<!-- 小尺寸输入框 -->
<input class="om-input om-input--sm" placeholder="小输入框" />

<!-- 大尺寸输入框 -->
<input class="om-input om-input--lg" placeholder="大输入框" />

<!-- 禁用输入框 -->
<input class="om-input" placeholder="禁用状态" disabled />
```

### 🃏 卡片系统

```html
<!-- 标准卡片 -->
<div class="om-card">
  <div class="om-card__header">卡片头部</div>
  <div class="om-card__body">卡片内容</div>
  <div class="om-card__footer">卡片底部</div>
</div>

<!-- 小卡片 -->
<div class="om-card om-card--sm">
  <div class="om-card__body">小卡片内容</div>
</div>

<!-- 大卡片 -->
<div class="om-card om-card--lg">
  <div class="om-card__body">大卡片内容</div>
</div>
```

### 📊 统计卡片

```html
<div class="om-stat-card">
  <div class="om-stat-card__icon">📈</div>
  <div class="om-stat-card__content">
    <div class="om-stat-card__value">1,234</div>
    <div class="om-stat-card__label">总销售额</div>
    <div class="om-stat-card__trend om-stat-card__trend--up">
      +12.5% 本月
    </div>
  </div>
</div>
```

---

## 🎬 动画系统

### 🎯 入场动画

```html
<!-- 淡入向上 -->
<div class="om-animate-fade-in-up">内容</div>

<!-- 淡入向下 -->
<div class="om-animate-fade-in-down">内容</div>

<!-- 右滑入 -->
<div class="om-animate-slide-in-right">内容</div>

<!-- 左滑入 -->
<div class="om-animate-slide-in-left">内容</div>

<!-- 缩放淡入 -->
<div class="om-animate-scale-in">内容</div>

<!-- 弹跳淡入 -->
<div class="om-animate-bounce-in">内容</div>

<!-- 脉冲动画 -->
<div class="om-animate-pulse">内容</div>
```

### 📊 分层动画

```html
<!-- 自动分层延迟动画 -->
<div class="marketing-stats-grid om-animate-stagger">
  <div class="marketing-stat-card">卡片 1 - 延迟 0ms</div>
  <div class="marketing-stat-card">卡片 2 - 延迟 100ms</div>
  <div class="marketing-stat-card">卡片 3 - 延迟 200ms</div>
  <div class="marketing-stat-card">卡片 4 - 延迟 300ms</div>
</div>
```

### ⚙️ 动画缓动函数

```css
var(--om-ease-out-quart)  /* 四次方缓出 */
var(--om-ease-out-expo)   /* 指数缓出 */
var(--om-ease-spring)     /* 弹簧效果 */
```

---

## 🔧 工具类

### 📏 间距工具类

#### 内边距
```html
<div class="om-p-0">无内边距</div>
<div class="om-p-1">4px 内边距</div>
<div class="om-p-2">8px 内边距</div>
<div class="om-p-3">12px 内边距</div>
<div class="om-p-4">16px 内边距</div>
<div class="om-p-5">20px 内边距</div>
<div class="om-p-6">24px 内边距</div>
<div class="om-p-8">32px 内边距</div>
```

#### 外边距
```html
<div class="om-m-0">无外边距</div>
<div class="om-m-1">4px 外边距</div>
<!-- ... 同内边距 -->

<!-- 单方向外边距 -->
<div class="om-mt-4">顶部 16px 外边距</div>
<div class="om-mb-4">底部 16px 外边距</div>
<div class="om-ml-4">左侧 16px 外边距</div>
<div class="om-mr-4">右侧 16px 外边距</div>
```

### ✍️ 文本工具类

#### 字体大小
```html
<span class="om-text-2xs">超小文本</span>
<span class="om-text-xs">小文本</span>
<span class="om-text-sm">较小文本</span>
<span class="om-text-md">标准文本</span>
<span class="om-text-lg">较大文本</span>
<span class="om-text-xl">大文本</span>
<span class="om-text-2xl">超大文本</span>
<span class="om-text-3xl">特大文本</span>
<span class="om-text-4xl">巨大文本</span>
```

#### 字体权重
```html
<span class="om-font-thin">细体</span>
<span class="om-font-light">轻体</span>
<span class="om-font-normal">常规</span>
<span class="om-font-medium">中等</span>
<span class="om-font-semibold">半粗体</span>
<span class="om-font-bold">粗体</span>
<span class="om-font-extrabold">超粗体</span>
<span class="om-font-black">黑体</span>
```

#### 文本颜色
```html
<span class="om-text-primary">主要文本</span>
<span class="om-text-secondary">次要文本</span>
<span class="om-text-tertiary">第三级文本</span>
<span class="om-text-disabled">禁用文本</span>
<span class="om-text-brand">品牌色文本</span>
<span class="om-text-success">成功色文本</span>
<span class="om-text-warning">警告色文本</span>
<span class="om-text-danger">危险色文本</span>
```

#### 文本对齐
```html
<div class="om-text-left">左对齐</div>
<div class="om-text-center">居中对齐</div>
<div class="om-text-right">右对齐</div>
<div class="om-text-justify">两端对齐</div>
```

### 🎨 显示工具类

```html
<div class="om-hidden">隐藏</div>
<div class="om-block">块级显示</div>
<div class="om-inline">行内显示</div>
<div class="om-inline-block">行内块显示</div>
<div class="om-flex">弹性布局</div>
<div class="om-inline-flex">行内弹性布局</div>
<div class="om-grid">网格布局</div>
```

### 🔗 Flexbox 工具类

```html
<!-- 方向 -->
<div class="om-flex om-flex-row">水平排列</div>
<div class="om-flex om-flex-col">垂直排列</div>

<!-- 换行 -->
<div class="om-flex om-flex-wrap">允许换行</div>
<div class="om-flex om-flex-nowrap">不允许换行</div>

<!-- 对齐 -->
<div class="om-flex om-items-start">顶部对齐</div>
<div class="om-flex om-items-center">垂直居中</div>
<div class="om-flex om-items-end">底部对齐</div>
<div class="om-flex om-items-stretch">拉伸对齐</div>

<!-- 分布 -->
<div class="om-flex om-justify-start">左对齐</div>
<div class="om-flex om-justify-center">居中</div>
<div class="om-flex om-justify-end">右对齐</div>
<div class="om-flex om-justify-between">两端对齐</div>
<div class="om-flex om-justify-around">环绕分布</div>
<div class="om-flex om-justify-evenly">平均分布</div>
```

### 📐 网格工具类

```html
<div class="om-grid om-grid--1">1列网格</div>
<div class="om-grid om-grid--2">2列网格</div>
<div class="om-grid om-grid--3">3列网格</div>
<div class="om-grid om-grid--4">4列网格</div>
<div class="om-grid om-grid--5">5列网格</div>
<div class="om-grid om-grid--6">6列网格</div>

<!-- 自适应网格 -->
<div class="om-grid om-grid--auto-fit">自适应填充</div>
<div class="om-grid om-grid--auto-fill">自适应排列</div>
```

### 🔘 圆角工具类

```html
<div class="om-rounded-none">无圆角</div>
<div class="om-rounded-xs">4px 圆角</div>
<div class="om-rounded-sm">6px 圆角</div>
<div class="om-rounded-md">8px 圆角</div>
<div class="om-rounded-lg">12px 圆角</div>
<div class="om-rounded-xl">16px 圆角</div>
<div class="om-rounded-2xl">24px 圆角</div>
<div class="om-rounded-3xl">32px 圆角</div>
<div class="om-rounded-full">完全圆角</div>
```

### 🌫️ 阴影工具类

```html
<div class="om-shadow-xs">轻微阴影</div>
<div class="om-shadow-sm">小阴影</div>
<div class="om-shadow-md">中等阴影</div>
<div class="om-shadow-lg">大阴影</div>
<div class="om-shadow-xl">超大阴影</div>
<div class="om-shadow-2xl">特大阴影</div>
<div class="om-shadow-3xl">巨大阴影</div>
<div class="om-shadow-none">无阴影</div>
```

---

## 🏷️ 状态指示器

### 🎯 状态徽章

```html
<span class="marketing-status-badge marketing-status-badge--success">
  成功状态
</span>
<span class="marketing-status-badge marketing-status-badge--warning">
  警告状态
</span>
<span class="marketing-status-badge marketing-status-badge--danger">
  错误状态
</span>
<span class="marketing-status-badge marketing-status-badge--info">
  信息状态
</span>
<span class="marketing-status-badge marketing-status-badge--neutral">
  中性状态
</span>
```

### 💫 加载状态

```html
<div class="marketing-loading">
  <div class="marketing-loading__spinner"></div>
  <div class="marketing-loading__text">加载中...</div>
</div>
```

### 📭 空状态

```html
<div class="marketing-empty-state">
  <div class="marketing-empty-state__icon">📋</div>
  <div class="marketing-empty-state__text">暂无数据</div>
  <div class="marketing-empty-state__description">
    当前列表为空，您可以添加一些数据来开始
  </div>
  <button class="om-button om-button--primary marketing-empty-state__action">
    立即添加
  </button>
</div>
```

---

## 📱 响应式设计

### 🎯 断点系统

```css
/* 移动端 */
@media (max-width: 768px) {
  /* 平板和移动端适配 */
}

/* 小屏移动端 */
@media (max-width: 480px) {
  /* 小屏移动端适配 */
}
```

### 📐 响应式网格

网格系统会在移动端自动变为单列布局：

```html
<!-- 桌面端：多列，移动端：单列 -->
<div class="marketing-stats-grid">
  <!-- 自动响应式调整 -->
</div>

<div class="marketing-cards-grid">
  <!-- 自动响应式调整 -->
</div>
```

---

## 🌓 主题支持

### 🌙 深色主题

系统自动支持用户的深色主题偏好：

```css
@media (prefers-color-scheme: dark) {
  /* 自动应用深色主题 */
}
```

### 🔍 高对比度

支持高对比度模式：

```css
@media (prefers-contrast: high) {
  /* 增强对比度，移除阴影和透明效果 */
}
```

### 🎭 减少动画

支持减少动画偏好：

```css
@media (prefers-reduced-motion: reduce) {
  /* 禁用所有动画效果 */
}
```

---

## ♿ 无障碍支持

### 🔍 焦点管理

系统提供清晰的焦点指示：

```css
:focus-visible {
  outline: 2px solid var(--om-color-primary);
  outline-offset: 2px;
}
```

### 📱 触摸友好

所有交互元素都具有足够的触摸目标大小（最小 44px × 44px）。

### 🎨 颜色对比

所有文本和背景的对比度都符合 WCAG AA 标准。

### 🔊 屏幕阅读器

使用语义化 HTML 结构，确保屏幕阅读器友好。

---

## 📝 最佳实践

### 🎯 设计一致性

1. **始终使用设计令牌** - 避免硬编码数值
2. **遵循组件规范** - 使用预定义的组件类
3. **保持视觉层次** - 正确使用字体大小和权重
4. **统一间距节奏** - 使用标准间距系统

### 🚀 性能优化

1. **避免过度嵌套** - 保持 DOM 结构简洁
2. **合理使用动画** - 避免同时运行过多动画
3. **优化图片资源** - 使用适当的图片格式和尺寸
4. **延迟加载非关键资源** - 提升首屏加载速度

### 📱 响应式原则

1. **移动端优先** - 从小屏开始设计
2. **渐进增强** - 在大屏设备上添加额外功能
3. **弹性布局** - 使用 Flexbox 和 Grid
4. **适配触摸** - 确保触摸目标大小合适

### 🔧 维护性

1. **模块化结构** - 将样式拆分为独立模块
2. **文档完整** - 为自定义组件编写文档
3. **版本控制** - 跟踪设计令牌的变更
4. **定期审查** - 清理未使用的样式

---

## 🔧 自定义扩展

### 🎨 创建自定义组件

```css
/* 基于设计令牌创建新组件 */
.my-custom-component {
  background: var(--om-glass-bg-strong);
  border: 1px solid var(--om-glass-border);
  border-radius: var(--om-radius-xl);
  padding: var(--om-space-6);
  box-shadow: var(--om-shadow-md);
  backdrop-filter: var(--om-glass-backdrop);
  transition: var(--om-transition-normal);
}

.my-custom-component:hover {
  background: var(--om-glass-bg);
  box-shadow: var(--om-shadow-lg);
  transform: translateY(-2px);
}
```

### 🌈 扩展色彩系统

```css
:root {
  /* 自定义品牌色 */
  --my-brand-primary: #ff6b6b;
  --my-brand-secondary: #4ecdc4;
  --my-brand-gradient: linear-gradient(135deg, var(--my-brand-primary) 0%, var(--my-brand-secondary) 100%);
}

.my-brand-button {
  background: var(--my-brand-gradient);
  color: var(--om-text-inverse);
  /* 其他属性继承基础按钮样式 */
}
```

### 📐 自定义断点

```css
/* 自定义断点 */
@media (max-width: 1200px) {
  .my-large-layout {
    grid-template-columns: 1fr;
  }
}

@media (min-width: 1400px) {
  .my-extra-large-container {
    max-width: 1320px;
    margin: 0 auto;
  }
}
```

---

## 🎯 迁移指南

### 📋 从旧系统迁移

#### 1. 替换容器类
```html
<!-- 旧版本 -->
<div class="customer-list-container">

<!-- 新版本 -->
<div class="marketing-container marketing-animate-fade-in-up">
```

#### 2. 更新页面头部
```html
<!-- 旧版本 -->
<div class="page-header">
  <h1>标题</h1>
</div>

<!-- 新版本 -->
<div class="marketing-page-header marketing-animate-slide-in-right">
  <div class="marketing-page-header__content">
    <div class="marketing-page-header__main">
      <h1 class="marketing-page-header__title">标题</h1>
      <p class="marketing-page-header__description">描述</p>
    </div>
  </div>
</div>
```

#### 3. 统计卡片标准化
```html
<!-- 旧版本 -->
<div class="stat-card">
  <span class="number">123</span>
  <span class="label">用户</span>
</div>

<!-- 新版本 -->
<div class="marketing-stat-card">
  <div class="marketing-stat-card__icon">👥</div>
  <div class="marketing-stat-card__content">
    <div class="marketing-stat-card__value">123</div>
    <div class="marketing-stat-card__label">用户</div>
  </div>
</div>
```

#### 4. 表格容器包装
```html
<!-- 旧版本 -->
<el-table>
  <!-- 表格内容 -->
</el-table>

<!-- 新版本 -->
<div class="marketing-table-container">
  <el-table>
    <!-- 表格内容 -->
  </el-table>
</div>
```

---

## 🎉 版本历史

### v2.0.0 - 现代化设计系统
- ✨ 全新的设计令牌系统
- 🎨 现代化色彩和渐变方案  
- 🪟 玻璃态设计语言
- 🎬 物理学动画系统
- 📱 增强的响应式设计
- ♿ 完整的无障碍支持
- 🌓 深色主题和用户偏好支持

### v1.0.0 - 基础设计系统
- 📦 基础组件库
- 🎨 基础色彩系统
- 📏 标准间距系统
- 📱 基础响应式支持

---

## 💡 获取帮助

### 📖 资源链接
- **Element Plus 官方文档**: https://element-plus.org/
- **CSS Grid 指南**: https://css-tricks.com/snippets/css/complete-guide-grid/
- **Flexbox 指南**: https://css-tricks.com/snippets/css/a-guide-to-flexbox/
- **无障碍指南**: https://www.w3.org/WAI/WCAG21/quickref/

### 🤝 贡献指南
1. 遵循现有的命名约定
2. 确保所有新组件都有完整文档
3. 测试在不同屏幕尺寸下的显示效果
4. 验证无障碍性
5. 提供使用示例

### 📞 技术支持
如果遇到问题或有改进建议，请联系开发团队或在项目中创建 Issue。

---

**🎨 海外营销平台设计系统 - 打造现代、美观、一致的用户界面体验**