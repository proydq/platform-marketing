export interface BaseButtonProps {
  variant?: 'primary' | 'success' | 'warning' | 'danger' | 'info' | 'default'
  size?: 'small' | 'default' | 'large'
  disabled?: boolean
  loading?: boolean
  round?: boolean
  circle?: boolean
  nativeType?: 'button' | 'submit' | 'reset'
}

export interface BaseButtonEmits {
  (e: 'click', event: MouseEvent): void
}