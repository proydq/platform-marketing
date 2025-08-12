<template>
  <div class="om-search-bar">
    <BaseCard padding="default" shadow="hover">
      <form @submit.prevent="handleSearch" class="om-search-bar__form">
        <div class="om-search-bar__main">
          <!-- 关键词搜索 -->
          <BaseInput
            v-model="searchQuery.keyword"
            :placeholder="keywordPlaceholder"
            clearable
            size="default"
            prefix-icon="search"
            @keydown.enter="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </BaseInput>
          
          <!-- 搜索按钮 -->
          <BaseButton
            variant="primary"
            size="default"
            :loading="loading"
            @click="handleSearch"
          >
            <el-icon><Search /></el-icon>
            {{ $t('common.search') }}
          </BaseButton>
          
          <!-- 重置按钮 -->
          <BaseButton
            variant="default"
            size="default"
            @click="handleReset"
          >
            <el-icon><Refresh /></el-icon>
            {{ $t('common.reset') }}
          </BaseButton>
          
          <!-- 展开/收起按钮 -->
          <BaseButton
            v-if="hasFilters"
            variant="default"
            size="default"
            @click="toggleExpanded"
          >
            <el-icon>
              <ArrowDown v-if="!expanded" />
              <ArrowUp v-else />
            </el-icon>
            {{ expanded ? $t('common.collapse') : $t('common.expand') }}
          </BaseButton>
        </div>
        
        <!-- 高级筛选 -->
        <el-collapse-transition>
          <div v-show="expanded && hasFilters" class="om-search-bar__filters">
            <div class="om-search-bar__filter-grid">
              <div
                v-for="field in schema"
                :key="field.key"
                class="om-search-bar__filter-item"
              >
                <label class="om-search-bar__filter-label">
                  {{ field.label }}
                </label>
                
                <!-- 选择器类型 -->
                <el-select
                  v-if="field.type === 'select'"
                  v-model="searchQuery[field.key]"
                  :placeholder="field.placeholder || $t('common.pleaseSelect')"
                  clearable
                  :multiple="field.multiple"
                  class="om-search-bar__filter-control"
                >
                  <el-option
                    v-for="option in field.options"
                    :key="option.value"
                    :label="option.label"
                    :value="option.value"
                  />
                </el-select>
                
                <!-- 日期选择器 -->
                <el-date-picker
                  v-else-if="field.type === 'date'"
                  v-model="searchQuery[field.key]"
                  :type="field.dateType || 'date'"
                  :placeholder="field.placeholder || $t('common.pleaseSelect')"
                  clearable
                  class="om-search-bar__filter-control"
                />
                
                <!-- 日期范围选择器 -->
                <el-date-picker
                  v-else-if="field.type === 'daterange'"
                  v-model="searchQuery[field.key]"
                  type="daterange"
                  :start-placeholder="field.startPlaceholder || $t('common.startDate')"
                  :end-placeholder="field.endPlaceholder || $t('common.endDate')"
                  clearable
                  class="om-search-bar__filter-control"
                />
                
                <!-- 数字输入框 -->
                <BaseInput
                  v-else-if="field.type === 'number'"
                  v-model="searchQuery[field.key]"
                  type="number"
                  :placeholder="field.placeholder"
                  :min="field.min"
                  :max="field.max"
                  clearable
                />
                
                <!-- 文本输入框 -->
                <BaseInput
                  v-else
                  v-model="searchQuery[field.key]"
                  :type="field.type || 'text'"
                  :placeholder="field.placeholder"
                  clearable
                />
              </div>
            </div>
          </div>
        </el-collapse-transition>
      </form>
    </BaseCard>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch } from 'vue'
import { BaseCard, BaseInput, BaseButton } from '@/components/base'
import { Search, Refresh, ArrowDown, ArrowUp } from '@element-plus/icons-vue'
import { useI18n } from 'vue-i18n'
import type { SearchSchema, SearchQuery } from './types'

interface Props {
  modelValue?: SearchQuery
  schema?: SearchSchema[]
  keywordPlaceholder?: string
  loading?: boolean
  defaultExpanded?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  modelValue: () => ({}),
  schema: () => [],
  keywordPlaceholder: '',
  loading: false,
  defaultExpanded: false
})

