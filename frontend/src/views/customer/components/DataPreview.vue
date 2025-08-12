<template>
  <div class="data-preview">
    <!-- 预览工具栏 -->
    <div class="preview-toolbar">
      <div class="toolbar-left">
        <el-select
          v-model="viewMode"
          style="width: 120px"
          @change="handleViewModeChange"
        >
          <el-option :label="$t('customer.acquisition.tableView')" value="table" />
          <el-option :label="$t('customer.acquisition.cardView')" value="card" />
          <el-option :label="$t('customer.acquisition.jsonView')" value="json" />
        </el-select>
        
        <el-input
          v-model="searchKeyword"
          :placeholder="$t('customer.acquisition.searchData')"
          style="width: 200px; margin-left: 12px"
          clearable
          @input="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </div>
      
      <div class="toolbar-right">
        <el-text type="info" size="small">
          {{ $t('customer.acquisition.totalRecords', { count: filteredData.length }) }}
        </el-text>
        
        <el-button size="small" @click="handleRefresh">
          <el-icon><Refresh /></el-icon>
          {{ $t('common.refresh') }}
        </el-button>
        
        <el-button size="small" type="primary" @click="handleExport">
          <el-icon><Download /></el-icon>
          {{ $t('customer.acquisition.export') }}
        </el-button>
      </div>
    </div>

    <!-- 数据统计 -->
    <div class="preview-stats" v-if="statistics">
      <div class="stat-item">
        <div class="stat-value">{{ statistics.total }}</div>
        <div class="stat-label">{{ $t('customer.acquisition.totalFound') }}</div>
      </div>
      <div class="stat-item">
        <div class="stat-value">{{ statistics.valid }}</div>
        <div class="stat-label">{{ $t('customer.acquisition.validData') }}</div>
      </div>
      <div class="stat-item">
        <div class="stat-value">{{ statistics.duplicates }}</div>
        <div class="stat-label">{{ $t('customer.acquisition.duplicates') }}</div>
      </div>
      <div class="stat-item">
        <div class="stat-value">{{ formatPercentage(statistics.quality) }}</div>
        <div class="stat-label">{{ $t('customer.acquisition.quality') }}</div>
      </div>
    </div>

    <!-- 数据内容 -->
    <div v-loading="loading" class="preview-content">
      <!-- 表格视图 -->
      <div v-if="viewMode === 'table'" class="table-view">
        <el-table
          :data="paginatedData"
          stripe
          border
          style="width: 100%"
          :empty-text="$t('common.noData')"
          max-height="500px"
        >
          <el-table-column type="index" width="50" />
          <el-table-column
            v-for="column in tableColumns"
            :key="column.key"
            :prop="column.key"
            :label="column.label"
            :width="column.width"
            show-overflow-tooltip
          >
            <template #default="{ row }">
              <span v-if="column.type === 'email'">
                <el-link
                  v-if="row[column.key]"
                  :href="`mailto:${row[column.key]}`"
                  type="primary"
                >
                  {{ row[column.key] }}
                </el-link>
              </span>
              <span v-else-if="column.type === 'phone'">
                <el-link
                  v-if="row[column.key]"
                  :href="`tel:${row[column.key]}`"
                  type="primary"
                >
                  {{ row[column.key] }}
                </el-link>
              </span>
              <span v-else-if="column.type === 'url'">
                <el-link
                  v-if="row[column.key]"
                  :href="row[column.key]"
                  target="_blank"
                  type="primary"
                >
                  {{ truncateText(row[column.key], 30) }}
                </el-link>
              </span>
              <el-tag
                v-else-if="column.type === 'tag'"
                size="small"
                :type="getTagType(row[column.key])"
              >
                {{ row[column.key] }}
              </el-tag>
              <span v-else>{{ row[column.key] || '-' }}</span>
            </template>
          </el-table-column>
          <el-table-column :label="$t('common.actions')" width="100" fixed="right">
            <template #default="{ row, $index }">
              <el-button
                size="small"
                type="primary"
                link
                @click="handleViewDetail(row, $index)"
              >
                <el-icon><View /></el-icon>
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 卡片视图 -->
      <div v-else-if="viewMode === 'card'" class="card-view">
        <div class="card-grid">
          <el-card
            v-for="(item, index) in paginatedData"
            :key="index"
            class="data-card"
            shadow="hover"
          >
            <div class="card-header">
              <h4 class="card-title">{{ item.name || item.title || $t('customer.acquisition.record') + (index + 1) }}</h4>
              <el-tag
                v-if="item.source"
                size="small"
                type="info"
              >
                {{ item.source }}
              </el-tag>
            </div>
            <div class="card-content">
              <div v-for="(value, key) in getDisplayFields(item)" :key="key" class="card-field">
                <span class="field-label">{{ getFieldLabel(key) }}:</span>
                <span class="field-value">{{ value || '-' }}</span>
              </div>
            </div>
            <div class="card-actions">
              <el-button size="small" type="primary" link @click="handleViewDetail(item, index)">
                {{ $t('customer.acquisition.viewDetail') }}
              </el-button>
            </div>
          </el-card>
        </div>
      </div>

      <!-- JSON 视图 -->
      <div v-else-if="viewMode === 'json'" class="json-view">
        <el-input
          v-model="jsonData"
          type="textarea"
          :rows="20"
          readonly
          class="json-textarea"
        />
      </div>
    </div>

    <!-- 分页 -->
    <div class="preview-pagination">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="filteredData.length"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        background
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
      />
    </div>

    <!-- 详情对话框 -->
    <el-dialog
      v-model="detailVisible"
      :title="$t('customer.acquisition.dataDetail')"
      width="600px"
    >
      <div v-if="selectedItem" class="detail-content">
        <el-descriptions :column="2" border>
          <el-descriptions-item
            v-for="(value, key) in selectedItem"
            :key="key"
            :label="getFieldLabel(key)"
          >
            <span v-if="key === 'email' && value">
              <el-link :href="`mailto:${value}`" type="primary">{{ value }}</el-link>
            </span>
            <span v-else-if="key === 'phone' && value">
              <el-link :href="`tel:${value}`" type="primary">{{ value }}</el-link>
            </span>
            <span v-else-if="key.includes('url') && value">
              <el-link :href="value" target="_blank" type="primary">{{ value }}</el-link>
            </span>
            <span v-else>{{ value || '-' }}</span>
          </el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <el-button @click="detailVisible = false">{{ $t('common.close') }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { 
  Search, 
  Refresh, 
  Download, 
  View 
} from '@element-plus/icons-vue'
import { useI18n } from 'vue-i18n'
import { formatPercentage } from '@/utils/format'

const { t } = useI18n()

// Props
const props = defineProps({
  data: {
    type: Array,
    default: () => []
  },
  loading: {
    type: Boolean,
    default: false
  },
  statistics: {
    type: Object,
    default: null
  }
})

// Emits
const emit = defineEmits(['refresh', 'export'])

// 响应式数据
const viewMode = ref('table')
const searchKeyword = ref('')
const currentPage = ref(1)
const pageSize = ref(20)
const detailVisible = ref(false)
const selectedItem = ref(null)

// 计算属性
const filteredData = computed(() => {
  if (!searchKeyword.value) return props.data
  
  const keyword = searchKeyword.value.toLowerCase()
  return props.data.filter(item => {
    return Object.values(item).some(value => {
      if (typeof value === 'string') {
        return value.toLowerCase().includes(keyword)
      }
      return false
    })
  })
})

const paginatedData = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredData.value.slice(start, end)
})

