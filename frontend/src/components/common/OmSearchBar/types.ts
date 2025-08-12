export interface SearchOption {
  label: string
  value: any
}

export interface SearchSchema {
  key: string
  label: string
  type?: 'text' | 'select' | 'date' | 'daterange' | 'number' | 'email' | 'tel'
  placeholder?: string
  startPlaceholder?: string
  endPlaceholder?: string
  options?: SearchOption[]
  multiple?: boolean
  dateType?: 'date' | 'datetime' | 'month' | 'year'
  min?: number
  max?: number
}

export interface SearchQuery {
  keyword?: string
  [key: string]: any
}

export interface OmSearchBarProps {
  modelValue?: SearchQuery
  schema?: SearchSchema[]
  keywordPlaceholder?: string
  loading?: boolean
  defaultExpanded?: boolean
}

export interface OmSearchBarEmits {
  (e: 'update:modelValue', value: SearchQuery): void
  (e: 'search', query: SearchQuery): void
  (e: 'reset'): void
}