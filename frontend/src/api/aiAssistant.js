import request from '@/utils/request'

// AI营销助手相关API

// 关键词研究相关
export const analyzeKeywords = (data) => {
  return request({
    url: '/v1/ai/keywords/analyze',
    method: 'post',
    data
  })
}

export const getKeywordSuggestions = (params) => {
  return request({
    url: '/v1/ai/keywords/suggestions',
    method: 'get',
    params
  })
}

// 产品推荐相关
export const getProductRecommendations = (data) => {
  return request({
    url: '/v1/ai/products/recommendations',
    method: 'post',
    data
  })
}

export const getPersonalizedProducts = (customerId) => {
  return request({
    url: `/api/ai/products/personalized/${customerId}`,
    method: 'get'
  })
}

// 策略推荐相关
export const generateMarketingStrategy = (data) => {
  return request({
    url: '/v1/ai/strategy/generate',
    method: 'post',
    data
  })
}

export const getStrategyTemplates = (params) => {
  return request({
    url: '/v1/ai/strategy/templates',
    method: 'get',
    params
  })
}

// 内容生成相关
export const generateIntelligentContent = (data) => {
  return request({
    url: '/v1/ai/content/generate',
    method: 'post',
    data
  })
}

export const optimizeContent = (data) => {
  return request({
    url: '/v1/ai/content/optimize',
    method: 'post',
    data
  })
}

// 市场分析相关
export const analyzeMarketTrends = (params) => {
  return request({
    url: '/v1/ai/market/trends',
    method: 'get',
    params
  })
}

export const getCompetitorAnalysis = (params) => {
  return request({
    url: '/v1/ai/market/competitors',
    method: 'get',
    params
  })
}

// 客户洞察相关
export const getCustomerInsights = (customerId) => {
  return request({
    url: `/api/ai/insights/customer/${customerId}`,
    method: 'get'
  })
}

export const predictCustomerBehavior = (data) => {
  return request({
    url: '/v1/ai/insights/predict',
    method: 'post',
    data
  })
}

// AI助手对话相关
export const askAIAssistant = (data) => {
  return request({
    url: '/v1/ai/assistant/chat',
    method: 'post',
    data
  })
}

export const getAIRecommendations = (params) => {
  return request({
    url: '/v1/ai/assistant/recommendations',
    method: 'get',
    params
  })
}