const tableColumns = computed(() => {
  if (!props.data || props.data.length === 0) return []
  
  const firstItem = props.data[0]
  const columns = []
  
  // 预定义字段顺序和配置
  const fieldConfigs = [
    { key: 'name', label: t('customer.name'), width: 120 },
    { key: 'email', label: t('customer.email'), width: 180, type: 'email' },
    { key: 'phone', label: t('customer.phone'), width: 140, type: 'phone' },
    { key: 'company', label: t('customer.company'), width: 150 },
    { key: 'title', label: t('customer.acquisition.jobTitle'), width: 120 },
    { key: 'location', label: t('customer.acquisition.location'), width: 120 },
    { key: 'source', label: t('customer.source'), width: 100, type: 'tag' },
    { key: 'website', label: t('customer.acquisition.website'), width: 150, type: 'url' }
  ]
  
  // 添加配置中存在的字段
  fieldConfigs.forEach(config => {
    if (firstItem.hasOwnProperty(config.key)) {
      columns.push(config)
    }
  })
  
  // 添加其他未配置的字段
  Object.keys(firstItem).forEach(key => {
    if (!fieldConfigs.find(config => config.key === key)) {
      columns.push({
        key,
        label: getFieldLabel(key),
        width: 120
      })
    }
  })
  
  return columns
})