const emit = defineEmits<{
  (e: 'update:modelValue', value: SearchQuery): void
  (e: 'search', query: SearchQuery): void
  (e: 'reset'): void
}>()

const { t } = useI18n()

const expanded = ref(props.defaultExpanded)
const searchQuery = reactive<SearchQuery>({ ...props.modelValue })

const hasFilters = computed(() => props.schema && props.schema.length > 0)

// 监听外部数据变化
watch(() => props.modelValue, (newValue) => {
  Object.assign(searchQuery, newValue)
}, { deep: true })

// 监听内部数据变化
watch(searchQuery, (newValue) => {
  emit('update:modelValue', { ...newValue })
}, { deep: true })

const toggleExpanded = () => {
  expanded.value = !expanded.value
}

const handleSearch = () => {
  emit('search', { ...searchQuery })
}

const handleReset = () => {
  // 重置所有字段
  const resetQuery: SearchQuery = { keyword: '' }
  props.schema?.forEach(field => {
    resetQuery[field.key] = field.multiple ? [] : ''
  })
  
  Object.assign(searchQuery, resetQuery)
  emit('reset')
  emit('search', { ...resetQuery })
}

// 获取关键词占位符
const keywordPlaceholder = computed(() => {
  return props.keywordPlaceholder || t('common.enterKeyword')
})
</script>

<style scoped>
.om-search-bar {
  margin-bottom: var(--om-space-lg);
}

.om-search-bar__form {
  width: 100%;
}

.om-search-bar__main {
  display: flex;
  align-items: center;
  gap: var(--om-space-md);
  flex-wrap: wrap;
}

.om-search-bar__main .om-base-input {
  flex: 1;
  min-width: 200px;
}

.om-search-bar__filters {
  margin-top: var(--om-space-lg);
  padding-top: var(--om-space-lg);
  border-top: 1px solid rgba(226, 232, 240, 0.5);
}

.om-search-bar__filter-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: var(--om-space-md);
}

.om-search-bar__filter-item {
  display: flex;
  flex-direction: column;
  gap: var(--om-space-xs);
}

.om-search-bar__filter-label {
  font-size: var(--om-font-sm);
  font-weight: var(--om-font-medium);
  color: var(--om-text-primary);
  line-height: var(--om-line-tight);
}

.om-search-bar__filter-control {
  width: 100%;
}

/* Element Plus 样式覆盖 */
:deep(.el-select) {
  width: 100%;
}

:deep(.el-select .el-input__wrapper) {
  border-radius: var(--om-input-radius);
  box-shadow: var(--om-input-shadow);
  border: 1px solid var(--om-input-border);
  background: var(--om-input-bg);
  transition: var(--om-transition-normal);
}

:deep(.el-select .el-input__wrapper:hover) {
  border-color: var(--om-input-border-focus);
  box-shadow: var(--om-input-shadow-hover);
}

:deep(.el-select .el-input__wrapper.is-focus) {
  border-color: var(--om-input-border-focus);
  box-shadow: var(--om-input-shadow-focus);
}

:deep(.el-date-editor) {
  width: 100%;
}

:deep(.el-date-editor .el-input__wrapper) {
  border-radius: var(--om-input-radius);
  box-shadow: var(--om-input-shadow);
  border: 1px solid var(--om-input-border);
  background: var(--om-input-bg);
  transition: var(--om-transition-normal);
}

:deep(.el-date-editor .el-input__wrapper:hover) {
  border-color: var(--om-input-border-focus);
  box-shadow: var(--om-input-shadow-hover);
}

:deep(.el-date-editor .el-input__wrapper.is-focus) {
  border-color: var(--om-input-border-focus);
  box-shadow: var(--om-input-shadow-focus);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .om-search-bar__main {
    flex-direction: column;
    align-items: stretch;
  }
  
  .om-search-bar__main .om-base-input {
    min-width: auto;
  }
  
  .om-search-bar__filter-grid {
    grid-template-columns: 1fr;
  }
}
</style>