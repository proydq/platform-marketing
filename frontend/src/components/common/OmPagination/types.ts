export interface OmPaginationProps {
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

export interface OmPaginationEmits {
  (e: 'update:current-page', value: number): void
  (e: 'update:page-size', value: number): void
  (e: 'size-change', value: number): void
  (e: 'current-change', value: number): void
  (e: 'prev-click', value: number): void
  (e: 'next-click', value: number): void
}