const jsonData = computed(() => {
  return JSON.stringify(filteredData.value, null, 2)
})

// 监听器
watch(() => props.data, () => {
  currentPage.value = 1
})

// 方法
const handleViewModeChange = () => {
  currentPage.value = 1
}

const handleSearch = () => {
  currentPage.value = 1
}

const handleRefresh = () => {
  emit('refresh')
}

const handleExport = () => {
  emit('export', filteredData.value)
}

const handleViewDetail = (item, index) => {
  selectedItem.value = item
  detailVisible.value = true
}

const handlePageChange = (page) => {
  currentPage.value = page
}

const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
}

const getDisplayFields = (item) => {
  // 只显示重要字段
  const importantFields = ['email', 'phone', 'company', 'title', 'location']
  const displayFields = {}
  
  importantFields.forEach(field => {
    if (item[field]) {
      displayFields[field] = item[field]
    }
  })
  
  return displayFields
}

const getFieldLabel = (key) => {
  const fieldLabels = {
    name: t('customer.name'),
    email: t('customer.email'),
    phone: t('customer.phone'),
    company: t('customer.company'),
    title: t('customer.acquisition.jobTitle'),
    location: t('customer.acquisition.location'),
    source: t('customer.source'),
    website: t('customer.acquisition.website'),
    social: t('customer.acquisition.socialProfile'),
    industry: t('customer.acquisition.industry'),
    size: t('customer.acquisition.companySize'),
    description: t('customer.acquisition.description')
  }
  
  return fieldLabels[key] || key.charAt(0).toUpperCase() + key.slice(1)
}

const getTagType = (value) => {
  if (!value) return 'info'
  
  const tagTypes = {
    'LinkedIn': 'primary',
    'Facebook': 'success',
    'Twitter': 'info',
    'Google Maps': 'warning',
    'Yellow Pages': 'danger'
  }
  
  return tagTypes[value] || 'info'
}

const truncateText = (text, length = 30) => {
  if (!text || text.length <= length) return text
  return text.substring(0, length) + '...'
}
</script>

<style scoped>
.data-preview {
  display: flex;
  flex-direction: column;
  height: 100%;
  gap: 24px;
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  padding: 24px;
  border-radius: 20px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.2);
  position: relative;
  overflow: hidden;
}

.data-preview::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.03) 0%, rgba(118, 75, 162, 0.03) 100%);
  z-index: -1;
}

.preview-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 16px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06);
  border: 1px solid rgba(255, 255, 255, 0.3);
  flex-wrap: wrap;
  gap: 16px;
  backdrop-filter: blur(10px);
}

.toolbar-left,
.toolbar-right {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
}

.preview-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(140px, 1fr));
  gap: 20px;
  padding: 24px;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 16px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06);
  border: 1px solid rgba(255, 255, 255, 0.3);
  backdrop-filter: blur(10px);
}

.stat-item {
  text-align: center;
  padding: 20px 16px;
  background: linear-gradient(135deg, #ffffff 0%, #f8fafc 100%);
  border-radius: 16px;
  border: 2px solid transparent;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.stat-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  opacity: 0;
  transition: opacity 0.3s ease;
  z-index: -1;
}

.stat-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.15);
  border-color: #667eea;
}

.stat-item:hover::before {
  opacity: 0.05;
}

.stat-value {
  font-size: 24px;
  font-weight: 800;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  margin-bottom: 6px;
}

.stat-label {
  font-size: 13px;
  color: #64748b;
  font-weight: 600;
  opacity: 0.8;
}

.preview-content {
  flex: 1;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
  border: 1px solid rgba(255, 255, 255, 0.3);
  overflow: hidden;
  backdrop-filter: blur(10px);
}

.table-view {
  height: 100%;
  padding: 16px;
}

.card-view {
  padding: 24px;
  max-height: 600px;
  overflow-y: auto;
}

.card-view::-webkit-scrollbar {
  width: 6px;
}

.card-view::-webkit-scrollbar-track {
  background: rgba(226, 232, 240, 0.3);
  border-radius: 3px;
}

.card-view::-webkit-scrollbar-thumb {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 3px;
}

.card-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: 20px;
}

.data-card {
  margin: 0;
  border-radius: 16px;
  border: 2px solid transparent;
  background: linear-gradient(135deg, #ffffff 0%, #f8fafc 100%);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.data-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  opacity: 0;
  transition: opacity 0.3s ease;
  z-index: -1;
}

.data-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 12px 32px rgba(102, 126, 234, 0.2);
  border-color: #667eea;
}

