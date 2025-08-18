<template>
  <div class="page-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="page-title">
        <span class="page-icon">📈</span>
        行为跟踪与分析
      </div>
      <div class="page-subtitle">
        全渠道行为记录、转化分析、ROI计算，深度洞察客户行为模式
      </div>
      <div class="page-actions">
        <el-button type="primary" class="btn-primary"> 导出分析报告 </el-button>
        <el-button class="btn-secondary"> 配置跟踪规则 </el-button>
      </div>
    </div>

    <!-- 数据概览卡片 -->
    <div class="stats-grid">
      <div class="stat-card fade-in">
        <div class="stat-header">
          <div
            class="stat-icon"
            style="
              background: linear-gradient(135deg, #dbeafe, #93c5fd);
              color: #1e40af;
            "
          >
            👁️
          </div>
          <div class="stat-trend positive">+{{ metrics.viewsTrend }}%</div>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ formatNumber(metrics.totalViews) }}</div>
          <div class="stat-label">总浏览量</div>
        </div>
      </div>

      <div class="stat-card fade-in" style="animation-delay: 0.1s">
        <div class="stat-header">
          <div
            class="stat-icon"
            style="
              background: linear-gradient(135deg, #d1fae5, #86efac);
              color: #059669;
            "
          >
            🎯
          </div>
          <div class="stat-trend positive">+{{ metrics.conversionTrend }}%</div>
        </div>
        <div class="stat-content">
          <div class="stat-value">
            {{ formatPercent(metrics.conversionRate) }}%
          </div>
          <div class="stat-label">转化率</div>
        </div>
      </div>

      <div class="stat-card fade-in" style="animation-delay: 0.2s">
        <div class="stat-header">
          <div
            class="stat-icon"
            style="
              background: linear-gradient(135deg, #fef3c7, #fcd34d);
              color: #d97706;
            "
          >
            💰
          </div>
          <div class="stat-trend positive">+{{ metrics.revenueTrend }}%</div>
        </div>
        <div class="stat-content">
          <div class="stat-value">${{ formatNumber(metrics.revenue) }}</div>
          <div class="stat-label">总收入</div>
        </div>
      </div>

      <div class="stat-card fade-in" style="animation-delay: 0.3s">
        <div class="stat-header">
          <div
            class="stat-icon"
            style="
              background: linear-gradient(135deg, #ede9fe, #c4b5fd);
              color: #7c3aed;
            "
          >
            📊
          </div>
          <div class="stat-trend positive">+{{ metrics.roiTrend }}%</div>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ formatPercent(metrics.roi) }}%</div>
          <div class="stat-label">ROI</div>
        </div>
      </div>
    </div>

    <!-- 分析标签页 -->
    <el-tabs v-model="activeTab" class="enhanced-tabs">
      <!-- 行为流分析 -->
      <el-tab-pane label="🔄 行为流分析" name="behavior">
        <div class="data-table-wrapper">
          <div class="table-header">
            <div class="table-title">客户行为路径分析</div>
            <div class="table-subtitle">
              追踪客户在各个渠道的行为轨迹，识别转化瓶颈
            </div>
            <div class="table-actions">
              <el-date-picker
                v-model="dateRange"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                @change="updateBehaviorFlow"
              />
              <el-select
                v-model="channelFilter"
                placeholder="选择渠道"
                @change="updateBehaviorFlow"
              >
                <el-option label="全部渠道" value="" />
                <el-option label="邮件" value="email" />
                <el-option label="WhatsApp" value="whatsapp" />
                <el-option label="社交媒体" value="social" />
                <el-option label="网站" value="website" />
              </el-select>
            </div>
          </div>

          <!-- 行为流图表 -->
          <div class="behavior-flow-chart">
            <div class="flow-stages">
              <div
                v-for="stage in behaviorFlow"
                :key="stage.name"
                class="flow-stage"
              >
                <div class="stage-header">
                  <span class="stage-name">{{ stage.name }}</span>
                  <span class="stage-count">{{
                    formatNumber(stage.count)
                  }}</span>
                </div>
                <div class="stage-bar">
                  <div
                    class="stage-fill"
                    :style="{
                      width: `${stage.percentage}%`,
                      backgroundColor: stage.color,
                    }"
                  ></div>
                </div>
                <div class="stage-info">
                  <span class="conversion-rate"
                    >转化率: {{ stage.conversionRate }}%</span
                  >
                  <span class="drop-rate">流失率: {{ stage.dropRate }}%</span>
                </div>
              </div>
            </div>
          </div>

          <!-- 行为日志表格 -->
          <div class="behavior-table">
            <el-table :data="behaviorLogs" style="width: 100%">
              <el-table-column prop="timestamp" label="时间" width="180">
                <template #default="{ row }">
                  {{ formatDate(row.timestamp) }}
                </template>
              </el-table-column>
              <el-table-column prop="customerId" label="客户ID" width="120" />
              <el-table-column
                prop="customerName"
                label="客户名称"
                width="150"
              />
              <el-table-column prop="channel" label="渠道" width="100">
                <template #default="{ row }">
                  <el-tag :type="getChannelType(row.channel)" size="small">
                    {{ row.channel }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="action" label="行为" width="150">
                <template #default="{ row }">
                  <span class="action-label">{{ row.action }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="page" label="页面/内容" min-width="200" />
              <el-table-column prop="duration" label="停留时间" width="100">
                <template #default="{ row }">
                  {{ formatDuration(row.duration) }}
                </template>
              </el-table-column>
              <el-table-column label="操作" width="100">
                <template #default="{ row }">
                  <el-button type="text" @click="viewBehaviorDetail(row)"
                    >详情</el-button
                  >
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>
      </el-tab-pane>

      <!-- 转化漏斗分析 -->
      <el-tab-pane label="🔻 转化漏斗" name="funnel">
        <div class="data-table-wrapper">
          <div class="table-header">
            <div class="table-title">营销转化漏斗分析</div>
            <div class="table-subtitle">
              分析客户从曝光到转化的完整路径，优化转化率
            </div>
            <div class="table-actions">
              <el-select v-model="funnelType" placeholder="选择漏斗类型">
                <el-option label="标准营销漏斗" value="standard" />
                <el-option label="邮件营销漏斗" value="email" />
                <el-option label="社交媒体漏斗" value="social" />
                <el-option label="自定义漏斗" value="custom" />
              </el-select>
              <el-button type="primary" @click="configureFunnel"
                >配置漏斗</el-button
              >
            </div>
          </div>
        </div>
        <!-- 漏斗图 -->
        <div class="funnel-chart">
          <div class="funnel-visualization">
            <div
              v-for="(stage, index) in funnelStages"
              :key="stage.id"
              class="funnel-stage"
            >
              <div
                class="funnel-level"
                :style="{
                  width: `${100 - index * 15}%`,
                  backgroundColor: getFunnelColor(index),
                }"
              >
                <div class="funnel-content">
                  <div class="funnel-stage-name">{{ stage.name }}</div>
                  <div class="funnel-metrics">
                    <span class="funnel-count"
                      >{{ formatNumber(stage.count) }} 人</span
                    >
                    <span class="funnel-rate">{{ stage.rate }}%</span>
                  </div>
                </div>
              </div>
              <div v-if="index < funnelStages.length - 1" class="funnel-arrow">
                <span class="conversion-info">
                  转化: {{ stage.conversionToNext }}%
                  <br />
                  流失: {{ stage.dropOffRate }}%
                </span>
              </div>
            </div>
          </div>
        </div>

        <!-- 漏斗详细数据 -->
        <div class="funnel-details">
          <el-table :data="funnelDetails" style="width: 100%">
            <el-table-column prop="stage" label="阶段" />
            <el-table-column prop="visitors" label="访客数" />
            <el-table-column prop="conversionRate" label="转化率">
              <template #default="{ row }">
                <el-progress :percentage="row.conversionRate" />
              </template>
            </el-table-column>
            <el-table-column prop="avgTime" label="平均停留时间" />
            <el-table-column prop="exitRate" label="退出率">
              <template #default="{ row }">
                <span :class="getExitRateClass(row.exitRate)">
                  {{ row.exitRate }}%
                </span>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-tab-pane>

      <!-- ROI分析 -->
      <el-tab-pane label="💰 ROI分析" name="roi">
        <div class="data-table-wrapper">
          <div class="table-header">
            <div class="table-title">投资回报率分析</div>
            <div class="table-subtitle">
              多维度ROI分析，精准衡量营销投入产出比
            </div>
            <div class="table-actions">
              <el-date-picker
                v-model="roiDateRange"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                @change="updateROIAnalysis"
              />
              <el-button type="primary" @click="exportROIReport"
                >导出报告</el-button
              >
            </div>
          </div>

          <!-- ROI概览 -->
          <div class="roi-overview">
            <el-row :gutter="20">
              <el-col :span="8">
                <div class="roi-metric">
                  <div class="roi-label">总投入</div>
                  <div class="roi-value negative">
                    ${{ formatNumber(roiData.totalCost) }}
                  </div>
                  <div class="roi-breakdown">
                    <div>广告费用: ${{ formatNumber(roiData.adCost) }}</div>
                    <div>人力成本: ${{ formatNumber(roiData.laborCost) }}</div>
                    <div>工具费用: ${{ formatNumber(roiData.toolCost) }}</div>
                  </div>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="roi-metric">
                  <div class="roi-label">总收益</div>
                  <div class="roi-value positive">
                    ${{ formatNumber(roiData.totalRevenue) }}
                  </div>
                  <div class="roi-breakdown">
                    <div>
                      直接销售: ${{ formatNumber(roiData.directSales) }}
                    </div>
                    <div>追加销售: ${{ formatNumber(roiData.upsells) }}</div>
                    <div>续费收入: ${{ formatNumber(roiData.renewals) }}</div>
                  </div>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="roi-metric">
                  <div class="roi-label">ROI</div>
                  <div
                    class="roi-value"
                    :class="roiData.roi > 0 ? 'positive' : 'negative'"
                  >
                    {{ roiData.roi }}%
                  </div>
                  <div class="roi-breakdown">
                    <div>净利润: ${{ formatNumber(roiData.netProfit) }}</div>
                    <div>回收周期: {{ roiData.paybackPeriod }} 天</div>
                    <div>客户LTV: ${{ formatNumber(roiData.customerLTV) }}</div>
                  </div>
                </div>
              </el-col>
            </el-row>
          </div>

          <!-- 渠道ROI对比 -->
          <div class="channel-roi">
            <h3>渠道ROI对比</h3>
            <el-table :data="channelROI" style="width: 100%">
              <el-table-column prop="channel" label="渠道" width="150">
                <template #default="{ row }">
                  <span class="channel-name">{{ row.channel }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="cost" label="成本" width="120">
                <template #default="{ row }">
                  ${{ formatNumber(row.cost) }}
                </template>
              </el-table-column>
              <el-table-column prop="revenue" label="收入" width="120">
                <template #default="{ row }">
                  ${{ formatNumber(row.revenue) }}
                </template>
              </el-table-column>
              <el-table-column prop="conversions" label="转化数" width="100" />
              <el-table-column prop="cpa" label="CPA" width="100">
                <template #default="{ row }">
                  ${{ row.cpa.toFixed(2) }}
                </template>
              </el-table-column>
              <el-table-column prop="roi" label="ROI" width="120">
                <template #default="{ row }">
                  <el-tag :type="getROIType(row.roi)"> {{ row.roi }}% </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="趋势" width="150">
                <template #default="{ row }">
                  <div class="mini-chart">
                    <span
                      v-for="(point, idx) in row.trend"
                      :key="idx"
                      class="trend-bar"
                      :style="{ height: `${point}%` }"
                    ></span>
                  </div>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="120">
                <template #default="{ row }">
                  <el-button type="text" @click="optimizeChannel(row)"
                    >优化</el-button
                  >
                  <el-button type="text" @click="viewChannelDetail(row)"
                    >详情</el-button
                  >
                </template>
              </el-table-column>
            </el-table>
          </div>

          <!-- 营销活动ROI -->
          <div class="campaign-roi">
            <h3>营销活动ROI排行</h3>
            <div class="campaign-roi-list">
              <div
                v-for="campaign in campaignROI"
                :key="campaign.id"
                class="campaign-roi-item"
              >
                <div class="campaign-info">
                  <span class="campaign-name">{{ campaign.name }}</span>
                  <el-tag size="small">{{ campaign.type }}</el-tag>
                </div>
                <div class="campaign-metrics">
                  <span class="metric"
                    >成本: ${{ formatNumber(campaign.cost) }}</span
                  >
                  <span class="metric"
                    >收入: ${{ formatNumber(campaign.revenue) }}</span
                  >
                  <span
                    class="metric roi-badge"
                    :class="campaign.roi > 0 ? 'positive' : 'negative'"
                  >
                    ROI: {{ campaign.roi }}%
                  </span>
                </div>
                <el-progress
                  :percentage="Math.min(campaign.roi, 100)"
                  :color="getProgressColor(campaign.roi)"
                />
              </div>
            </div>
          </div>
        </div>
      </el-tab-pane>

      <!-- 客户评分 -->
      <el-tab-pane label="⭐ 客户评分" name="scoring">
        <div class="data-table-wrapper">
          <div class="table-header">
            <div class="table-title">客户行为评分与分级</div>
            <div class="table-subtitle">
              基于客户行为数据的智能评分系统，精准识别高价值客户
            </div>
            <div class="table-actions">
              <el-button @click="configureScoringRules">评分规则</el-button>
              <el-button type="primary" @click="recalculateScores"
                >重新计算</el-button
              >
            </div>
          </div>

          <!-- 评分分布 -->
          <div class="score-distribution">
            <h3>客户评分分布</h3>
            <div class="distribution-chart">
              <div
                v-for="segment in scoreSegments"
                :key="segment.level"
                class="score-segment"
              >
                <div class="segment-bar">
                  <div
                    class="segment-fill"
                    :style="{
                      height: `${segment.percentage}%`,
                      backgroundColor: segment.color,
                    }"
                  >
                    <span class="segment-count">{{ segment.count }}</span>
                  </div>
                </div>
                <div class="segment-label">
                  <span class="level-name">{{ segment.level }}</span>
                  <span class="score-range">{{ segment.range }}</span>
                </div>
              </div>
            </div>
          </div>

          <!-- 高价值客户列表 -->
          <div class="high-value-customers">
            <h3>高价值客户 TOP 10</h3>
            <el-table :data="highValueCustomers" style="width: 100%">
              <el-table-column prop="rank" label="#" width="50" />
              <el-table-column prop="name" label="客户名称" width="150" />
              <el-table-column prop="score" label="评分" width="100">
                <template #default="{ row }">
                  <span class="score-badge">{{ row.score }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="level" label="等级" width="100">
                <template #default="{ row }">
                  <el-tag :type="getLevelType(row.level)">{{
                    row.level
                  }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="totalRevenue" label="总收入" width="120">
                <template #default="{ row }">
                  ${{ formatNumber(row.totalRevenue) }}
                </template>
              </el-table-column>
              <el-table-column prop="orderCount" label="订单数" width="80" />
              <el-table-column
                prop="lastActivity"
                label="最后活跃"
                width="120"
              />
              <el-table-column label="行为指标" min-width="200">
                <template #default="{ row }">
                  <div class="behavior-indicators">
                    <span class="indicator">访问: {{ row.visits }}</span>
                    <span class="indicator">互动: {{ row.interactions }}</span>
                    <span class="indicator">分享: {{ row.shares }}</span>
                  </div>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="120">
                <template #default="{ row }">
                  <el-button type="text" @click="viewCustomerProfile(row)"
                    >查看</el-button
                  >
                  <el-button type="text" @click="engageCustomer(row)"
                    >互动</el-button
                  >
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>

    <!-- 行为详情弹窗 -->
    <el-dialog v-model="behaviorDetailVisible" title="行为详情" width="600px">
      <div v-if="selectedBehavior" class="behavior-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="客户ID">{{
            selectedBehavior.customerId
          }}</el-descriptions-item>
          <el-descriptions-item label="客户名称">{{
            selectedBehavior.customerName
          }}</el-descriptions-item>
          <el-descriptions-item label="时间">{{
            formatDate(selectedBehavior.timestamp)
          }}</el-descriptions-item>
          <el-descriptions-item label="渠道">{{
            selectedBehavior.channel
          }}</el-descriptions-item>
          <el-descriptions-item label="行为类型">{{
            selectedBehavior.action
          }}</el-descriptions-item>
          <el-descriptions-item label="页面/内容">{{
            selectedBehavior.page
          }}</el-descriptions-item>
          <el-descriptions-item label="停留时间">{{
            formatDuration(selectedBehavior.duration)
          }}</el-descriptions-item>
          <el-descriptions-item label="设备类型">{{
            selectedBehavior.device
          }}</el-descriptions-item>
          <el-descriptions-item label="IP地址">{{
            selectedBehavior.ip
          }}</el-descriptions-item>
          <el-descriptions-item label="地理位置">{{
            selectedBehavior.location
          }}</el-descriptions-item>
        </el-descriptions>

        <div class="behavior-context">
          <h4>行为上下文</h4>
          <el-timeline>
            <el-timeline-item
              v-for="event in selectedBehavior.context"
              :key="event.id"
              :timestamp="formatDate(event.timestamp)"
            >
              {{ event.action }} - {{ event.page }}
            </el-timeline-item>
          </el-timeline>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from "vue";
import { ElMessage } from "element-plus";

// 状态变量
const activeTab = ref("behavior");
const dateRange = ref([]);
const roiDateRange = ref([]);
const channelFilter = ref("");
const funnelType = ref("standard");
const behaviorDetailVisible = ref(false);
const selectedBehavior = ref(null);

// 指标数据
const metrics = reactive({
  totalViews: 125432,
  viewsTrend: 12.5,
  conversionRate: 3.8,
  conversionTrend: 5.2,
  revenue: 458920,
  revenueTrend: 18.3,
  roi: 245,
  roiTrend: 22.1,
});

// 行为流数据
const behaviorFlow = ref([
  {
    name: "访问",
    count: 10000,
    percentage: 100,
    conversionRate: 60,
    dropRate: 40,
    color: "#409EFF",
  },
  {
    name: "浏览产品",
    count: 6000,
    percentage: 60,
    conversionRate: 50,
    dropRate: 50,
    color: "#67C23A",
  },
  {
    name: "加入购物车",
    count: 3000,
    percentage: 30,
    conversionRate: 40,
    dropRate: 60,
    color: "#E6A23C",
  },
  {
    name: "开始结账",
    count: 1200,
    percentage: 12,
    conversionRate: 75,
    dropRate: 25,
    color: "#F56C6C",
  },
  {
    name: "完成购买",
    count: 900,
    percentage: 9,
    conversionRate: 100,
    dropRate: 0,
    color: "#909399",
  },
]);

// 行为日志数据
const behaviorLogs = ref([
  {
    timestamp: new Date(),
    customerId: "C001",
    customerName: "张三",
    channel: "email",
    action: "打开邮件",
    page: "促销活动邮件 - 春季特惠",
    duration: 45,
    device: "Mobile",
    ip: "192.168.1.100",
    location: "上海",
  },
  {
    timestamp: new Date(Date.now() - 3600000),
    customerId: "C002",
    customerName: "李四",
    channel: "website",
    action: "浏览产品",
    page: "产品详情页 - 智能手表",
    duration: 180,
    device: "Desktop",
    ip: "192.168.1.101",
    location: "北京",
  },
]);

// 漏斗阶段数据
const funnelStages = ref([
  {
    id: 1,
    name: "曝光",
    count: 50000,
    rate: 100,
    conversionToNext: 40,
    dropOffRate: 60,
  },
  {
    id: 2,
    name: "点击",
    count: 20000,
    rate: 40,
    conversionToNext: 25,
    dropOffRate: 75,
  },
  {
    id: 3,
    name: "注册",
    count: 5000,
    rate: 10,
    conversionToNext: 60,
    dropOffRate: 40,
  },
  {
    id: 4,
    name: "购买",
    count: 3000,
    rate: 6,
    conversionToNext: 30,
    dropOffRate: 70,
  },
  {
    id: 5,
    name: "复购",
    count: 900,
    rate: 1.8,
    conversionToNext: 0,
    dropOffRate: 0,
  },
]);

// 漏斗详细数据
const funnelDetails = ref([
  {
    stage: "曝光",
    visitors: 50000,
    conversionRate: 40,
    avgTime: "5秒",
    exitRate: 60,
  },
  {
    stage: "点击",
    visitors: 20000,
    conversionRate: 25,
    avgTime: "30秒",
    exitRate: 75,
  },
  {
    stage: "注册",
    visitors: 5000,
    conversionRate: 60,
    avgTime: "2分钟",
    exitRate: 40,
  },
  {
    stage: "购买",
    visitors: 3000,
    conversionRate: 30,
    avgTime: "5分钟",
    exitRate: 70,
  },
  {
    stage: "复购",
    visitors: 900,
    conversionRate: 100,
    avgTime: "3分钟",
    exitRate: 0,
  },
]);

// ROI数据
const roiData = reactive({
  totalCost: 50000,
  adCost: 30000,
  laborCost: 15000,
  toolCost: 5000,
  totalRevenue: 172500,
  directSales: 120000,
  upsells: 32500,
  renewals: 20000,
  roi: 245,
  netProfit: 122500,
  paybackPeriod: 45,
  customerLTV: 850,
});

// 渠道ROI数据
const channelROI = ref([
  {
    channel: "邮件营销",
    cost: 10000,
    revenue: 45000,
    conversions: 450,
    cpa: 22.22,
    roi: 350,
    trend: [30, 45, 60, 55, 70, 85, 90],
  },
  {
    channel: "WhatsApp",
    cost: 8000,
    revenue: 32000,
    conversions: 320,
    cpa: 25,
    roi: 300,
    trend: [40, 50, 55, 60, 65, 70, 75],
  },
  {
    channel: "社交媒体",
    cost: 15000,
    revenue: 48000,
    conversions: 400,
    cpa: 37.5,
    roi: 220,
    trend: [20, 35, 45, 50, 60, 65, 70],
  },
  {
    channel: "搜索广告",
    cost: 12000,
    revenue: 30000,
    conversions: 250,
    cpa: 48,
    roi: 150,
    trend: [50, 45, 40, 45, 50, 55, 60],
  },
]);

// 营销活动ROI数据
const campaignROI = ref([
  {
    id: 1,
    name: "春季促销",
    type: "季节性",
    cost: 5000,
    revenue: 25000,
    roi: 400,
  },
  {
    id: 2,
    name: "新品发布",
    type: "产品推广",
    cost: 8000,
    revenue: 32000,
    roi: 300,
  },
  {
    id: 3,
    name: "会员专享",
    type: "客户维护",
    cost: 3000,
    revenue: 12000,
    roi: 300,
  },
  {
    id: 4,
    name: "黑五特惠",
    type: "节日营销",
    cost: 10000,
    revenue: 35000,
    roi: 250,
  },
  {
    id: 5,
    name: "清仓甩卖",
    type: "库存清理",
    cost: 2000,
    revenue: 6000,
    roi: 200,
  },
]);

// 客户评分分布
const scoreSegments = ref([
  {
    level: "钻石",
    range: "90-100",
    count: 120,
    percentage: 80,
    color: "#409EFF",
  },
  {
    level: "黄金",
    range: "70-89",
    count: 450,
    percentage: 60,
    color: "#67C23A",
  },
  {
    level: "白银",
    range: "50-69",
    count: 890,
    percentage: 45,
    color: "#E6A23C",
  },
  {
    level: "青铜",
    range: "30-49",
    count: 1200,
    percentage: 30,
    color: "#F56C6C",
  },
  {
    level: "普通",
    range: "0-29",
    count: 2000,
    percentage: 20,
    color: "#909399",
  },
]);

// 高价值客户
const highValueCustomers = ref([
  {
    rank: 1,
    name: "ABC公司",
    score: 95,
    level: "钻石",
    totalRevenue: 125000,
    orderCount: 45,
    lastActivity: "2小时前",
    visits: 320,
    interactions: 85,
    shares: 12,
  },
  {
    rank: 2,
    name: "XYZ集团",
    score: 92,
    level: "钻石",
    totalRevenue: 98000,
    orderCount: 38,
    lastActivity: "1天前",
    visits: 280,
    interactions: 72,
    shares: 8,
  },
]);

// 方法
const formatNumber = (num) => {
  return new Intl.NumberFormat().format(num);
};

const formatPercent = (num) => {
  return num.toFixed(1);
};

const formatDate = (date) => {
  return new Date(date).toLocaleString("zh-CN");
};

const formatDuration = (seconds) => {
  if (seconds < 60) return `${seconds}秒`;
  if (seconds < 3600) return `${Math.floor(seconds / 60)}分钟`;
  return `${Math.floor(seconds / 3600)}小时`;
};

const getTrendClass = (trend) => {
  return trend > 0 ? "positive" : trend < 0 ? "negative" : "neutral";
};

const getChannelType = (channel) => {
  const types = {
    email: "primary",
    whatsapp: "success",
    social: "warning",
    website: "info",
  };
  return types[channel] || "info";
};

const getFunnelColor = (index) => {
  const colors = ["#409EFF", "#67C23A", "#E6A23C", "#F56C6C", "#909399"];
  return colors[index] || "#909399";
};

const getExitRateClass = (rate) => {
  return rate > 50 ? "high-exit" : rate > 20 ? "medium-exit" : "low-exit";
};

const getROIType = (roi) => {
  if (roi >= 300) return "success";
  if (roi >= 150) return "warning";
  return "danger";
};

const getProgressColor = (roi) => {
  if (roi >= 300) return "#67C23A";
  if (roi >= 150) return "#E6A23C";
  return "#F56C6C";
};

const getLevelType = (level) => {
  const types = {
    钻石: "primary",
    黄金: "warning",
    白银: "info",
    青铜: "danger",
    普通: "",
  };
  return types[level] || "";
};

const updateBehaviorFlow = () => {
  ElMessage.success("行为流数据已更新");
};

const viewBehaviorDetail = (row) => {
  selectedBehavior.value = {
    ...row,
    context: [
      {
        id: 1,
        timestamp: new Date(Date.now() - 600000),
        action: "访问首页",
        page: "首页",
      },
      {
        id: 2,
        timestamp: new Date(Date.now() - 300000),
        action: "搜索产品",
        page: "搜索结果页",
      },
      { id: 3, timestamp: new Date(), action: row.action, page: row.page },
    ],
  };
  behaviorDetailVisible.value = true;
};

const configureFunnel = () => {
  ElMessage.info("打开漏斗配置界面");
};

const updateROIAnalysis = () => {
  ElMessage.success("ROI分析已更新");
};

const exportROIReport = () => {
  ElMessage.success("正在导出ROI报告...");
};

const optimizeChannel = (channel) => {
  ElMessage.info(`优化${channel.channel}渠道策略`);
};

const viewChannelDetail = (channel) => {
  ElMessage.info(`查看${channel.channel}详细数据`);
};

const configureScoringRules = () => {
  ElMessage.info("配置客户评分规则");
};

const recalculateScores = () => {
  ElMessage.success("正在重新计算客户评分...");
};

const viewCustomerProfile = (customer) => {
  ElMessage.info(`查看客户${customer.name}的详细档案`);
};

const engageCustomer = (customer) => {
  ElMessage.info(`与客户${customer.name}进行互动`);
};

// 生命周期
onMounted(() => {
  // 初始化数据加载
  ElMessage.success("行为跟踪数据加载完成");
});
</script>

<style scoped>
.behavior-track-container {
  padding: 20px;
}

.page-header {
  margin-bottom: 24px;
  text-align: center;
}

.page-header h1 {
  margin: 0 0 8px 0;
  color: #1f2937;
  font-size: 28px;
}

.page-header p {
  margin: 0;
  color: #6b7280;
  font-size: 16px;
}

/* 指标卡片样式 */
.metrics-overview {
  margin-bottom: 24px;
}

.metric-card {
  height: 120px;
  display: flex;
  align-items: center;
  padding: 20px;
}

.metric-icon {
  font-size: 32px;
  margin-right: 16px;
}

.metric-content {
  flex: 1;
}

.metric-value {
  font-size: 24px;
  font-weight: bold;
  color: #1f2937;
  margin-bottom: 4px;
}

.metric-label {
  font-size: 14px;
  color: #6b7280;
  margin-bottom: 4px;
}

.metric-trend {
  font-size: 12px;
  font-weight: 500;
}

.metric-trend.positive {
  color: #10b981;
}

.metric-trend.negative {
  color: #ef4444;
}

.metric-trend.neutral {
  color: #6b7280;
}

/* 分析标签页样式 */
.analysis-tabs {
  margin-top: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  gap: 12px;
  align-items: center;
}

/* 行为流样式 */
.behavior-flow-chart {
  padding: 20px 0;
}

.flow-stages {
  display: flex;
  justify-content: space-between;
  margin-bottom: 30px;
}

.flow-stage {
  flex: 1;
  margin: 0 10px;
}

.stage-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  font-size: 14px;
}

