<template>
  <div class="page-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="page-title">
        <span class="page-icon">🤖</span>
        AI营销助手
      </div>
      <div class="page-subtitle">
        关键词研究、内容智能生成、产品与策略推荐，全方位AI营销解决方案
      </div>
      <div class="page-actions">
        <el-button type="primary" class="btn-primary">
          查看使用指南
        </el-button>
        <el-button class="btn-secondary">
          历史记录
        </el-button>
      </div>
    </div>

    <!-- 功能导航标签 -->
    <el-tabs v-model="activeTab" class="enhanced-tabs">
      <!-- 1. 关键词研究 -->
      <el-tab-pane label="🔍 关键词研究" name="keywords">
        <div class="keywords-research">
          <div class="data-table-wrapper">
            <div class="table-header">
              <div class="table-title">关键词分析与研究</div>
              <div class="table-subtitle">基于AI技术的深度关键词挖掘和竞争分析</div>
              <div class="table-actions">
                <el-button type="primary" class="btn-primary" @click="analyzeKeywords">
                  开始分析
                </el-button>
              </div>
            </div>

            <div class="research-form form-section">
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
              <div class="data-table-wrapper">
                <div class="table-header">
                  <div class="table-title">🎯 关键词分析结果</div>
                  <div class="table-subtitle">智能分析结果，助您精准定位营销关键词</div>
                </div>
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
            </div>
          </div>
        </div>
      </el-tab-pane>

      <!-- 2. 产品推荐 -->
      <el-tab-pane label="📦 产品推荐" name="products">
        <div class="product-recommendations">
          <el-row :gutter="20">
            <el-col :span="8">
              <div class="form-section">
                <div class="form-title">🎯 智能产品推荐</div>
                
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

                  <el-button type="primary" class="btn-primary" @click="getProductRecommendations" block>
                    获取推荐
                  </el-button>
                </el-form>
              </div>
            </el-col>

            <el-col :span="16">
              <div class="data-table-wrapper">
                <div class="table-header">
                  <div class="table-title">🎁 推荐产品列表</div>
                  <div class="table-subtitle">基于客户画像和行为数据的个性化产品推荐</div>
                </div>

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

                <div v-else class="empty-state">
                  <div class="empty-icon">🎁</div>
                  <div class="empty-title">请先设置推荐条件</div>
                  <div class="empty-description">在左侧配置客户信息和需求，系统将为您智能推荐适合的产品</div>
                </div>
              </div>
            </el-col>
          </el-row>
        </div>
      </el-tab-pane>

      <!-- 3. 策略推荐 -->
      <el-tab-pane label="🧠 策略推荐" name="strategy">
        <div class="strategy-recommendations">
          <div class="data-table-wrapper">
            <div class="table-header">
              <div class="table-title">💡 智能营销策略</div>
              <div class="table-subtitle">基于AI算法和大数据分析的智能营销策略推荐</div>
              <div class="table-actions">
                <el-button type="primary" class="btn-primary" @click="generateStrategy">
                  生成策略
                </el-button>
              </div>
            </div>

            <div class="form-section">
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
            </div>

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
          </div>
        </div>
      </el-tab-pane>

      <!-- 4. 智能内容生成 -->
      <el-tab-pane label="✍️ 智能内容" name="content">
        <div class="intelligent-content">
          <el-row :gutter="20">
            <el-col :span="10">
              <div class="form-section">
                <div class="form-title">🤖 AI内容生成器</div>

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

                  <el-button type="primary" class="btn-primary" @click="generateIntelligentContent" block>
                    🚀 生成内容
                  </el-button>
                </el-form>
              </div>
            </el-col>

            <el-col :span="14">
              <div class="data-table-wrapper">
                <div class="table-header">
                  <div class="table-title">📝 生成内容预览</div>
                  <div class="table-subtitle">实时预览生成的内容，支持复制和保存</div>
                  <div class="table-actions" v-if="generatedContent">
                    <el-button size="small" class="btn-secondary" @click="copyContent">复制</el-button>
                    <el-button size="small" type="primary" class="btn-primary" @click="saveContent">保存</el-button>
                  </div>
                </div>

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

                <div v-else class="empty-state">
                  <div class="empty-icon">📝</div>
                  <div class="empty-title">请先配置参数并生成内容</div>
                  <div class="empty-description">在左侧选择内容类型和目标客户，系统将为您生成个性化的营销内容</div>
                </div>
              </div>
            </el-col>
          </el-row>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, reactive } from "vue";
import { ElMessage } from "element-plus";

