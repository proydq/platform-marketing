<template>
  <el-drawer
    v-model="visible"
    :title="$t('customer.detail')"
    size="600px"
    :destroy-on-close="true"
  >
    <div v-loading="loading" class="detail-container">
      <template v-if="customerData">
        <!-- 基本信息 -->
        <div class="detail-section">
          <h3 class="section-title">{{ $t('customer.basicInfo') }}</h3>
          <el-descriptions :column="2" border>
            <el-descriptions-item :label="$t('customer.name')">
              <span class="detail-value">{{ customerData.name }}</span>
            </el-descriptions-item>
            <el-descriptions-item :label="$t('customer.email')">
              <span class="detail-value">{{ customerData.email }}</span>
            </el-descriptions-item>
            <el-descriptions-item :label="$t('customer.phone')">
              <span class="detail-value">{{ customerData.phone || '-' }}</span>
            </el-descriptions-item>
            <el-descriptions-item :label="$t('customer.company')">
              <span class="detail-value">{{ customerData.company || '-' }}</span>
            </el-descriptions-item>
            <el-descriptions-item :label="$t('customer.source')">
              <el-tag type="info" size="small">{{ customerData.source || '-' }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item :label="$t('customer.status')">
              <el-tag 
                :type="customerData.status === 'active' ? 'success' : 'danger'" 
                size="small"
              >
                {{ customerData.status === 'active' ? $t('customer.statusActive') : $t('customer.statusInactive') }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item :label="$t('customer.createdAt')" :span="2">
              <span class="detail-value">{{ formatDate(customerData.createdAt) }}</span>
            </el-descriptions-item>
            <el-descriptions-item 
              v-if="customerData.remark" 
              :label="$t('customer.remark')" 
              :span="2"
            >
              <span class="detail-value">{{ customerData.remark }}</span>
            </el-descriptions-item>
          </el-descriptions>
        </div>

        <!-- 统计信息 -->
        <div class="detail-section">
          <h3 class="section-title">{{ $t('customer.statistics') }}</h3>
          <div class="stats-grid">
            <div class="stat-item">
              <div class="stat-value">{{ customerStats.totalInteractions || 0 }}</div>
              <div class="stat-label">{{ $t('customer.totalInteractions') }}</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ customerStats.emailsSent || 0 }}</div>
              <div class="stat-label">{{ $t('customer.emailsSent') }}</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ customerStats.campaignsParticipated || 0 }}</div>
              <div class="stat-label">{{ $t('customer.campaignsParticipated') }}</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ formatDate(customerStats.lastInteraction) || '-' }}</div>
              <div class="stat-label">{{ $t('customer.lastInteraction') }}</div>
            </div>
          </div>
        </div>

        <!-- 最近活动 -->
        <div class="detail-section">
          <h3 class="section-title">{{ $t('customer.recentActivities') }}</h3>
          <el-timeline v-if="activities.length > 0">
            <el-timeline-item
              v-for="activity in activities"
              :key="activity.id"
              :timestamp="formatDate(activity.createdAt)"
              placement="top"
            >
              <div class="activity-content">
                <div class="activity-type">
                  <el-tag size="small" :type="getActivityType(activity.type)">
                    {{ getActivityLabel(activity.type) }}
                  </el-tag>
                </div>
                <div class="activity-description">{{ activity.description }}</div>
              </div>
            </el-timeline-item>
          </el-timeline>
          <el-empty v-else :description="$t('customer.noActivities')" />
        </div>
      </template>
    </div>

    <template #footer>
      <div class="drawer-footer">
        <el-button @click="visible = false">{{ $t('common.close') }}</el-button>
        <el-button type="primary" @click="handleEdit">
          <el-icon><Edit /></el-icon>
          {{ $t('common.edit') }}
        </el-button>
      </div>
    </template>
  </el-drawer>
</template>

<script setup>
import { ref, reactive, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Edit } from '@element-plus/icons-vue'
import { useI18n } from 'vue-i18n'
import { useCustomerStore } from '@/stores/modules/customer'
import { formatDate } from '@/utils/date'

const { t } = useI18n()
const customerStore = useCustomerStore()

// Props
const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  customerId: {
    type: [String, Number],
    default: null
  }
})

// Emits
const emit = defineEmits(['update:modelValue', 'edit'])