.data-card:hover::before {
  opacity: 0.03;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 2px solid rgba(102, 126, 234, 0.1);
}

.card-title {
  margin: 0;
  font-size: 18px;
  font-weight: 700;
  color: #1e293b;
  letter-spacing: 0.5px;
}

.card-content {
  margin-bottom: 16px;
  padding: 8px;
  background: rgba(248, 250, 252, 0.5);
  border-radius: 10px;
  border: 1px solid rgba(226, 232, 240, 0.5);
}

.card-field {
  display: flex;
  margin-bottom: 10px;
  font-size: 14px;
  padding: 6px 8px;
  background: rgba(255, 255, 255, 0.7);
  border-radius: 6px;
  border: 1px solid rgba(255, 255, 255, 0.5);
  transition: all 0.2s ease;
}

.card-field:hover {
  background: rgba(255, 255, 255, 0.9);
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
}

.card-field:last-child {
  margin-bottom: 0;
}

.field-label {
  font-weight: 600;
  color: #374151;
  width: 90px;
  flex-shrink: 0;
  position: relative;
}

.field-label::after {
  content: ':';
  margin-left: 2px;
}

.field-value {
  color: #1e293b;
  flex: 1;
  font-weight: 500;
}

.card-actions {
  text-align: right;
  padding-top: 12px;
  border-top: 1px solid rgba(226, 232, 240, 0.5);
}

.json-view {
  padding: 24px;
  height: 100%;
  background: #1e293b;
  position: relative;
}

.json-view::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.05) 0%, rgba(118, 75, 162, 0.05) 100%);
  z-index: -1;
}

.json-textarea {
  font-family: 'JetBrains Mono', 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  font-size: 13px;
  line-height: 1.6;
  background: transparent;
  color: #e2e8f0;
  border: none;
  outline: none;
}

.json-textarea ::-webkit-scrollbar {
  width: 8px;
}

.json-textarea ::-webkit-scrollbar-track {
  background: rgba(71, 85, 105, 0.3);
  border-radius: 4px;
}

.json-textarea ::-webkit-scrollbar-thumb {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 4px;
}

.preview-pagination {
  display: flex;
  justify-content: center;
  padding: 20px;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.3);
  backdrop-filter: blur(10px);
}

.detail-content {
  max-height: 500px;
  overflow-y: auto;
  padding: 4px;
}

.detail-content::-webkit-scrollbar {
  width: 6px;
}

.detail-content::-webkit-scrollbar-track {
  background: rgba(226, 232, 240, 0.3);
  border-radius: 3px;
}

.detail-content::-webkit-scrollbar-thumb {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 3px;
}

/* Element Plus 组件样式增强 */
:deep(.el-table) {
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

:deep(.el-table__header) {
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
}

:deep(.el-table__header th) {
  background: transparent;
  border: none;
  color: #374151;
  font-weight: 600;
  padding: 16px 12px;
}

:deep(.el-table__body tr) {
  background: rgba(255, 255, 255, 0.7);
  transition: all 0.2s ease;
}

:deep(.el-table__body tr:hover) {
  background: rgba(102, 126, 234, 0.05);
}

:deep(.el-table__body td) {
  border: none;
  padding: 14px 12px;
}

:deep(.el-select) {
  border-radius: 10px;
}

:deep(.el-input__wrapper) {
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: all 0.3s ease;
  border: 1px solid #e2e8f0;
  background: rgba(255, 255, 255, 0.9);
}

:deep(.el-input__wrapper:hover) {
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.15);
  border-color: #667eea;
  background: rgba(255, 255, 255, 1);
}

:deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.25);
  border-color: #667eea;
  background: rgba(255, 255, 255, 1);
}

:deep(.el-button) {
  border-radius: 10px;
  font-weight: 600;
  padding: 10px 20px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: none;
}

:deep(.el-button--primary) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.3);
}

:deep(.el-button--primary:hover) {
  transform: translateY(-2px);
  box-shadow: 0 6px 24px rgba(102, 126, 234, 0.4);
}

:deep(.el-button--default) {
  background: rgba(255, 255, 255, 0.9);
  color: #64748b;
  border: 1px solid rgba(100, 116, 139, 0.2);
  backdrop-filter: blur(8px);
}