// AI营销助手的核心状态
const activeTab = ref("keywords");

// 关键词研究表单和结果
const keywordForm = reactive({
  industry: "",
  market: "",
  seedKeywords: "",
  analysisGoals: []
});

const keywordResults = ref([]);

// 产品推荐表单和结果
const productForm = reactive({
  customerType: "",
  purchaseHistory: "",
  budgetRange: [0, 5000]
});

const productRecommendations = ref([]);

// 策略推荐表单和结果
const strategyForm = reactive({
  goal: "",
  audience: "",
  budget: "",
  timeline: ""
});

const strategyRecommendations = ref([]);

// 内容生成表单和结果
const contentForm = reactive({
  type: "",
  customerSegment: "",
  tone: "",
  keyPoints: "",
  language: "zh"
});

const generatedContent = ref(null);
const contentSuggestions = ref([]);

// 关键词研究方法
const analyzeKeywords = async () => {
  if (!keywordForm.industry || !keywordForm.market) {
    ElMessage.warning("请选择行业和目标市场");
    return;
  }

  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 2000));
    
    // 模拟关键词分析结果
    keywordResults.value = [
      {
        keyword: "智能手机壳",
        searchVolume: "12,000",
        competition: "中等",
        trend: 15,
        difficulty: 65,
        suggestion: "建议优化长尾关键词"
      },
      {
        keyword: "iPhone保护套",
        searchVolume: "8,500",
        competition: "高",
        trend: -5,
        difficulty: 85,
        suggestion: "竞争激烈，考虑细分市场"
      },
      {
        keyword: "手机配件批发",
        searchVolume: "3,200",
        competition: "低",
        trend: 25,
        difficulty: 45,
        suggestion: "潜力关键词，建议重点投入"
      }
    ];

    ElMessage.success("关键词分析完成！");
  } catch (error) {
    ElMessage.error("分析失败，请重试");
  }
};

// 获取产品推荐
const getProductRecommendations = async () => {
  if (!productForm.customerType) {
    ElMessage.warning("请选择客户类型");
    return;
  }

  try {
    await new Promise(resolve => setTimeout(resolve, 1500));
    
    productRecommendations.value = [
      {
        id: 1,
        name: "无线充电器套装",
        price: 89.99,
        matchScore: 4.5,
        reason: "高复购率产品",
        image: "/product1.jpg"
      },
      {
        id: 2,
        name: "蓝牙耳机专业版",
        price: 159.99,
        matchScore: 4.2,
        reason: "符合预算范围",
        image: "/product2.jpg"
      },
      {
        id: 3,
        name: "智能手环运动版",
        price: 299.99,
        matchScore: 3.8,
        reason: "新客户喜爱",
        image: "/product3.jpg"
      }
    ];

    ElMessage.success("产品推荐已生成！");
  } catch (error) {
    ElMessage.error("获取推荐失败");
  }
};

// 生成营销策略
const generateStrategy = async () => {
  if (!strategyForm.goal || !strategyForm.audience) {
    ElMessage.warning("请选择营销目标和目标受众");
    return;
  }

  try {
    await new Promise(resolve => setTimeout(resolve, 2000));
    
    strategyRecommendations.value = [
      {
        id: 1,
        title: "社交媒体内容营销策略",
        priority: "高优先级",
        description: "通过优质内容建立品牌认知，提升用户参与度和转化率",
        channels: ["facebook", "instagram", "tiktok"],
        expectedROI: 185,
        difficulty: 3
      },
      {
        id: 2,
        title: "邮件自动化营销策略",
        priority: "中优先级", 
        description: "建立邮件营销漏斗，实现客户生命周期自动化管理",
        channels: ["email", "whatsapp"],
        expectedROI: 220,
        difficulty: 2
      },
      {
        id: 3,
        title: "联盟营销推广策略",
        priority: "低优先级",
        description: "与行业KOL合作，扩大品牌影响力和客户群体",
        channels: ["influencer", "partnership"],
        expectedROI: 150,
        difficulty: 4
      }
    ];

    ElMessage.success("营销策略已生成！");
  } catch (error) {
    ElMessage.error("策略生成失败");
  }
};

