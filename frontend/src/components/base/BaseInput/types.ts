export interface BaseInputProps {
  modelValue?: string | number
  type?: 'text' | 'password' | 'email' | 'number' | 'tel' | 'url'
  label?: string
  placeholder?: string
  disabled?: boolean
  readonly?: boolean
  required?: boolean
  clearable?: boolean
  size?: 'small' | 'default' | 'large'
  maxlength?: number
  minlength?: number
  max?: number
  min?: number
  step?: number
  prefixIcon?: string
  suffixIcon?: string
  errorMessage?: string
  helpText?: string
  autocomplete?: string
}

export interface BaseInputEmits {
  (e: 'update:modelValue', value: string | number): void
  (e: 'change', value: string | number): void
  (e: 'focus', event: FocusEvent): void
  (e: 'blur', event: FocusEvent): void
  (e: 'clear'): void
  (e: 'keydown', event: KeyboardEvent): void
}