:deep(.el-button--default:hover) {
  background: rgba(255, 255, 255, 1);
  color: #374151;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

:deep(.el-button.is-link) {
  color: #667eea;
  font-weight: 600;
}

:deep(.el-button.is-link:hover) {
  color: #764ba2;
  text-decoration: underline;
}

:deep(.el-pagination) {
  background: transparent;
}

:deep(.el-pager li) {
  border-radius: 8px;
  margin: 0 4px;
  transition: all 0.2s ease;
  background: rgba(255, 255, 255, 0.7);
  border: 1px solid rgba(226, 232, 240, 0.5);
}

:deep(.el-pager li:hover) {
  background: rgba(102, 126, 234, 0.1);
  border-color: #667eea;
}

:deep(.el-pager li.is-active) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-color: transparent;
}

:deep(.el-dialog) {
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.15);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

:deep(.el-dialog__header) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 20px 24px;
}

:deep(.el-dialog__title) {
  color: white;
  font-weight: 700;
}

:deep(.el-dialog__headerbtn) {
  color: white;
  font-size: 20px;
}

:deep(.el-descriptions) {
  background: transparent;
}

:deep(.el-descriptions__table) {
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

:deep(.el-descriptions__label) {
  font-weight: 600;
  color: #374151;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  border: 1px solid rgba(226, 232, 240, 0.5);
}

:deep(.el-descriptions__content) {
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid rgba(226, 232, 240, 0.5);
}

/* 动画效果 */
@keyframes slideInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.data-preview {
  animation: fadeIn 0.8s ease-out;
}

.preview-toolbar,
.preview-stats,
.preview-content,
.preview-pagination {
  animation: slideInUp 0.6s ease-out;
}

.preview-toolbar { animation-delay: 0.1s; }
.preview-stats { animation-delay: 0.2s; }
.preview-content { animation-delay: 0.3s; }
.preview-pagination { animation-delay: 0.4s; }

.data-card:nth-child(1) { animation: slideInUp 0.6s ease-out 0.1s both; }
.data-card:nth-child(2) { animation: slideInUp 0.6s ease-out 0.2s both; }
.data-card:nth-child(3) { animation: slideInUp 0.6s ease-out 0.3s both; }
.data-card:nth-child(4) { animation: slideInUp 0.6s ease-out 0.4s both; }
.data-card:nth-child(5) { animation: slideInUp 0.6s ease-out 0.5s both; }
.data-card:nth-child(6) { animation: slideInUp 0.6s ease-out 0.6s both; }

.stat-item:nth-child(1) { animation: slideInUp 0.6s ease-out 0.2s both; }
.stat-item:nth-child(2) { animation: slideInUp 0.6s ease-out 0.3s both; }
.stat-item:nth-child(3) { animation: slideInUp 0.6s ease-out 0.4s both; }
.stat-item:nth-child(4) { animation: slideInUp 0.6s ease-out 0.5s both; }

/* 响应式设计 */
@media (max-width: 768px) {
  .data-preview {
    padding: 16px;
    gap: 20px;
  }
  
  .preview-toolbar {
    flex-direction: column;
    align-items: stretch;
    padding: 16px 20px;
  }
  
  .toolbar-left,
  .toolbar-right {
    justify-content: center;
    gap: 12px;
  }
  
  .preview-stats {
    grid-template-columns: repeat(2, 1fr);
    gap: 16px;
    padding: 16px;
  }
  
  .stat-item {
    padding: 16px 12px;
  }
  
  .stat-value {
    font-size: 20px;
  }
  
  .card-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  
  .data-card {
    margin: 0 -4px;
  }
  
  .card-view {
    padding: 16px;
  }
  
  .json-view {
    padding: 16px;
  }
}

@media (max-width: 480px) {
  .data-preview {
    gap: 16px;
    padding: 12px;
    border-radius: 16px;
  }
  
  .preview-stats {
    grid-template-columns: 1fr;
    gap: 12px;
    padding: 12px;
  }
  
  .stat-item {
    padding: 12px 8px;
  }
  
  .stat-value {
    font-size: 18px;
  }
  
  .toolbar-left,
  .toolbar-right {
    flex-direction: column;
    align-items: stretch;
    gap: 8px;
  }
  
  .card-title {
    font-size: 16px;
  }
  
  .field-label {
    width: 70px;
    font-size: 13px;
  }
  
  .field-value {
    font-size: 13px;
  }
  
  .card-content {
    padding: 6px;
  }
  
  .card-field {
    padding: 4px 6px;
    font-size: 13px;
  }
  
  .json-textarea {
    font-size: 12px;
  }
}
</style>