// 智能内容生成
const generateIntelligentContent = async () => {
  if (!contentForm.type || !contentForm.customerSegment) {
    ElMessage.warning("请选择内容类型和目标客户");
    return;
  }

  try {
    await new Promise(resolve => setTimeout(resolve, 2500));
    
    // 模拟生成内容
    if (contentForm.type.includes('email')) {
      generatedContent.value = {
        subject: "🎉 限时优惠！专为您推荐的精选产品",
        body: `亲爱的${contentForm.customerSegment === 'vip_customer' ? 'VIP' : ''}客户，

感谢您一直以来的信任与支持！

${contentForm.keyPoints || '我们为您精心挑选了以下产品'}，现在享受特别优惠：

✨ 产品亮点：
- 高品质保证
- 专业售后服务  
- 限时特惠价格

立即行动，把握机会！

此致
敬礼！
您的专属销售团队`
      };
    } else {
      generatedContent.value = `🚀 ${contentForm.keyPoints || '发现全新产品体验'}！

专为${contentForm.customerSegment === 'new_customer' ? '新朋友' : '老朋友'}设计的优质产品，现已上线！

💡 为什么选择我们：
- 创新设计理念
- 用户至上服务
- 性价比优势

#产品推荐 #优质体验 #限时优惠`;
    }

    contentSuggestions.value = [
      "建议添加更多个性化元素",
      "可以加入用户评价增加可信度", 
      "考虑添加紧迫感词汇提升转化",
      "建议优化移动端阅读体验"
    ];

    ElMessage.success("内容生成完成！");
  } catch (error) {
    ElMessage.error("内容生成失败");
  }
};

// 工具方法
const getCompetitionType = (competition) => {
  const map = { "低": "success", "中等": "warning", "高": "danger" };
  return map[competition] || "info";
};

const getTrendClass = (trend) => {
  return trend > 0 ? "trend-up" : trend < 0 ? "trend-down" : "trend-flat";
};

const getDifficultyColor = (difficulty) => {
  return difficulty > 70 ? "#f56c6c" : difficulty > 40 ? "#e6a23c" : "#67c23a";
};

const getStrategyType = (priority) => {
  const map = { "高优先级": "danger", "中优先级": "warning", "低优先级": "info" };
  return map[priority] || "info";
};

const getChannelName = (channel) => {
  const map = {
    facebook: "Facebook",
    instagram: "Instagram", 
    tiktok: "TikTok",
    email: "邮件营销",
    whatsapp: "WhatsApp",
    influencer: "KOL合作",
    partnership: "联盟营销"
  };
  return map[channel] || channel;
};

const getContentTypeName = (type) => {
  const map = {
    product_email: "产品介绍邮件",
    promotion_email: "促销邮件",
    social_post: "社交媒体文案",
    product_description: "产品描述",
    customer_reply: "客户回复"
  };
  return map[type] || type;
};

const getWordCount = (content) => {
  if (typeof content === 'string') {
    return content.length;
  }
  if (content && content.body) {
    return content.body.length;
  }
  return 0;
};

// 操作方法
const addToStrategy = (keyword) => {
  ElMessage.success(`已将"${keyword.keyword}"加入策略库`);
};

const addToQuote = (product) => {
  ElMessage.success(`已将"${product.name}"加入报价单`);
};

const generateContent = (product) => {
  contentForm.keyPoints = `推荐产品：${product.name}，价格：$${product.price}`;
  activeTab.value = "content";
  ElMessage.info("已自动填充产品信息到内容生成");
};

const viewStrategyDetails = (strategy) => {
  ElMessage.info(`查看策略详情：${strategy.title}`);
};

const implementStrategy = (strategy) => {
  ElMessage.success(`开始执行策略：${strategy.title}`);
};

const copyContent = () => {
  ElMessage.success("内容已复制到剪贴板");
};

const saveContent = () => {
  ElMessage.success("内容已保存到内容库");
};
</script>

<style scoped>
/* 使用全局设计系统，仅保留必要的组件特定样式 */

/* 关键词研究样式 */
.research-form {
  margin-bottom: var(--spacing-6);
}

.keyword-results {
  margin-top: var(--spacing-6);
}

.suggestion {
  font-size: var(--text-sm);
  color: var(--text-secondary);
}

/* 产品推荐样式 */
.recommendations-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: var(--spacing-4);
  padding: var(--spacing-5) 0;
}

.product-recommendation-item {
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-lg);
  overflow: hidden;
  transition: all var(--transition-duration) var(--transition-timing);
  background: var(--bg-primary);
}

.product-recommendation-item:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-lg);
  border-color: var(--primary-light);
}

.product-image {
  height: 160px;
  background: var(--bg-tertiary);
  display: flex;
  align-items: center;
  justify-content: center;
}

.product-image img {
  max-width: 100%;
  max-height: 100%;
  object-fit: cover;
}

