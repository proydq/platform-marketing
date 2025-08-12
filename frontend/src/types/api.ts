// API 相关类型定义

/**
 * 统一响应结构
 */
export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
}

/**
 * 分页请求参数
 */
export interface PageRequest {
  page?: number
  size?: number
  keyword?: string
  [key: string]: any
}

/**
 * 分页响应数据
 */
export interface PageResponse<T = any> {
  total: number
  list: T[]
}

/**
 * 排序参数
 */
export interface SortParam {
  field: string
  order: 'asc' | 'desc'
}

/**
 * 批量操作请求
 */
export interface BatchRequest {
  ids: string[]
  action?: string
  [key: string]: any
}

/**
 * 文件上传响应
 */
export interface UploadResponse {
  url: string
  filename: string
  size: number
  type: string
}

/**
 * 导出请求参数
 */
export interface ExportRequest {
  format?: 'xlsx' | 'csv' | 'pdf'
  filters?: Record<string, any>
  columns?: string[]
}