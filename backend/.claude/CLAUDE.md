# CLAUDE.md - 海外营销系统重构

## 项目概述
海外营销平台的重构项目，专注于改善前端架构、统一API接口和优化用户体验。

## 重构原则
- 确保每次修改都是可测试的增量改进
- 更新相关的导入路径和引用
- 保持现有功能的完整性
- 统一代码风格和命名规范

## 可用命令
- `/refactor-customer` - 重构客户管理模块
- `/refactor-marketing` - 重构营销中心
- `/optimize-routes` - 优化路由结构  
- `/unify-apis` - 统一API接口
- `/review-structure` - 审查重构结果
- `/quick-start` - 快速开始重构流程

## 技术栈
- **前端**: Vue 3 + Vite + Element Plus + Pinia
- **后端**: Spring Boot + MySQL + JWT
- **构建**: Maven (后端) + Vite (前端)

## 目录结构
```
platform-marketing/
├── backend/           # Spring Boot 后端
├── frontend/          # Vue 3 前端
└── .claude/           # Claude Code 配置
    ├── CLAUDE.md      # 主配置文件
    └── commands/      # 重构命令脚本
```