.product-info {
  padding: var(--spacing-4);
}

.product-info h4 {
  margin: 0 0 var(--spacing-2) 0;
  color: var(--text-primary);
  font-size: var(--text-base);
  font-weight: 600;
}

.product-price {
  font-size: var(--text-lg);
  font-weight: 700;
  color: var(--success-color);
  margin-bottom: var(--spacing-2);
}

.match-score {
  margin-bottom: var(--spacing-2);
}

.product-actions {
  padding: 0 var(--spacing-4) var(--spacing-4);
  display: flex;
  gap: var(--spacing-2);
}

.budget-labels {
  display: flex;
  justify-content: space-between;
  margin-top: var(--spacing-2);
  font-size: var(--text-sm);
  color: var(--text-secondary);
}

/* 策略推荐样式 */
.strategy-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: var(--spacing-5);
  margin-top: var(--spacing-5);
}

.strategy-item {
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-lg);
  background: var(--bg-primary);
  transition: all var(--transition-duration) var(--transition-timing);
}

.strategy-item:hover {
  box-shadow: var(--shadow-md);
  border-color: var(--primary-light);
}

.strategy-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-4);
  border-bottom: 1px solid var(--border-primary);
  background: var(--bg-secondary);
}

.strategy-title {
  font-weight: 600;
  color: var(--text-primary);
  font-size: var(--text-base);
}

.strategy-content {
  padding: var(--spacing-4);
}

.strategy-description {
  margin-bottom: var(--spacing-4);
  color: var(--text-secondary);
  line-height: 1.6;
}

.strategy-channels {
  margin-bottom: var(--spacing-4);
}

.label {
  font-weight: 500;
  color: var(--text-primary);
  margin-right: var(--spacing-2);
  font-size: var(--text-sm);
}

.channel-tag {
  margin-right: var(--spacing-2);
  margin-bottom: var(--spacing-1);
}

.strategy-metrics {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-4);
  padding: var(--spacing-3);
  background: var(--bg-tertiary);
  border-radius: var(--radius-md);
}

.metric-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-2);
}

.metric-label {
  font-size: var(--text-sm);
  color: var(--text-secondary);
}

.metric-value {
  font-weight: 600;
  color: var(--success-color);
}

.strategy-actions {
  display: flex;
  gap: var(--spacing-2);
}

/* 智能内容样式 */
.intelligent-content {
  padding: var(--spacing-5) 0;
}

.content-display {
  min-height: 300px;
  padding: var(--spacing-4);
}

.content-meta {
  display: flex;
  gap: var(--spacing-2);
  margin-bottom: var(--spacing-4);
  align-items: center;
}

.word-count {
  margin-left: var(--spacing-4);
  font-size: var(--text-sm);
  color: var(--text-secondary);
}

.content-body {
  margin-bottom: var(--spacing-5);
}

.email-preview .email-subject {
  padding: var(--spacing-3);
  background: var(--bg-tertiary);
  border-radius: var(--radius-md);
  margin-bottom: var(--spacing-3);
  font-weight: 600;
}

.email-preview .email-body,
.text-preview {
  background: var(--bg-primary);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-md);
  padding: var(--spacing-4);
  white-space: pre-wrap;
  line-height: 1.6;
  max-height: 300px;
  overflow-y: auto;
  font-family: inherit;
}

.content-suggestions {
  margin-top: var(--spacing-5);
  padding: var(--spacing-4);
  background: var(--primary-lightest);
  border-radius: var(--radius-md);
  border: 1px solid var(--primary-lighter);
}

.content-suggestions h4 {
  margin: 0 0 var(--spacing-3) 0;
  color: var(--primary-color);
  font-size: var(--text-base);
  font-weight: 600;
}

.content-suggestions ul {
  margin: 0;
  padding-left: var(--spacing-5);
}

.content-suggestions li {
  margin-bottom: var(--spacing-1);
  color: var(--text-primary);
  font-size: var(--text-sm);
}

/* 趋势指示器 */
.trend-up { color: var(--success-color); }
.trend-down { color: var(--danger-color); }
.trend-flat { color: var(--text-tertiary); }

/* 响应式设计 */
@media (max-width: 768px) {
  .strategy-cards {
    grid-template-columns: 1fr;
  }
  
  .recommendations-grid {
    grid-template-columns: 1fr;
  }
  
  .intelligent-content .el-row {
    flex-direction: column;
  }
  
  .intelligent-content .el-col {
    width: 100% !important;
    margin-bottom: var(--spacing-4);
  }
}
</style>
