<template>
  <div class="ai-marketing-assistant">
    <div class="page-header">
      <h1>🤖 AI营销助手</h1>
      <p>关键词研究、内容智能生成、产品与策略推荐</p>
    </div>

    <!-- 功能导航标签 -->
    <el-tabs v-model="activeTab" class="assistant-tabs">
      <!-- 1. 关键词研究 -->
      <el-tab-pane label="🔍 关键词研究" name="keywords">
        <div class="keywords-research">
          <el-card class="research-card">
            <template #header>
              <div class="card-header">
                <span>关键词分析与研究</span>
                <el-button type="primary" @click="analyzeKeywords">
                  开始分析
                </el-button>
              </div>
            </template>

            <div class="research-form">
              <el-row :gutter="20">
                <el-col :span="12">
                  <el-form-item label="目标行业">
                    <el-select v-model="keywordForm.industry" placeholder="选择行业">
                      <el-option label="电子产品" value="electronics" />
                      <el-option label="服装时尚" value="fashion" />
                      <el-option label="家居用品" value="home" />
                      <el-option label="健康美容" value="beauty" />
                      <el-option label="汽车配件" value="automotive" />
                      <el-option label="工业设备" value="industrial" />
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="目标市场">
                    <el-select v-model="keywordForm.market" placeholder="选择市场">
                      <el-option label="美国" value="US" />
                      <el-option label="欧洲" value="EU" />
                      <el-option label="亚太" value="APAC" />
                      <el-option label="全球" value="Global" />
                    </el-select>
                  </el-form-item>
                </el-col>
              </el-row>

              <el-form-item label="种子关键词">
                <el-input
                  v-model="keywordForm.seedKeywords"
                  type="textarea"
                  :rows="3"
                  placeholder="输入种子关键词，用逗号分隔（如：手机壳, 保护套, 手机配件）"
                />
              </el-form-item>

              <el-form-item label="分析目标">
                <el-checkbox-group v-model="keywordForm.analysisGoals">
                  <el-checkbox label="search_volume">搜索量分析</el-checkbox>
                  <el-checkbox label="competition">竞争度分析</el-checkbox>
                  <el-checkbox label="trends">趋势分析</el-checkbox>
                  <el-checkbox label="related">相关词推荐</el-checkbox>
                  <el-checkbox label="long_tail">长尾词挖掘</el-checkbox>
                </el-checkbox-group>
              </el-form-item>
            </div>

            <!-- 关键词分析结果 -->
            <div v-if="keywordResults.length > 0" class="keyword-results">
              <h3>🎯 关键词分析结果</h3>
              <el-table :data="keywordResults" style="width: 100%">
                <el-table-column prop="keyword" label="关键词" width="200" />
                <el-table-column prop="searchVolume" label="月搜索量" width="120">
                  <template #default="{ row }">
                    <el-tag type="primary">{{ row.searchVolume }}</el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="competition" label="竞争度" width="120">
                  <template #default="{ row }">
                    <el-tag 
                      :type="getCompetitionType(row.competition)"
                    >
                      {{ row.competition }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="trend" label="趋势" width="100">
                  <template #default="{ row }">
                    <span :class="getTrendClass(row.trend)">
                      {{ row.trend > 0 ? '↗️' : row.trend < 0 ? '↘️' : '➡️' }}
                    </span>
                  </template>
                </el-table-column>
                <el-table-column prop="difficulty" label="SEO难度" width="120">
                  <template #default="{ row }">
                    <el-progress 
                      :percentage="row.difficulty" 
                      :color="getDifficultyColor(row.difficulty)"
                    />
                  </template>
                </el-table-column>
                <el-table-column label="建议" min-width="200">
                  <template #default="{ row }">
                    <span class="suggestion">{{ row.suggestion }}</span>
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="120">
                  <template #default="{ row }">
                    <el-button size="small" @click="addToStrategy(row)">
                      加入策略
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </el-card>
        </div>
      </el-tab-pane>

      <!-- 2. 产品推荐 -->
      <el-tab-pane label="📦 产品推荐" name="products">
        <div class="product-recommendations">
          <el-row :gutter="20">
            <el-col :span="8">
              <el-card class="recommendation-card">
                <template #header>
                  <span>🎯 智能产品推荐</span>
                </template>
                
                <el-form :model="productForm" label-width="100px">
                  <el-form-item label="客户类型">
                    <el-select v-model="productForm.customerType">
                      <el-option label="新客户" value="new" />
                      <el-option label="老客户" value="existing" />
                      <el-option label="VIP客户" value="vip" />
                      <el-option label="流失客户" value="churned" />
                    </el-select>
                  </el-form-item>

                  <el-form-item label="购买历史">
                    <el-input 
                      v-model="productForm.purchaseHistory"
                      type="textarea"
                      :rows="3"
                      placeholder="描述客户的购买历史和偏好"
                    />
                  </el-form-item>

                  <el-form-item label="预算范围">
                    <el-slider
                      v-model="productForm.budgetRange"
                      range
                      :min="0"
                      :max="10000"
                      :step="100"
                      show-stops
                    />
                    <div class="budget-labels">
                      <span>${{ productForm.budgetRange[0] }}</span>
                      <span>${{ productForm.budgetRange[1] }}</span>
                    </div>
                  </el-form-item>

                  <el-button type="primary" @click="getProductRecommendations" block>
                    获取推荐
                  </el-button>
                </el-form>
              </el-card>
            </el-col>

            <el-col :span="16">
              <el-card class="recommendations-display">
                <template #header>
                  <span>🎁 推荐产品列表</span>
                </template>

                <div v-if="productRecommendations.length > 0" class="recommendations-grid">
                  <div 
                    v-for="product in productRecommendations" 
                    :key="product.id"
                    class="product-recommendation-item"
                  >
                    <div class="product-image">
                      <img :src="product.image || '/default-product.png'" :alt="product.name">
                    </div>
                    <div class="product-info">
                      <h4>{{ product.name }}</h4>
                      <div class="product-price">${{ product.price }}</div>
                      <div class="match-score">
                        <span>匹配度: </span>
                        <el-rate 
                          v-model="product.matchScore" 
                          disabled 
                          show-score 
                          text-color="#ff9900"
                        />
                      </div>
                      <div class="recommendation-reason">
                        <el-tag size="small" type="info">{{ product.reason }}</el-tag>
                      </div>
                    </div>
                    <div class="product-actions">
                      <el-button size="small" @click="addToQuote(product)">加入报价</el-button>
                      <el-button size="small" type="primary" @click="generateContent(product)">
                        生成内容
                      </el-button>
                    </div>
                  </div>
                </div>

                <div v-else class="empty-recommendations">
                  <el-empty description="请先设置推荐条件" />
                </div>
              </el-card>
            </el-col>
          </el-row>
        </div>
      </el-tab-pane>

      <!-- 3. 策略推荐 -->
      <el-tab-pane label="🧠 策略推荐" name="strategy">
        <div class="strategy-recommendations">
          <el-card class="strategy-card">
            <template #header>
              <div class="card-header">
                <span>💡 智能营销策略</span>
                <el-button type="primary" @click="generateStrategy">
                  生成策略
                </el-button>
              </div>
            </template>

            <el-form :model="strategyForm" :inline="true" class="strategy-form">
              <el-form-item label="营销目标">
                <el-select v-model="strategyForm.goal">
                  <el-option label="品牌认知" value="awareness" />
                  <el-option label="潜客获取" value="lead_generation" />
                  <el-option label="销售转化" value="conversion" />
                  <el-option label="客户留存" value="retention" />
                  <el-option label="客户复购" value="repeat_purchase" />
                </el-select>
              </el-form-item>

              <el-form-item label="目标受众">
                <el-select v-model="strategyForm.audience">
                  <el-option label="年轻消费者(18-35)" value="young_consumers" />
                  <el-option label="中年专业人士(35-50)" value="professionals" />
                  <el-option label="企业采购(B2B)" value="b2b_buyers" />
                  <el-option label="高端客户" value="premium_customers" />
                </el-select>
              </el-form-item>

              <el-form-item label="预算规模">
                <el-select v-model="strategyForm.budget">
                  <el-option label="小预算(<$1000)" value="small" />
                  <el-option label="中等预算($1000-$5000)" value="medium" />
                  <el-option label="大预算(>$5000)" value="large" />
                </el-select>
              </el-form-item>

              <el-form-item label="时间周期">
                <el-select v-model="strategyForm.timeline">
                  <el-option label="短期(1月内)" value="short" />
                  <el-option label="中期(1-3月)" value="medium" />
                  <el-option label="长期(3月以上)" value="long" />
                </el-select>
              </el-form-item>
            </el-form>

            <!-- 策略推荐结果 -->
            <div v-if="strategyRecommendations.length > 0" class="strategy-results">
              <h3>🎯 推荐策略方案</h3>
              <div class="strategy-cards">
                <el-card 
                  v-for="strategy in strategyRecommendations" 
                  :key="strategy.id"
                  class="strategy-item"
                  shadow="hover"
                >
                  <template #header>
                    <div class="strategy-header">
                      <span class="strategy-title">{{ strategy.title }}</span>
                      <el-tag :type="getStrategyType(strategy.priority)">
                        {{ strategy.priority }}
                      </el-tag>
                    </div>
                  </template>

                  <div class="strategy-content">
                    <p class="strategy-description">{{ strategy.description }}</p>
                    
                    <div class="strategy-channels">
                      <span class="label">推荐渠道:</span>
                      <el-tag 
                        v-for="channel in strategy.channels" 
                        :key="channel"
                        size="small"
                        class="channel-tag"
                      >
                        {{ getChannelName(channel) }}
                      </el-tag>
                    </div>

                    <div class="strategy-metrics">
                      <div class="metric-item">
                        <span class="metric-label">预期ROI:</span>
                        <span class="metric-value">{{ strategy.expectedROI }}%</span>
                      </div>
                      <div class="metric-item">
                        <span class="metric-label">执行难度:</span>
                        <el-rate 
                          v-model="strategy.difficulty" 
                          disabled 
                          max="5"
                        />
                      </div>
                    </div>

                    <div class="strategy-actions">
                      <el-button size="small" @click="viewStrategyDetails(strategy)">
                        查看详情
                      </el-button>
                      <el-button size="small" type="primary" @click="implementStrategy(strategy)">
                        立即执行
                      </el-button>
                    </div>
                  </div>
                </el-card>
              </div>
            </div>
          </el-card>
        </div>
      </el-tab-pane>

      <!-- 4. 智能内容生成 -->
      <el-tab-pane label="✍️ 智能内容" name="content">
        <div class="intelligent-content">
          <el-row :gutter="20">
            <el-col :span="10">
              <el-card class="content-generator">
                <template #header>
                  <span>🤖 AI内容生成器</span>
                </template>

                <el-form :model="contentForm" label-width="100px">
                  <el-form-item label="内容类型">
                    <el-select v-model="contentForm.type">
                      <el-option label="产品介绍邮件" value="product_email" />
                      <el-option label="促销邮件" value="promotion_email" />
                      <el-option label="社交媒体文案" value="social_post" />
                      <el-option label="产品描述" value="product_description" />
                      <el-option label="客户回复" value="customer_reply" />
                    </el-select>
                  </el-form-item>

                  <el-form-item label="目标客户">
                    <el-select v-model="contentForm.customerSegment">
                      <el-option label="新客户" value="new_customer" />
                      <el-option label="潜在客户" value="prospect" />
                      <el-option label="老客户" value="existing_customer" />
                      <el-option label="VIP客户" value="vip_customer" />
                    </el-select>
                  </el-form-item>

                  <el-form-item label="语调风格">
                    <el-select v-model="contentForm.tone">
                      <el-option label="专业正式" value="professional" />
                      <el-option label="友好亲切" value="friendly" />
                      <el-option label="热情推销" value="enthusiastic" />
                      <el-option label="简洁直接" value="concise" />
                    </el-select>
                  </el-form-item>

                  <el-form-item label="关键信息">
                    <el-input 
                      v-model="contentForm.keyPoints"
                      type="textarea"
                      :rows="4"
                      placeholder="输入想要突出的关键信息、产品特点或优惠信息"
                    />
                  </el-form-item>

                  <el-form-item label="目标语言">
                    <el-select v-model="contentForm.language">
                      <el-option label="中文" value="zh" />
                      <el-option label="English" value="en" />
                      <el-option label="Español" value="es" />
                      <el-option label="Français" value="fr" />
                      <el-option label="Deutsch" value="de" />
                    </el-select>
                  </el-form-item>

                  <el-button type="primary" @click="generateIntelligentContent" block>
                    🚀 生成内容
                  </el-button>
                </el-form>
              </el-card>
            </el-col>

            <el-col :span="14">
              <el-card class="content-preview">
                <template #header>
                  <div class="preview-header">
                    <span>📝 生成内容预览</span>
                    <div v-if="generatedContent">
                      <el-button size="small" @click="copyContent">复制</el-button>
                      <el-button size="small" type="primary" @click="saveContent">保存</el-button>
                    </div>
                  </div>
                </template>

                <div v-if="generatedContent" class="content-display">
                  <div class="content-meta">
                    <el-tag size="small">{{ getContentTypeName(contentForm.type) }}</el-tag>
                    <el-tag size="small" type="info">{{ contentForm.language.toUpperCase() }}</el-tag>
                    <span class="word-count">字数: {{ getWordCount(generatedContent) }}</span>
                  </div>

                  <div class="content-body">
                    <div v-if="contentForm.type.includes('email')" class="email-preview">
                      <div class="email-subject">
                        <strong>主题: </strong>{{ generatedContent.subject }}
                      </div>
                      <div class="email-body">
                        <pre>{{ generatedContent.body }}</pre>
                      </div>
                    </div>
                    <div v-else class="text-preview">
                      <pre>{{ generatedContent }}</pre>
                    </div>
                  </div>

                  <div class="content-suggestions">
                    <h4>💡 优化建议</h4>
                    <ul>
                      <li v-for="suggestion in contentSuggestions" :key="suggestion">
                        {{ suggestion }}
                      </li>
                    </ul>
                  </div>
                </div>

                <div v-else class="empty-content">
                  <el-empty description="请先配置参数并生成内容" />
                </div>
              </el-card>
            </el-col>
          </el-row>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { ElMessage } from 'element-plus'

// 当前活跃标签
const activeTab = ref('keywords')

// 关键词研究相关数据
const keywordForm = reactive({
  industry: '',
  market: '',
  seedKeywords: '',
  analysisGoals: []
})

const keywordResults = ref([])

// 产品推荐相关数据
const productForm = reactive({
  customerType: '',
  purchaseHistory: '',
  budgetRange: [500, 2000]
})

const productRecommendations = ref([])

// 策略推荐相关数据
const strategyForm = reactive({
  goal: '',
  audience: '',
  budget: '',
  timeline: ''
})

const strategyRecommendations = ref([])

// 内容生成相关数据
const contentForm = reactive({
  type: '',
  customerSegment: '',
  tone: '',
  keyPoints: '',
  language: 'en'
})

const generatedContent = ref(null)
const contentSuggestions = ref([])

// 关键词分析功能
const analyzeKeywords = async () => {
  if (!keywordForm.seedKeywords) {
    ElMessage.warning('请输入种子关键词')
    return
  }

  ElMessage.info('正在分析关键词...')
  
  // 模拟API调用
  setTimeout(() => {
    keywordResults.value = [
      {
        keyword: 'phone case',
        searchVolume: '12,000',
        competition: '中等',
        trend: 15,
        difficulty: 65,
        suggestion: '适合新品推广，竞争适中'
      },
      {
        keyword: 'protective case',
        searchVolume: '8,500',
        competition: '低',
        trend: 25,
        difficulty: 45,
        suggestion: '机会关键词，建议重点优化'
      },
      {
        keyword: 'mobile accessories',
        searchVolume: '15,000',
        competition: '高',
        trend: -5,
        difficulty: 80,
        suggestion: '竞争激烈，需长期投入'
      }
    ]
    ElMessage.success('关键词分析完成')
  }, 2000)
}

// 获取产品推荐
const getProductRecommendations = async () => {
  ElMessage.info('正在分析客户需求...')
  
  setTimeout(() => {
    productRecommendations.value = [
      {
        id: 1,
        name: '高端真皮手机壳',
        price: 89,
        image: '/product1.jpg',
        matchScore: 4.5,
        reason: '基于客户偏好推荐'
      },
      {
        id: 2,
        name: '防摔透明保护套',
        price: 29,
        image: '/product2.jpg',
        matchScore: 4.2,
        reason: '价格区间匹配'
      },
      {
        id: 3,
        name: '无线充电手机支架',
        price: 65,
        image: '/product3.jpg',
        matchScore: 3.8,
        reason: '相关产品推荐'
      }
    ]
    ElMessage.success('产品推荐生成完成')
  }, 1500)
}

// 生成营销策略
const generateStrategy = async () => {
  ElMessage.info('正在生成策略方案...')
  
  setTimeout(() => {
    strategyRecommendations.value = [
      {
        id: 1,
        title: '邮件营销组合策略',
        description: '针对目标客户群体，通过个性化邮件序列提升转化率',
        priority: '高优先级',
        channels: ['email', 'social'],
        expectedROI: 230,
        difficulty: 3
      },
      {
        id: 2,
        title: '社交媒体内容营销',
        description: '利用视觉内容和用户生成内容提升品牌认知度',
        priority: '中优先级',
        channels: ['social', 'content'],
        expectedROI: 180,
        difficulty: 2
      }
    ]
    ElMessage.success('策略方案生成完成')
  }, 2000)
}

// 生成智能内容
const generateIntelligentContent = async () => {
  if (!contentForm.type || !contentForm.keyPoints) {
    ElMessage.warning('请完善内容生成参数')
    return
  }

  ElMessage.info('AI正在生成内容...')
  
  setTimeout(() => {
    if (contentForm.type.includes('email')) {
      generatedContent.value = {
        subject: '您专属的产品推荐 - 限时优惠',
        body: `亲爱的客户，

基于您的偏好和购买历史，我们为您精选了以下产品：

${contentForm.keyPoints}

这些产品完美符合您的需求，现在下单还可享受15%的专属折扣。

期待您的回复！

最诚挚的问候`
      }
    } else {
      generatedContent.value = `🌟 ${contentForm.keyPoints}

专为您定制的优质产品，现已上线！立即了解更多详情，享受限时优惠。

#优质产品 #限时优惠 #专属定制`
    }

    contentSuggestions.value = [
      '建议添加更具体的产品特点描述',
      '可以增加紧迫感的表达',
      '建议加入客户评价或社会证明'
    ]

    ElMessage.success('内容生成完成')
  }, 3000)
}

// 辅助函数
const getCompetitionType = (competition) => {
  const types = { '低': 'success', '中等': 'warning', '高': 'danger' }
  return types[competition] || 'info'
}

const getTrendClass = (trend) => {
  return trend > 0 ? 'trend-up' : trend < 0 ? 'trend-down' : 'trend-stable'
}

const getDifficultyColor = (difficulty) => {
  if (difficulty < 30) return '#67c23a'
  if (difficulty < 70) return '#e6a23c'
  return '#f56c6c'
}

const getStrategyType = (priority) => {
  const types = { '高优先级': 'danger', '中优先级': 'warning', '低优先级': 'info' }
  return types[priority] || 'info'
}

const getChannelName = (channel) => {
  const names = {
    email: '邮件',
    social: '社交媒体',
    content: '内容营销',
    ads: '付费广告'
  }
  return names[channel] || channel
}

const getContentTypeName = (type) => {
  const names = {
    product_email: '产品介绍邮件',
    promotion_email: '促销邮件',
    social_post: '社交媒体文案',
    product_description: '产品描述',
    customer_reply: '客户回复'
  }
  return names[type] || type
}

const getWordCount = (content) => {
  if (typeof content === 'string') {
    return content.length
  }
  if (content && content.body) {
    return content.body.length
  }
  return 0
}

// 操作函数
const addToStrategy = (keyword) => {
  ElMessage.success(`关键词 "${keyword.keyword}" 已加入营销策略`)
}

const addToQuote = (product) => {
  ElMessage.success(`产品 "${product.name}" 已加入报价单`)
}

const generateContent = (product) => {
  ElMessage.info(`正在为产品 "${product.name}" 生成营销内容...`)
}

const viewStrategyDetails = (strategy) => {
  ElMessage.info(`查看策略详情: ${strategy.title}`)
}

const implementStrategy = (strategy) => {
  ElMessage.success(`开始执行策略: ${strategy.title}`)
}

const copyContent = () => {
  navigator.clipboard.writeText(
    typeof generatedContent.value === 'string' 
      ? generatedContent.value 
      : generatedContent.value.body
  )
  ElMessage.success('内容已复制到剪贴板')
}

const saveContent = () => {
  ElMessage.success('内容已保存到内容库')
}
</script>

<style scoped>
.ai-marketing-assistant {
  padding: 24px;
}

.page-header {
  margin-bottom: 24px;
}

.page-header h1 {
  margin: 0 0 8px 0;
  font-size: 28px;
  font-weight: 600;
}

.page-header p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.assistant-tabs {
  background: white;
  border-radius: 8px;
  padding: 24px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.research-form {
  margin-bottom: 24px;
}

.keyword-results {
  margin-top: 24px;
}

.keyword-results h3 {
  margin-bottom: 16px;
}

.suggestion {
  font-size: 12px;
  color: #666;
}

.recommendation-card {
  height: 600px;
  overflow-y: auto;
}

.recommendations-display {
  height: 600px;
  overflow-y: auto;
}

.recommendations-grid {
  display: grid;
  gap: 16px;
}

.product-recommendation-item {
  display: flex;
  gap: 16px;
  padding: 16px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.product-recommendation-item:hover {
  border-color: #409EFF;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.1);
}

.product-image {
  width: 80px;
  height: 80px;
  flex-shrink: 0;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 4px;
}

.product-info {
  flex: 1;
}

.product-info h4 {
  margin: 0 0 8px 0;
  font-size: 16px;
}

.product-price {
  font-size: 18px;
  font-weight: 600;
  color: #e6a23c;
  margin-bottom: 8px;
}

.match-score {
  margin-bottom: 8px;
  font-size: 14px;
}

.recommendation-reason {
  margin-bottom: 8px;
}

.product-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
  flex-shrink: 0;
}

.budget-labels {
  display: flex;
  justify-content: space-between;
  margin-top: 8px;
  font-size: 12px;
  color: #666;
}

.strategy-form {
  margin-bottom: 24px;
}

.strategy-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 16px;
}

.strategy-item {
  height: 100%;
}

.strategy-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.strategy-title {
  font-weight: 600;
}

.strategy-content {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.strategy-description {
  margin-bottom: 16px;
  color: #666;
  line-height: 1.6;
}

.strategy-channels {
  margin-bottom: 16px;
}

.label {
  font-size: 14px;
  color: #666;
  margin-right: 8px;
}

.channel-tag {
  margin-right: 8px;
}

.strategy-metrics {
  display: flex;
  gap: 24px;
  margin-bottom: 16px;
}

.metric-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.metric-label {
  font-size: 14px;
  color: #666;
}

.metric-value {
  font-weight: 600;
  color: #67c23a;
}

.strategy-actions {
  margin-top: auto;
  display: flex;
  gap: 8px;
}

.content-generator {
  height: 600px;
  overflow-y: auto;
}

.content-preview {
  height: 600px;
  overflow-y: auto;
}

.preview-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.content-display {
  height: 100%;
}

.content-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 16px;
  padding-bottom: 8px;
  border-bottom: 1px solid #e0e0e0;
}

.word-count {
  margin-left: auto;
  font-size: 12px;
  color: #666;
}

.content-body {
  margin-bottom: 24px;
}

.email-preview {
  background: #f9f9f9;
  padding: 16px;
  border-radius: 8px;
}

.email-subject {
  margin-bottom: 16px;
  padding-bottom: 8px;
  border-bottom: 1px solid #e0e0e0;
}

.email-body pre {
  margin: 0;
  white-space: pre-wrap;
  font-family: inherit;
  line-height: 1.6;
}

.text-preview pre {
  margin: 0;
  white-space: pre-wrap;
  font-family: inherit;
  line-height: 1.6;
  background: #f9f9f9;
  padding: 16px;
  border-radius: 8px;
}

.content-suggestions h4 {
  margin-bottom: 12px;
  color: #333;
}

.content-suggestions ul {
  margin: 0;
  padding-left: 20px;
}

.content-suggestions li {
  margin-bottom: 8px;
  color: #666;
  line-height: 1.5;
}

.empty-content,
.empty-recommendations {
  height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.trend-up {
  color: #67c23a;
}

.trend-down {
  color: #f56c6c;
}

.trend-stable {
  color: #909399;
}
</style>