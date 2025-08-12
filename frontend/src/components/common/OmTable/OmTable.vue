<template>
  <BaseCard padding="none" shadow="hover" class="om-table-container">
    <el-table
      ref="tableRef"
      :class="tableClasses"
      :data="data"
      :loading="loading"
      :stripe="stripe"
      :border="border"
      :size="size"
      :fit="fit"
      :show-header="showHeader"
      :highlight-current-row="highlightCurrentRow"
      :row-class-name="getRowClassName"
      :empty-text="emptyText || $t('common.noData')"
      v-bind="$attrs"
      @select="handleSelect"
      @select-all="handleSelectAll"
      @selection-change="handleSelectionChange"
      @row-click="handleRowClick"
      @row-dblclick="handleRowDblClick"
      @current-change="handleCurrentChange"
      @sort-change="handleSortChange"
    >
      <!-- 选择列 -->
      <el-table-column
        v-if="selectable"
        type="selection"
        width="55"
        align="center"
        :selectable="selectableFunction"
      />
      
      <!-- 序号列 -->
      <el-table-column
        v-if="showIndex"
        type="index"
        :label="indexLabel || $t('common.index')"
        :width="indexWidth"
        align="center"
        :index="getIndex"
      />
      
      <!-- 数据列 -->
      <el-table-column
        v-for="column in columns"
        :key="column.prop || column.key"
        :prop="column.prop || column.key"
        :label="column.label"
        :width="column.width"
        :min-width="column.minWidth"
        :fixed="column.fixed"
        :sortable="column.sortable"
        :align="column.align"
        :header-align="column.headerAlign || column.align"
        :show-overflow-tooltip="column.showOverflowTooltip"
        :class-name="column.className"
        :label-class-name="column.labelClassName"
      >
        <template #header="{ column: col, $index }">
          <slot :name="`${column.prop || column.key}-header`" :column="col" :index="$index">
            {{ column.label }}
          </slot>
        </template>
        
        <template #default="{ row, column: col, $index }">
          <slot :name="column.prop || column.key" :row="row" :column="col" :index="$index">
            <!-- 默认渲染 -->
            <span v-if="!column.formatter">
              {{ getCellValue(row, column.prop || column.key) }}
            </span>
            <!-- 格式化渲染 -->
            <span v-else>
              {{ column.formatter(row, col, getCellValue(row, column.prop || column.key), $index) }}
            </span>
          </slot>
        </template>
      </el-table-column>
      
      <!-- 操作列 -->
      <el-table-column
        v-if="$slots.actions"
        :label="actionsLabel || $t('common.actions')"
        :width="actionsWidth"
        :fixed="actionsFixed"
        align="center"
        class-name="om-table__actions"
      >
        <template #default="{ row, $index }">
          <div class="om-table__action-buttons">
            <slot name="actions" :row="row" :index="$index" />
          </div>
        </template>
      </el-table-column>
    </el-table>
  </BaseCard>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { BaseCard } from '@/components/base'
import { useI18n } from 'vue-i18n'
import type { TableColumn, TableData } from './types'

interface Props {
  data?: TableData[]
  columns?: TableColumn[]
  loading?: boolean
  stripe?: boolean
  border?: boolean
  size?: 'large' | 'default' | 'small'
  fit?: boolean
  showHeader?: boolean
  highlightCurrentRow?: boolean
  selectable?: boolean
  selectableFunction?: (row: TableData, index: number) => boolean
  showIndex?: boolean
  indexLabel?: string
  indexWidth?: number | string
  indexOffset?: number
  actionsLabel?: string
  actionsWidth?: number | string
  actionsFixed?: boolean | 'left' | 'right'
  emptyText?: string
  rowClassName?: string | ((row: TableData, index: number) => string)
}

const props = withDefaults(defineProps<Props>(), {
  data: () => [],
  columns: () => [],
  loading: false,
  stripe: true,
  border: false,
  size: 'default',
  fit: true,
  showHeader: true,
  highlightCurrentRow: false,
  selectable: false,
  showIndex: false,
  indexWidth: 60,
  indexOffset: 0,
  actionsWidth: 150,
  actionsFixed: 'right'
})

const emit = defineEmits<{
  (e: 'select', selection: TableData[], row: TableData): void
  (e: 'select-all', selection: TableData[]): void
  (e: 'selection-change', selection: TableData[]): void
  (e: 'row-click', row: TableData, column: any, event: Event): void
  (e: 'row-dblclick', row: TableData, column: any, event: Event): void
  (e: 'current-change', currentRow: TableData | null, oldCurrentRow: TableData | null): void
  (e: 'sort-change', data: { column: any; prop: string; order: string | null }): void
}>()

const { t } = useI18n()
const tableRef = ref()

const tableClasses = computed(() => [
  'om-table',
  `om-table--${props.size}`
])

// 获取行类名
const getRowClassName = ({ row, rowIndex }: { row: TableData; rowIndex: number }) => {
  let className = 'om-table__row'
  
  if (typeof props.rowClassName === 'string') {
    className += ` ${props.rowClassName}`
  } else if (typeof props.rowClassName === 'function') {
    className += ` ${props.rowClassName(row, rowIndex)}`
  }
  
  return className
}

