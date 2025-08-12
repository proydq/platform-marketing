<template>
  <div :class="containerClasses">
    <label v-if="label" :class="labelClasses" :for="inputId">
      {{ label }}
      <span v-if="required" class="om-base-input__required">*</span>
    </label>
    
    <div class="om-base-input__wrapper">
      <div v-if="$slots.prefix || prefixIcon" class="om-base-input__prefix">
        <slot name="prefix">
          <i v-if="prefixIcon" :class="prefixIcon" />
        </slot>
      </div>
      
      <input
        :id="inputId"
        ref="inputRef"
        :class="inputClasses"
        :type="type"
        :value="modelValue"
        :placeholder="placeholder"
        :disabled="disabled"
        :readonly="readonly"
        :maxlength="maxlength"
        :minlength="minlength"
        :max="max"
        :min="min"
        :step="step"
        :autocomplete="autocomplete"
        v-bind="$attrs"
        @input="handleInput"
        @change="handleChange"
        @focus="handleFocus"
        @blur="handleBlur"
        @keydown="handleKeydown"
      />
      
      <div v-if="$slots.suffix || suffixIcon || clearable" class="om-base-input__suffix">
        <i
          v-if="clearable && modelValue && !disabled && !readonly"
          class="om-base-input__clear"
          @click="handleClear"
        >
          ×
        </i>
        <slot name="suffix">
          <i v-if="suffixIcon" :class="suffixIcon" />
        </slot>
      </div>
    </div>
    
    <div v-if="errorMessage || helpText" :class="messageClasses">
      {{ errorMessage || helpText }}
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, nextTick } from 'vue'

interface Props {
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

const props = withDefaults(defineProps<Props>(), {
  type: 'text',
  disabled: false,
  readonly: false,
  required: false,
  clearable: false,
  size: 'default'
})

const emit = defineEmits<{
  (e: 'update:modelValue', value: string | number): void
  (e: 'change', value: string | number): void
  (e: 'focus', event: FocusEvent): void
  (e: 'blur', event: FocusEvent): void
  (e: 'clear'): void
  (e: 'keydown', event: KeyboardEvent): void
}>()

const inputRef = ref<HTMLInputElement>()
const focused = ref(false)

const inputId = computed(() => `om-input-${Math.random().toString(36).substr(2, 9)}`)

const containerClasses = computed(() => [
  'om-base-input',
  `om-base-input--${props.size}`,
  {
    'om-base-input--disabled': props.disabled,
    'om-base-input--readonly': props.readonly,
    'om-base-input--focused': focused.value,
    'om-base-input--error': !!props.errorMessage
  }
])

const labelClasses = computed(() => [
  'om-base-input__label',
  {
    'om-base-input__label--required': props.required
  }
])

const inputClasses = computed(() => [
  'om-base-input__inner'
])

const messageClasses = computed(() => [
  'om-base-input__message',
  {
    'om-base-input__message--error': !!props.errorMessage,
    'om-base-input__message--help': !!props.helpText && !props.errorMessage
  }
])

const handleInput = (event: Event) => {
  const target = event.target as HTMLInputElement
  let value: string | number = target.value
  
  if (props.type === 'number') {
    value = target.valueAsNumber || 0
  }
  
  emit('update:modelValue', value)
}

const handleChange = (event: Event) => {
  const target = event.target as HTMLInputElement
  let value: string | number = target.value
  
  if (props.type === 'number') {
    value = target.valueAsNumber || 0
  }
  
  emit('change', value)
}

const handleFocus = (event: FocusEvent) => {
  focused.value = true
  emit('focus', event)
}

const handleBlur = (event: FocusEvent) => {
  focused.value = false
  emit('blur', event)
}

const handleClear = () => {
  emit('update:modelValue', '')
  emit('clear')
  nextTick(() => {
    inputRef.value?.focus()
  })
}

const handleKeydown = (event: KeyboardEvent) => {
  emit('keydown', event)
}

const focus = () => {
  inputRef.value?.focus()
}

const blur = () => {
  inputRef.value?.blur()
}

defineExpose({
  focus,
  blur,
  inputRef
})
</script>

<style scoped>
.om-base-input {
  width: 100%;
}

.om-base-input__label {
  display: block;
  font-size: var(--om-font-sm);
  font-weight: var(--om-font-medium);
  color: var(--om-text-primary);
  margin-bottom: var(--om-space-xs);
  line-height: var(--om-line-tight);
}

.om-base-input__required {
  color: var(--om-color-danger);
  margin-left: 2px;
}

.om-base-input__wrapper {
  position: relative;
  display: flex;
  align-items: center;
  border-radius: var(--om-input-radius);
  box-shadow: var(--om-input-shadow);
  border: 1px solid var(--om-input-border);
  background: var(--om-input-bg);
  transition: var(--om-transition-normal);
  overflow: hidden;
}

.om-base-input__wrapper:hover:not(.om-base-input--disabled .om-base-input__wrapper) {
  border-color: var(--om-input-border-focus);
  box-shadow: var(--om-input-shadow-hover);
}

.om-base-input--focused .om-base-input__wrapper {
  border-color: var(--om-input-border-focus);
  box-shadow: var(--om-input-shadow-focus);
}

.om-base-input--error .om-base-input__wrapper {
  border-color: var(--om-color-danger);
  box-shadow: 0 0 0 3px rgba(239, 68, 68, 0.1);
}

.om-base-input__inner {
  flex: 1;
  border: none;
  outline: none;
  background: transparent;
  padding: var(--om-space-sm) var(--om-space-md);
  font-size: var(--om-font-md);
  color: var(--om-text-primary);
  line-height: var(--om-line-normal);
}

.om-base-input__inner::placeholder {
  color: var(--om-text-disabled);
}

.om-base-input__inner:disabled {
  cursor: not-allowed;
  color: var(--om-text-disabled);
}

.om-base-input__prefix,
.om-base-input__suffix {
  display: flex;
  align-items: center;
  color: var(--om-text-secondary);
  font-size: var(--om-font-sm);
  padding: 0 var(--om-space-sm);
}

.om-base-input__clear {
  cursor: pointer;
  font-size: 18px;
  color: var(--om-text-disabled);
  transition: color var(--om-transition-fast);
  user-select: none;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 16px;
  height: 16px;
  border-radius: 50%;
  background: var(--om-color-gray-300);
  color: var(--om-color-white);
  font-size: 12px;
  font-weight: bold;
  line-height: 1;
}

.om-base-input__clear:hover {
  background: var(--om-color-gray-400);
}

.om-base-input__message {
  margin-top: var(--om-space-xs);
  font-size: var(--om-font-xs);
  line-height: var(--om-line-tight);
}

.om-base-input__message--error {
  color: var(--om-color-danger);
}

.om-base-input__message--help {
  color: var(--om-text-secondary);
}

/* 尺寸变体 */
.om-base-input--small .om-base-input__inner {
  padding: 6px var(--om-space-sm);
  font-size: var(--om-font-sm);
}

.om-base-input--large .om-base-input__inner {
  padding: 14px var(--om-space-lg);
  font-size: var(--om-font-lg);
}

/* 状态样式 */
.om-base-input--disabled .om-base-input__wrapper {
  background: var(--om-color-gray-100);
  cursor: not-allowed;
  opacity: 0.7;
}

.om-base-input--readonly .om-base-input__wrapper {
  background: var(--om-color-gray-50);
}
</style>