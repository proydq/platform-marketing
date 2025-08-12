// 通用类型定义

/**
 * 基础实体接口
 */
export interface BaseEntity {
  id: string
  createdAt: string
  updatedAt: string
}

/**
 * 选项接口
 */
export interface Option<T = any> {
  label: string
  value: T
  disabled?: boolean
  children?: Option<T>[]
}

/**
 * 菜单项接口
 */
export interface MenuItem {
  id: string
  title: string
  icon?: string
  path?: string
  component?: string
  redirect?: string
  meta?: MenuMeta
  children?: MenuItem[]
}

/**
 * 菜单元数据
 */
export interface MenuMeta {
  title: string
  icon?: string
  hidden?: boolean
  keepAlive?: boolean
  permission?: string[]
  breadcrumb?: boolean
  activeMenu?: string
}

/**
 * 用户信息
 */
export interface UserInfo {
  id: string
  username: string
  email: string
  avatar?: string
  nickname?: string
  roles: string[]
  permissions: string[]
  lastLoginAt?: string
}

/**
 * 权限信息
 */
export interface Permission {
  id: string
  name: string
  code: string
  type: 'menu' | 'button' | 'api'
  resource?: string
  action?: string
  description?: string
}

/**
 * 角色信息
 */
export interface Role {
  id: string
  name: string
  code: string
  description?: string
  permissions: Permission[]
  status: 'active' | 'inactive'
}

/**
 * 文件信息
 */
export interface FileInfo {
  id?: string
  name: string
  url: string
  size: number
  type: string
  extension: string
  uploadAt?: string
}

/**
 * 统计图表数据点
 */
export interface ChartDataPoint {
  label: string
  value: number
  color?: string
}

/**
 * 趋势数据点
 */
export interface TrendDataPoint {
  date: string
  value: number
  change?: number
  changePercent?: number
}

/**
 * 通知消息
 */
export interface Notification {
  id: string
  title: string
  content: string
  type: 'info' | 'success' | 'warning' | 'error'
  read: boolean
  createdAt: string
  link?: string
}

/**
 * 系统设置
 */
export interface SystemSettings {
  siteName: string
  siteUrl: string
  logo?: string
  favicon?: string
  copyright: string
  icp?: string
  theme: 'light' | 'dark' | 'auto'
  language: string
  timezone: string
}

/**
 * 操作日志
 */
export interface OperationLog {
  id: string
  userId: string
  username: string
  action: string
  module: string
  description: string
  ip: string
  userAgent: string
  status: 'success' | 'failed'
  createdAt: string
}

/**
 * 地址信息
 */
export interface Address {
  country: string
  region: string
  city: string
  address: string
  zipCode?: string
  latitude?: number
  longitude?: number
}