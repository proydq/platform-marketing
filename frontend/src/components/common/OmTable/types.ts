export interface TableColumn {
  prop?: string
  key?: string
  label: string
  width?: number | string
  minWidth?: number | string
  fixed?: boolean | 'left' | 'right'
  sortable?: boolean | 'custom'
  align?: 'left' | 'center' | 'right'
  headerAlign?: 'left' | 'center' | 'right'
  showOverflowTooltip?: boolean
  className?: string
  labelClassName?: string
  formatter?: (row: TableData, column: any, cellValue: any, index: number) => string
}

export interface TableData {
  [key: string]: any
}

export interface OmTableProps {
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

export interface OmTableEmits {
  (e: 'select', selection: TableData[], row: TableData): void
  (e: 'select-all', selection: TableData[]): void
  (e: 'selection-change', selection: TableData[]): void
  (e: 'row-click', row: TableData, column: any, event: Event): void
  (e: 'row-dblclick', row: TableData, column: any, event: Event): void
  (e: 'current-change', currentRow: TableData | null, oldCurrentRow: TableData | null): void
  (e: 'sort-change', data: { column: any; prop: string; order: string | null }): void
}