.stage-name {
  font-weight: 500;
  color: #374151;
}

.stage-count {
  color: #6b7280;
}

.stage-bar {
  height: 8px;
  background: #f3f4f6;
  border-radius: 4px;
  margin-bottom: 8px;
  overflow: hidden;
}

.stage-fill {
  height: 100%;
  border-radius: 4px;
  transition: width 0.3s ease;
}

.stage-info {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #6b7280;
}

.behavior-table {
  margin-top: 20px;
}

.action-label {
  font-weight: 500;
  color: #374151;
}

/* 漏斗图样式 */
.funnel-visualization {
  padding: 40px 20px;
}

.funnel-stage {
  margin-bottom: 20px;
}

.funnel-level {
  margin: 0 auto;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
  transition: all 0.3s ease;
}

.funnel-content {
  text-align: center;
  color: white;
}

.funnel-stage-name {
  font-size: 16px;
  font-weight: 500;
  margin-bottom: 4px;
}

.funnel-metrics {
  display: flex;
  gap: 20px;
  font-size: 14px;
}

.funnel-arrow {
  text-align: center;
  padding: 10px 0;
  font-size: 12px;
  color: #6b7280;
}

.conversion-info {
  line-height: 1.5;
}

.funnel-details {
  margin-top: 30px;
}