// 获取索引
const getIndex = (index: number) => {
  return index + 1 + props.indexOffset
}

// 获取单元格值
const getCellValue = (row: TableData, prop: string) => {
  return prop.split('.').reduce((obj: any, key: string) => obj?.[key], row)
}

// 事件处理
const handleSelect = (selection: TableData[], row: TableData) => {
  emit('select', selection, row)
}

const handleSelectAll = (selection: TableData[]) => {
  emit('select-all', selection)
}

const handleSelectionChange = (selection: TableData[]) => {
  emit('selection-change', selection)
}

const handleRowClick = (row: TableData, column: any, event: Event) => {
  emit('row-click', row, column, event)
}

const handleRowDblClick = (row: TableData, column: any, event: Event) => {
  emit('row-dblclick', row, column, event)
}

const handleCurrentChange = (currentRow: TableData | null, oldCurrentRow: TableData | null) => {
  emit('current-change', currentRow, oldCurrentRow)
}

const handleSortChange = (data: { column: any; prop: string; order: string | null }) => {
  emit('sort-change', data)
}

// 暴露方法
const clearSelection = () => {
  tableRef.value?.clearSelection()
}

const getSelectionRows = () => {
  return tableRef.value?.getSelectionRows() || []
}

const toggleRowSelection = (row: TableData, selected?: boolean) => {
  tableRef.value?.toggleRowSelection(row, selected)
}

const toggleAllSelection = () => {
  tableRef.value?.toggleAllSelection()
}

const setCurrentRow = (row: TableData | null) => {
  tableRef.value?.setCurrentRow(row)
}

const clearSort = () => {
  tableRef.value?.clearSort()
}

const doLayout = () => {
  tableRef.value?.doLayout()
}

const sort = (prop: string, order: 'ascending' | 'descending') => {
  tableRef.value?.sort(prop, order)
}

defineExpose({
  clearSelection,
  getSelectionRows,
  toggleRowSelection,
  toggleAllSelection,
  setCurrentRow,
  clearSort,
  doLayout,
  sort,
  tableRef
})
</script>

<style scoped>
.om-table-container {
  overflow: hidden;
}

:deep(.el-table) {
  border-radius: var(--om-radius-lg);
  background: var(--om-table-bg);
  backdrop-filter: var(--om-glass-backdrop);
  border: none;
}

:deep(.el-table__header) {
  background: var(--om-table-header-bg);
}

:deep(.el-table__header th) {
  background: transparent;
  border: none;
  color: var(--om-text-primary);
  font-weight: var(--om-font-semibold);
  padding: var(--om-space-md) var(--om-space-sm);
  font-size: var(--om-font-sm);
  border-bottom: 1px solid rgba(226, 232, 240, 0.3);
}

:deep(.el-table__body) {
  background: transparent;
}

:deep(.el-table__body tr) {
  background: var(--om-table-row-bg);
  transition: var(--om-transition-fast);
  border: none;
}

:deep(.el-table__body tr:hover) {
  background: var(--om-table-row-hover-bg);
}

:deep(.el-table__body tr.current-row) {
  background: rgba(102, 126, 234, 0.12);
}

:deep(.el-table__body td) {
  border: none;
  padding: var(--om-space-sm);
  vertical-align: middle;
  border-bottom: 1px solid rgba(226, 232, 240, 0.2);
}

:deep(.el-table__body td:last-child) {
  border-right: none;
}

:deep(.el-table__empty-block) {
  background: transparent;
}

:deep(.el-table__empty-text) {
  color: var(--om-text-secondary);
}

/* 选择框样式 */
:deep(.el-checkbox) {
  --el-checkbox-bg-color: var(--om-color-white);
  --el-checkbox-checked-bg-color: var(--om-color-primary);
  --el-checkbox-checked-border-color: var(--om-color-primary);
}

/* 排序图标样式 */
:deep(.caret-wrapper) {
  height: 34px;
}

:deep(.sort-caret) {
  border-color: var(--om-text-disabled);
}

:deep(.sort-caret.ascending) {
  border-bottom-color: var(--om-color-primary);
}

:deep(.sort-caret.descending) {
  border-top-color: var(--om-color-primary);
}

/* 操作列样式 */
.om-table__action-buttons {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--om-space-xs);
  flex-wrap: wrap;
}

:deep(.om-table__actions .cell) {
  padding-left: var(--om-space-xs);
  padding-right: var(--om-space-xs);
}

/* 加载状态样式 */
:deep(.el-loading-mask) {
  background-color: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(2px);
}

/* 尺寸变体 */
:deep(.om-table--small .el-table__header th) {
  padding: 10px var(--om-space-xs);
}

:deep(.om-table--small .el-table__body td) {
  padding: 8px var(--om-space-xs);
}

:deep(.om-table--large .el-table__header th) {
  padding: var(--om-space-lg) var(--om-space-md);
}

:deep(.om-table--large .el-table__body td) {
  padding: var(--om-space-md);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .om-table__action-buttons {
    flex-direction: column;
    gap: 4px;
  }
  
  :deep(.el-table__header th) {
    padding: 8px 4px;
    font-size: 12px;
  }
  
  :deep(.el-table__body td) {
    padding: 6px 4px;
    font-size: 12px;
  }
}
</style>