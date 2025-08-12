<template>
  <div v-if="total > 0" class="om-pagination-wrapper">
    <el-pagination
      :class="paginationClasses"
      :current-page="currentPage"
      :page-size="pageSize"
      :total="total"
      :page-sizes="pageSizes"
      :layout="layout"
      :background="background"
      :small="small"
      :disabled="disabled"
      :hide-on-single-page="hideOnSinglePage"
      v-bind="$attrs"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      @prev-click="handlePrevClick"
      @next-click="handleNextClick"
    />
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  currentPage?: number
  pageSize?: number
  total?: number
  pageSizes?: number[]
  layout?: string
  background?: boolean
  small?: boolean
  disabled?: boolean
  hideOnSinglePage?: boolean
  align?: 'left' | 'center' | 'right'
}

const props = withDefaults(defineProps<Props>(), {
  currentPage: 1,
  pageSize: 10,
  total: 0,
  pageSizes: () => [10, 20, 50, 100],
  layout: 'total, sizes, prev, pager, next, jumper',
  background: true,
  small: false,
  disabled: false,
  hideOnSinglePage: false,
  align: 'center'
})

const emit = defineEmits<{
  (e: 'update:current-page', value: number): void
  (e: 'update:page-size', value: number): void
  (e: 'size-change', value: number): void
  (e: 'current-change', value: number): void
  (e: 'prev-click', value: number): void
  (e: 'next-click', value: number): void
}>()

const paginationClasses = computed(() => [
  'om-pagination',
  `om-pagination--${props.align}`,
  {
    'om-pagination--small': props.small,
    'om-pagination--disabled': props.disabled
  }
])

const handleSizeChange = (value: number) => {
  emit('update:page-size', value)
  emit('size-change', value)
}

const handleCurrentChange = (value: number) => {
  emit('update:current-page', value)
  emit('current-change', value)
}

const handlePrevClick = (value: number) => {
  emit('prev-click', value)
}

const handleNextClick = (value: number) => {
  emit('next-click', value)
}
</script>

<style scoped>
.om-pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: var(--om-space-xl);
  padding-top: var(--om-space-lg);
  border-top: 1px solid rgba(226, 232, 240, 0.5);
}

.om-pagination {
  background: rgba(255, 255, 255, 0.8);
  padding: var(--om-space-md) var(--om-space-lg);
  border-radius: var(--om-radius-xl);
  box-shadow: var(--om-shadow-md);
  backdrop-filter: var(--om-glass-blur);
  border: 1px solid var(--om-glass-border);
}

.om-pagination--left {
  justify-content: flex-start;
}

.om-pagination--right {
  justify-content: flex-end;
}

/* Element Plus 分页器样式覆盖 */
:deep(.el-pagination) {
  display: flex;
  align-items: center;
  gap: var(--om-space-sm);
  font-size: var(--om-font-sm);
}

:deep(.el-pagination .el-pagination__total) {
  color: var(--om-text-secondary);
  font-weight: var(--om-font-medium);
  margin-right: var(--om-space-md);
}

:deep(.el-pagination .el-pagination__sizes) {
  margin-right: var(--om-space-md);
}

:deep(.el-pagination .el-select) {
  --el-select-border-color-hover: var(--om-color-primary);
  --el-select-input-focus-border-color: var(--om-color-primary);
}

:deep(.el-pagination .el-select .el-input__wrapper) {
  border-radius: var(--om-radius-md);
  background: var(--om-color-white);
  box-shadow: var(--om-shadow-sm);
  border: 1px solid var(--om-color-gray-200);
  transition: var(--om-transition-normal);
}

:deep(.el-pagination .el-select .el-input__wrapper:hover) {
  border-color: var(--om-color-primary);
  box-shadow: var(--om-shadow-md);
}

:deep(.el-pagination .el-pager li) {
  min-width: 32px;
  height: 32px;
  line-height: 30px;
  border-radius: var(--om-radius-md);
  border: 1px solid var(--om-color-gray-200);
  background: var(--om-color-white);
  color: var(--om-text-primary);
  font-weight: var(--om-font-medium);
  transition: var(--om-transition-normal);
  margin: 0 2px;
}

:deep(.el-pagination .el-pager li:hover) {
  background: var(--om-color-primary);
  border-color: var(--om-color-primary);
  color: var(--om-text-inverse);
  transform: translateY(-1px);
  box-shadow: var(--om-shadow-md);
}

:deep(.el-pagination .el-pager li.is-active) {
  background: var(--om-color-brand-gradient);
  border-color: var(--om-color-primary);
  color: var(--om-text-inverse);
  box-shadow: var(--om-shadow-button);
}

:deep(.el-pagination .btn-prev),
:deep(.el-pagination .btn-next) {
  width: 32px;
  height: 32px;
  border-radius: var(--om-radius-md);
  border: 1px solid var(--om-color-gray-200);
  background: var(--om-color-white);
  color: var(--om-text-primary);
  transition: var(--om-transition-normal);
}

:deep(.el-pagination .btn-prev:hover:not(:disabled)),
:deep(.el-pagination .btn-next:hover:not(:disabled)) {
  background: var(--om-color-primary);
  border-color: var(--om-color-primary);
  color: var(--om-text-inverse);
  transform: translateY(-1px);
  box-shadow: var(--om-shadow-md);
}

:deep(.el-pagination .btn-prev:disabled),
:deep(.el-pagination .btn-next:disabled) {
  background: var(--om-color-gray-100);
  border-color: var(--om-color-gray-200);
  color: var(--om-text-disabled);
  cursor: not-allowed;
}

:deep(.el-pagination .el-pagination__jump) {
  margin-left: var(--om-space-md);
  color: var(--om-text-secondary);
}

:deep(.el-pagination .el-pagination__jump .el-input) {
  width: 60px;
  margin: 0 var(--om-space-xs);
}

:deep(.el-pagination .el-pagination__jump .el-input__wrapper) {
  border-radius: var(--om-radius-md);
  background: var(--om-color-white);
  box-shadow: var(--om-shadow-sm);
  border: 1px solid var(--om-color-gray-200);
  transition: var(--om-transition-normal);
}

:deep(.el-pagination .el-pagination__jump .el-input__wrapper:hover) {
  border-color: var(--om-color-primary);
  box-shadow: var(--om-shadow-md);
}

:deep(.el-pagination .el-pagination__jump .el-input__wrapper.is-focus) {
  border-color: var(--om-color-primary);
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.2);
}

/* 小尺寸样式 */
.om-pagination--small :deep(.el-pagination .el-pager li) {
  min-width: 28px;
  height: 28px;
  line-height: 26px;
  font-size: 12px;
}

.om-pagination--small :deep(.el-pagination .btn-prev),
.om-pagination--small :deep(.el-pagination .btn-next) {
  width: 28px;
  height: 28px;
  font-size: 12px;
}

/* 禁用状态样式 */
.om-pagination--disabled {
  opacity: 0.6;
  pointer-events: none;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .om-pagination-wrapper {
    overflow-x: auto;
  }
  
  .om-pagination {
    padding: var(--om-space-sm) var(--om-space-md);
    white-space: nowrap;
    min-width: max-content;
  }
  
  :deep(.el-pagination) {
    font-size: 12px;
    gap: 4px;
  }
  
  :deep(.el-pagination .el-pagination__total) {
    display: none;
  }
  
  :deep(.el-pagination .el-pagination__jump) {
    display: none;
  }
  
  :deep(.el-pagination .el-pager li) {
    min-width: 28px;
    height: 28px;
    line-height: 26px;
    font-size: 12px;
    margin: 0 1px;
  }
}
</style>