import { defineStore } from 'pinia'
import { 
  // 客户管理 API
  getCustomerList,
  getCustomer,
  createCustomer,
  updateCustomer,
  deleteCustomer,
  updateCustomerStatus,
  importCustomers,
  exportCustomers,
  getCustomerSources,
  
  // 客户获取/采集 API
  getAcquisitionTasks,
  getAcquisitionTask,
  createAcquisitionTask,
  updateAcquisitionTask,
  deleteAcquisitionTask,
  startAcquisitionTask,
  stopAcquisitionTask,
  createQuickAcquisition,
  getAcquisitionPreview,
  exportTaskData,
  
  // 客户分析 API
  getCustomerAnalytics,
  getGrowthTrendData,
  getSourceDistributionData,
  getStatusDistributionData,
  getConversionRateData,
  getAnalyticsTableData,
  exportAnalyticsReport
} from '@/api/customer'

export const useCustomerStore = defineStore('customer', {
  state: () => ({
    // 客户管理相关状态
    customerList: [],
    currentCustomer: null,
    total: 0,
    loading: false,
    sourcesOptions: [],
    
    // 客户获取相关状态
    acquisitionTasks: [],
    currentTask: null,
    acquisitionTotal: 0,
    acquisitionLoading: false,
    
    // 缓存配置
    cache: {
      customerList: null,
      customerListExpiry: null,
      sourcesOptions: null,
      sourcesOptionsExpiry: null,
    },
    
    // 缓存有效时间 (5分钟)
    cacheTimeout: 5 * 60 * 1000
  }),
  
  getters: {
    // 客户管理相关 getters
    activeCustomers: (state) => {
      return state.customerList.filter(customer => customer.status === 'active')
    },
    
    inactiveCustomers: (state) => {
      return state.customerList.filter(customer => customer.status === 'inactive')
    },
    
    customersBySource: (state) => {
      const sourceMap = {}
      state.customerList.forEach(customer => {
        const source = customer.source || 'unknown'
        if (!sourceMap[source]) {
          sourceMap[source] = []
        }
        sourceMap[source].push(customer)
      })
      return sourceMap
    },
    
    // 客户获取相关 getters
    runningTasks: (state) => {
      return state.acquisitionTasks.filter(task => task.status === 'running')
    },
    
    completedTasks: (state) => {
      return state.acquisitionTasks.filter(task => task.status === 'completed')
    },
    
    tasksByPlatform: (state) => {
      const platformMap = {}
      state.acquisitionTasks.forEach(task => {
        const platforms = Array.isArray(task.platform) 
          ? task.platform 
          : (task.platform ? task.platform.split(',') : [])
        
        platforms.forEach(platform => {
          if (!platformMap[platform]) {
            platformMap[platform] = []
          }
          platformMap[platform].push(task)
        })
      })
      return platformMap
    },
    
    // 缓存相关 getters
    isCustomerListCached: (state) => {
      return state.cache.customerList && 
             state.cache.customerListExpiry && 
             Date.now() < state.cache.customerListExpiry
    },
    
    isSourcesOptionsCached: (state) => {
      return state.cache.sourcesOptions && 
             state.cache.sourcesOptionsExpiry && 
             Date.now() < state.cache.sourcesOptionsExpiry
    }
  },
  
  actions: {
    // ========== 客户管理相关 Actions ==========
    
    /**
     * 获取客户列表
     */
    async fetchCustomerList(params = {}, useCache = true) {
      // 检查缓存
      if (useCache && this.isCustomerListCached && !params.refresh) {
        this.customerList = this.cache.customerList
        return this.customerList
      }
      
      this.loading = true
      try {
        const response = await getCustomerList(params)
        const data = response.data || response
        
        this.customerList = data.rows || data.list || []
        this.total = data.total || 0
        
        // 更新缓存
        this.cache.customerList = this.customerList
        this.cache.customerListExpiry = Date.now() + this.cacheTimeout
        
        return this.customerList
      } catch (error) {
        console.error('获取客户列表失败:', error)
        throw error
      } finally {
        this.loading = false
      }
    },
    
    /**
     * 获取客户详情
     */
    async fetchCustomer(id) {
      try {
        const response = await getCustomer(id)
        this.currentCustomer = response.data || response
        return this.currentCustomer
      } catch (error) {
        console.error('获取客户详情失败:', error)
        throw error
      }
    },
    
    /**
     * 创建客户
     */
    async createCustomer(customerData) {
      try {
        const response = await createCustomer(customerData)
        // 创建成功后刷新列表
        await this.fetchCustomerList({ refresh: true }, false)
        return response.data || response
      } catch (error) {
        console.error('创建客户失败:', error)
        throw error
      }
    },
    
    /**
     * 更新客户
     */
    async updateCustomer(id, customerData) {
      try {
        const response = await updateCustomer(id, customerData)
        
        // 更新本地状态
        const index = this.customerList.findIndex(item => item.id === id)
        if (index !== -1) {
          this.customerList[index] = { ...this.customerList[index], ...customerData }
        }
        
        // 清除缓存，确保下次获取最新数据
        this.clearCustomerListCache()
        
        return response.data || response
      } catch (error) {
        console.error('更新客户失败:', error)
        throw error
      }
    },
    
    /**
     * 删除客户
     */
    async deleteCustomer(id) {
      try {
        const response = await deleteCustomer(id)
        
        // 从本地状态中移除
        this.customerList = this.customerList.filter(item => item.id !== id)
        this.total -= 1
        
        // 清除缓存
        this.clearCustomerListCache()
        
        return response.data || response
      } catch (error) {
        console.error('删除客户失败:', error)
        throw error
      }
    },
    
    /**
     * 更新客户状态
     */
    async updateCustomerStatus(id, status) {
      try {
        const response = await updateCustomerStatus(id, status)
        
        // 更新本地状态
        const customer = this.customerList.find(item => item.id === id)
        if (customer) {
          customer.status = status
        }
        
        return response.data || response
      } catch (error) {
        console.error('更新客户状态失败:', error)
        throw error
      }
    },
    
    /**
     * 导入客户数据
     */
    async importCustomers(file) {
      try {
        const response = await importCustomers(file)
        // 导入成功后刷新列表
        await this.fetchCustomerList({ refresh: true }, false)
        return response.data || response
      } catch (error) {
        console.error('导入客户失败:', error)
        throw error
      }
    },
    
    /**
     * 导出客户数据
     */
    async exportCustomers(params) {
      try {
        const response = await exportCustomers(params)
        
        // 创建下载链接
        const blob = new Blob([response], { 
          type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' 
        })
        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        link.download = `customers_${new Date().getTime()}.xlsx`
        link.click()
        window.URL.revokeObjectURL(url)
        
        return response
      } catch (error) {
        console.error('导出客户失败:', error)
        throw error
      }
    },
    
    /**
     * 获取客户来源选项
     */
    async fetchSourcesOptions(useCache = true) {
      // 检查缓存
      if (useCache && this.isSourcesOptionsCached) {
        this.sourcesOptions = this.cache.sourcesOptions
        return this.sourcesOptions
      }
      
      try {
        const response = await getCustomerSources()
        this.sourcesOptions = response.data || response || []
        
        // 更新缓存
        this.cache.sourcesOptions = this.sourcesOptions
        this.cache.sourcesOptionsExpiry = Date.now() + this.cacheTimeout
        
        return this.sourcesOptions
      } catch (error) {
        console.error('获取客户来源选项失败:', error)
        // 设置默认值
        this.sourcesOptions = ['LinkedIn', 'Facebook', 'Google Maps', 'Yellow Pages', 'Website']
        return this.sourcesOptions
      }
    },
    
    // ========== 客户获取相关 Actions ==========
    
    /**
     * 获取采集任务列表
     */
    async fetchAcquisitionTasks(params = {}) {
      this.acquisitionLoading = true
      try {
        const response = await getAcquisitionTasks(params)
        const data = response.data || response
        
        this.acquisitionTasks = data.rows || data.list || []
        this.acquisitionTotal = data.total || 0
        
        return this.acquisitionTasks
      } catch (error) {
        console.error('获取采集任务失败:', error)
        throw error
      } finally {
        this.acquisitionLoading = false
      }
    },
    
    /**
     * 获取采集任务详情
     */
    async fetchAcquisitionTask(id) {
      try {
        const response = await getAcquisitionTask(id)
        this.currentTask = response.data || response
        return this.currentTask
      } catch (error) {
        console.error('获取采集任务详情失败:', error)
        throw error
      }
    },
    
    /**
     * 创建采集任务
     */
    async createAcquisitionTask(taskData) {
      try {
        const response = await createAcquisitionTask(taskData)
        // 创建成功后刷新列表
        await this.fetchAcquisitionTasks()
        return response.data || response
      } catch (error) {
        console.error('创建采集任务失败:', error)
        throw error
      }
    },
    
    /**
     * 更新采集任务
     */
    async updateAcquisitionTask(id, taskData) {
      try {
        const response = await updateAcquisitionTask(id, taskData)
        
        // 更新本地状态
        const index = this.acquisitionTasks.findIndex(item => item.id === id)
        if (index !== -1) {
          this.acquisitionTasks[index] = { ...this.acquisitionTasks[index], ...taskData }
        }
        
        return response.data || response
      } catch (error) {
        console.error('更新采集任务失败:', error)
        throw error
      }
    },
    
    /**
     * 删除采集任务
     */
    async deleteAcquisitionTask(id) {
      try {
        const response = await deleteAcquisitionTask(id)
        
        // 从本地状态中移除
        this.acquisitionTasks = this.acquisitionTasks.filter(item => item.id !== id)
        this.acquisitionTotal -= 1
        
        return response.data || response
      } catch (error) {
        console.error('删除采集任务失败:', error)
        throw error
      }
    },
    
    /**
     * 启动采集任务
     */
    async startAcquisitionTask(id) {
      try {
        const response = await startAcquisitionTask(id)
        
        // 更新任务状态
        const task = this.acquisitionTasks.find(item => item.id === id)
        if (task) {
          task.status = 'running'
        }
        
        return response.data || response
      } catch (error) {
        console.error('启动采集任务失败:', error)
        throw error
      }
    },
    
    /**
     * 停止采集任务
     */
    async stopAcquisitionTask(id) {
      try {
        const response = await stopAcquisitionTask(id)
        
        // 更新任务状态
        const task = this.acquisitionTasks.find(item => item.id === id)
        if (task) {
          task.status = 'paused'
        }
        
        return response.data || response
      } catch (error) {
        console.error('停止采集任务失败:', error)
        throw error
      }
    },
    
    /**
     * 创建快速采集任务
     */
    async createQuickAcquisition(config) {
      try {
        const response = await createQuickAcquisition(config)
        // 刷新任务列表
        await this.fetchAcquisitionTasks()
        return response.data || response
      } catch (error) {
        console.error('创建快速采集失败:', error)
        throw error
      }
    },
    
    /**
     * 获取预览数据
     */
    async getPreviewData(params = {}) {
      try {
        const response = await getAcquisitionPreview(params)
        return response.data || response || []
      } catch (error) {
        console.error('获取预览数据失败:', error)
        // 返回模拟数据
        return [
          { name: 'John Doe', email: 'john@example.com', source: 'LinkedIn', time: '2024-01-01' },
          { name: 'Jane Smith', email: 'jane@example.com', source: 'Facebook', time: '2024-01-02' },
          { name: 'Bob Johnson', email: 'bob@example.com', source: 'Google Maps', time: '2024-01-03' }
        ]
      }
    },
    
    /**
     * 导出任务数据
     */
    async exportTaskData(taskId) {
      try {
        const response = await exportTaskData(taskId)
        
        // 创建下载链接
        const blob = new Blob([response], { 
          type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' 
        })
        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        link.download = `task_data_${taskId}_${new Date().getTime()}.xlsx`
        link.click()
        window.URL.revokeObjectURL(url)
        
        return response
      } catch (error) {
        console.error('导出任务数据失败:', error)
        throw error
      }
    },
    
    // ========== 客户分析相关 Actions ==========
    
    /**
     * 获取客户分析数据
     */
    async getCustomerAnalytics(params) {
      try {
        const response = await getCustomerAnalytics(params)
        return response.data || response
      } catch (error) {
        console.error('获取客户分析数据失败:', error)
        throw error
      }
    },
    
    /**
     * 获取增长趋势数据
     */
    async getGrowthTrendData(params) {
      try {
        const response = await getGrowthTrendData(params)
        return response.data || response || []
      } catch (error) {
        console.error('获取增长趋势数据失败:', error)
        throw error
      }
    },
    
    /**
     * 获取来源分布数据
     */
    async getSourceDistributionData(params) {
      try {
        const response = await getSourceDistributionData(params)
        return response.data || response || []
      } catch (error) {
        console.error('获取来源分布数据失败:', error)
        throw error
      }
    },
    
    /**
     * 获取状态分布数据
     */
    async getStatusDistributionData(params) {
      try {
        const response = await getStatusDistributionData(params)
        return response.data || response || []
      } catch (error) {
        console.error('获取状态分布数据失败:', error)
        throw error
      }
    },
    
    /**
     * 获取转化率数据
     */
    async getConversionRateData(params) {
      try {
        const response = await getConversionRateData(params)
        return response.data || response || []
      } catch (error) {
        console.error('获取转化率数据失败:', error)
        throw error
      }
    },
    
    /**
     * 获取分析表格数据
     */
    async getAnalyticsTableData(params) {
      try {
        const response = await getAnalyticsTableData(params)
        return response.data || response || []
      } catch (error) {
        console.error('获取分析表格数据失败:', error)
        throw error
      }
    },
    
    /**
     * 导出分析报告
     */
    async exportAnalyticsReport(params) {
      try {
        const response = await exportAnalyticsReport(params)
        
        // 创建下载链接
        const blob = new Blob([response], { 
          type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' 
        })
        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        link.download = `customer_analytics_${new Date().getTime()}.xlsx`
        link.click()
        window.URL.revokeObjectURL(url)
        
        return response
      } catch (error) {
        console.error('导出分析报告失败:', error)
        throw error
      }
    },
    
    // ========== 缓存管理 Actions ==========
    
    /**
     * 清除客户列表缓存
     */
    clearCustomerListCache() {
      this.cache.customerList = null
      this.cache.customerListExpiry = null
    },
    
    /**
     * 清除来源选项缓存
     */
    clearSourcesOptionsCache() {
      this.cache.sourcesOptions = null
      this.cache.sourcesOptionsExpiry = null
    },
    
    /**
     * 清除所有缓存
     */
    clearAllCache() {
      this.cache = {
        customerList: null,
        customerListExpiry: null,
        sourcesOptions: null,
        sourcesOptionsExpiry: null,
      }
    },
    
    /**
     * 重置所有状态
     */
    resetState() {
      this.customerList = []
      this.currentCustomer = null
      this.total = 0
      this.loading = false
      this.sourcesOptions = []
      this.acquisitionTasks = []
      this.currentTask = null
      this.acquisitionTotal = 0
      this.acquisitionLoading = false
      this.clearAllCache()
    }
  }
})