/* ROI样式 */
.roi-overview {
  margin-bottom: 30px;
}

.roi-metric {
  padding: 20px;
  background: #f9fafb;
  border-radius: 8px;
  text-align: center;
}

.roi-label {
  font-size: 14px;
  color: #6b7280;
  margin-bottom: 8px;
}

.roi-value {
  font-size: 32px;
  font-weight: bold;
  margin-bottom: 16px;
}

.roi-value.positive {
  color: #10b981;
}

.roi-value.negative {
  color: #ef4444;
}

.roi-breakdown {
  font-size: 12px;
  color: #6b7280;
  line-height: 1.8;
}

.channel-roi {
  margin-bottom: 30px;
}

.channel-roi h3,
.campaign-roi h3 {
  margin-bottom: 16px;
  color: #374151;
  font-size: 16px;
}

.channel-name {
  font-weight: 500;
  color: #374151;
}

.mini-chart {
  display: flex;
  align-items: flex-end;
  gap: 2px;
  height: 30px;
}

.trend-bar {
  flex: 1;
  background: #3b82f6;
  min-height: 5px;
  border-radius: 2px;
}

.campaign-roi-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.campaign-roi-item {
  padding: 16px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
}

.campaign-info {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.campaign-name {
  font-weight: 500;
  color: #374151;
}

.campaign-metrics {
  display: flex;
  gap: 20px;
  margin-bottom: 12px;
  font-size: 14px;
  color: #6b7280;
}

.roi-badge {
  font-weight: 500;
}

.roi-badge.positive {
  color: #10b981;
}

.roi-badge.negative {
  color: #ef4444;
}

/* 客户评分样式 */
.score-distribution {
  margin-bottom: 30px;
}

.distribution-chart {
  display: flex;
  align-items: flex-end;
  justify-content: space-around;
  height: 200px;
  padding: 20px;
  background: #f9fafb;
  border-radius: 8px;
}

.score-segment {
  display: flex;
  flex-direction: column;
  align-items: center;
  flex: 1;
}

.segment-bar {
  width: 60px;
  height: 150px;
  display: flex;
  align-items: flex-end;
  margin-bottom: 12px;
}

.segment-fill {
  width: 100%;
  border-radius: 4px 4px 0 0;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: 500;
  transition: height 0.3s ease;
}

.segment-label {
  text-align: center;
}

.level-name {
  display: block;
  font-weight: 500;
  color: #374151;
  margin-bottom: 4px;
}

.score-range {
  display: block;
  font-size: 12px;
  color: #6b7280;
}

.high-value-customers h3 {
  margin-bottom: 16px;
  color: #374151;
  font-size: 16px;
}

.score-badge {
  font-weight: bold;
  color: #3b82f6;
  font-size: 16px;
}

.behavior-indicators {
  display: flex;
  gap: 12px;
}

.indicator {
  font-size: 12px;
  color: #6b7280;
  padding: 2px 6px;
  background: #f3f4f6;
  border-radius: 4px;
}

/* 行为详情弹窗 */
.behavior-detail {
  padding: 20px;
}

.behavior-context {
  margin-top: 20px;
}

.behavior-context h4 {
  margin-bottom: 16px;
  color: #374151;
}

/* 退出率样式 */
.high-exit {
  color: #ef4444;
  font-weight: 500;
}

.medium-exit {
  color: #f59e0b;
}

.low-exit {
  color: #10b981;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .flow-stages {
    flex-direction: column;
  }

  .flow-stage {
    margin: 10px 0;
  }

  .distribution-chart {
    flex-direction: column;
    height: auto;
  }

  .segment-bar {
    width: 100%;
    height: 40px;
  }
}
</style>
