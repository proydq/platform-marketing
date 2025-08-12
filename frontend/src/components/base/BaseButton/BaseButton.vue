<template>
  <button
    :class="buttonClasses"
    :disabled="disabled"
    :type="nativeType"
    v-bind="$attrs"
    @click="handleClick"
  >
    <slot name="icon" />
    <span v-if="$slots.default" :class="{ 'ml-2': $slots.icon }">
      <slot />
    </span>
  </button>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  variant?: 'primary' | 'success' | 'warning' | 'danger' | 'info' | 'default'
  size?: 'small' | 'default' | 'large'
  disabled?: boolean
  loading?: boolean
  round?: boolean
  circle?: boolean
  nativeType?: 'button' | 'submit' | 'reset'
}

const props = withDefaults(defineProps<Props>(), {
  variant: 'default',
  size: 'default',
  disabled: false,
  loading: false,
  round: false,
  circle: false,
  nativeType: 'button'
})

const emit = defineEmits<{
  (e: 'click', event: MouseEvent): void
}>()

const buttonClasses = computed(() => [
  'om-base-button',
  `om-base-button--${props.variant}`,
  `om-base-button--${props.size}`,
  {
    'om-base-button--disabled': props.disabled,
    'om-base-button--loading': props.loading,
    'om-base-button--round': props.round,
    'om-base-button--circle': props.circle
  }
])

const handleClick = (event: MouseEvent) => {
  if (!props.disabled && !props.loading) {
    emit('click', event)
  }
}
</script>

<style scoped>
.om-base-button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: var(--om-space-xs);
  padding: var(--om-button-padding);
  border: none;
  border-radius: var(--om-button-radius);
  font-size: var(--om-font-md);
  font-weight: var(--om-font-medium);
  line-height: 1;
  cursor: pointer;
  transition: var(--om-transition-normal);
  text-decoration: none;
  outline: none;
  user-select: none;
  white-space: nowrap;
  vertical-align: middle;
  background: var(--om-color-white);
  color: var(--om-text-primary);
  box-shadow: var(--om-shadow-sm);
  border: 1px solid var(--om-color-gray-200);
}

.om-base-button:hover:not(.om-base-button--disabled):not(.om-base-button--loading) {
  transform: translateY(-1px);
  box-shadow: var(--om-shadow-md);
}

.om-base-button:active:not(.om-base-button--disabled):not(.om-base-button--loading) {
  transform: translateY(0);
}

/* 变体样式 */
.om-base-button--primary {
  background: var(--om-color-brand-gradient);
  color: var(--om-text-inverse);
  border: none;
  box-shadow: var(--om-shadow-button);
}

.om-base-button--primary:hover:not(.om-base-button--disabled):not(.om-base-button--loading) {
  box-shadow: var(--om-shadow-button-hover);
}

.om-base-button--success {
  background: var(--om-color-success-gradient);
  color: var(--om-text-inverse);
  border: none;
}

.om-base-button--warning {
  background: var(--om-color-warning-gradient);
  color: var(--om-text-inverse);
  border: none;
}

.om-base-button--danger {
  background: var(--om-color-danger-gradient);
  color: var(--om-text-inverse);
  border: none;
}

.om-base-button--info {
  background: var(--om-color-info-gradient);
  color: var(--om-text-inverse);
  border: none;
}

/* 尺寸样式 */
.om-base-button--small {
  padding: var(--om-button-padding-sm);
  font-size: var(--om-font-sm);
}

.om-base-button--large {
  padding: var(--om-button-padding-lg);
  font-size: var(--om-font-lg);
}

/* 状态样式 */
.om-base-button--disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none !important;
}

.om-base-button--loading {
  cursor: default;
  pointer-events: none;
}

.om-base-button--loading::before {
  content: '';
  display: inline-block;
  width: 14px;
  height: 14px;
  border: 2px solid currentColor;
  border-radius: 50%;
  border-right-color: transparent;
  margin-right: var(--om-space-xs);
  animation: om-button-loading-spin 1s linear infinite;
}

/* 形状样式 */
.om-base-button--round {
  border-radius: 50px;
}

.om-base-button--circle {
  width: 40px;
  height: 40px;
  padding: 0;
  border-radius: 50%;
}

.om-base-button--circle.om-base-button--small {
  width: 32px;
  height: 32px;
}

.om-base-button--circle.om-base-button--large {
  width: 48px;
  height: 48px;
}

/* 加载动画 */
@keyframes om-button-loading-spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

.ml-2 {
  margin-left: var(--om-space-xs);
}
</style>