// 响应式数据
const loading = ref(false)
const customerData = ref(null)
const customerStats = reactive({
  totalInteractions: 0,
  emailsSent: 0,
  campaignsParticipated: 0,
  lastInteraction: null
})
const activities = ref([])

const visible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

// 监听器
watch([() => props.customerId, visible], async ([newCustomerId, newVisible]) => {
  if (newVisible && newCustomerId) {
    await loadCustomerDetail(newCustomerId)
  }
})

// 方法
const loadCustomerDetail = async (customerId) => {
  loading.value = true
  try {
    // 加载客户基本信息
    customerData.value = await customerStore.fetchCustomer(customerId)
    
    // 加载客户统计信息和活动记录（模拟数据）
    await loadCustomerStats(customerId)
    await loadCustomerActivities(customerId)
  } catch (error) {
    ElMessage.error(t('customer.loadDetailError'))
    console.error('加载客户详情失败:', error)
  } finally {
    loading.value = false
  }
}

const loadCustomerStats = async (customerId) => {
  try {
    // 这里应该调用实际的API
    // const stats = await customerStore.fetchCustomerStats(customerId)
    
    // 模拟数据
    Object.assign(customerStats, {
      totalInteractions: Math.floor(Math.random() * 50) + 1,
      emailsSent: Math.floor(Math.random() * 20) + 1,
      campaignsParticipated: Math.floor(Math.random() * 10) + 1,
      lastInteraction: new Date(Date.now() - Math.random() * 30 * 24 * 60 * 60 * 1000)
    })
  } catch (error) {
    console.error('加载客户统计失败:', error)
  }
}

const loadCustomerActivities = async (customerId) => {
  try {
    // 这里应该调用实际的API
    // activities.value = await customerStore.fetchCustomerActivities(customerId)
    
    // 模拟数据
    const activityTypes = ['email_sent', 'campaign_joined', 'profile_updated', 'interaction']
    activities.value = Array.from({ length: 5 }, (_, index) => ({
      id: index + 1,
      type: activityTypes[Math.floor(Math.random() * activityTypes.length)],
      description: getActivityDescription(activityTypes[Math.floor(Math.random() * activityTypes.length)]),
      createdAt: new Date(Date.now() - Math.random() * 7 * 24 * 60 * 60 * 1000)
    })).sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
  } catch (error) {
    console.error('加载客户活动失败:', error)
  }
}

const getActivityType = (type) => {
  const typeMap = {
    email_sent: 'success',
    campaign_joined: 'primary',
    profile_updated: 'warning',
    interaction: 'info'
  }
  return typeMap[type] || 'info'
}

const getActivityLabel = (type) => {
  const labelMap = {
    email_sent: t('customer.activity.emailSent'),
    campaign_joined: t('customer.activity.campaignJoined'),
    profile_updated: t('customer.activity.profileUpdated'),
    interaction: t('customer.activity.interaction')
  }
  return labelMap[type] || type
}

const getActivityDescription = (type) => {
  const descriptionMap = {
    email_sent: t('customer.activity.emailSentDesc'),
    campaign_joined: t('customer.activity.campaignJoinedDesc'),
    profile_updated: t('customer.activity.profileUpdatedDesc'),
    interaction: t('customer.activity.interactionDesc')
  }
  return descriptionMap[type] || t('customer.activity.defaultDesc')
}

const handleEdit = () => {
  emit('edit', customerData.value)
  visible.value = false
}
</script>

<style scoped>
.detail-container {
  padding: 0 4px;
}

.detail-section {
  margin-bottom: 32px;
}

.section-title {
  margin: 0 0 16px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  border-left: 4px solid #409eff;
  padding-left: 12px;
}

.detail-value {
  color: #606266;
  font-size: 14px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(140px, 1fr));
  gap: 16px;
}

.stat-item {
  text-align: center;
  padding: 20px 16px;
  background: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e4e7ed;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #303133;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 12px;
  color: #909399;
  font-weight: 500;
}

.activity-content {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.activity-type {
  display: flex;
  align-items: center;
}

.activity-description {
  color: #606266;
  font-size: 14px;
  line-height: 1.4;
}

.drawer-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 16px 0 0 0;
  border-top: 1px solid #e4e7ed;
}

:deep(.el-descriptions__label) {
  font-weight: 500;
  color: #606266;
}

:deep(.el-timeline-item__timestamp) {
  color: #909399;
  font-size: 12px;
}
</style>