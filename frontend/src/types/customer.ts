// 客户相关类型定义

/**
 * 客户实体
 */
export interface Customer {
  id: string
  name: string
  email: string
  phone?: string
  company?: string
  source: string
  status: 'active' | 'inactive'
  industry?: string
  country?: string
  region?: string
  tags?: string[]
  notes?: string
  createdAt: string
  updatedAt: string
  lastContactAt?: string
}

/**
 * 客户创建请求
 */
export interface CustomerCreateRequest {
  name: string
  email: string
  phone?: string
  company?: string
  source: string
  industry?: string
  country?: string
  region?: string
  tags?: string[]
  notes?: string
}

/**
 * 客户更新请求
 */
export interface CustomerUpdateRequest extends Partial<CustomerCreateRequest> {
  id: string
}

/**
 * 客户搜索请求
 */
export interface CustomerSearchRequest {
  keyword?: string
  status?: string
  source?: string
  industry?: string
  country?: string
  dateRange?: [string, string]
  page?: number
  size?: number
}

/**
 * 客户状态更新请求
 */
export interface CustomerStatusUpdateRequest {
  id: string
  status: 'active' | 'inactive'
}

/**
 * 客户统计数据
 */
export interface CustomerStats {
  total: number
  active: number
  inactive: number
  newThisMonth: number
  growthRate: number
}

/**
 * 客户来源统计
 */
export interface CustomerSourceStats {
  source: string
  count: number
  percentage: number
}

/**
 * 客户导入结果
 */
export interface CustomerImportResult {
  total: number
  success: number
  failed: number
  errors?: Array<{
    row: number
    message: string
  }>
}