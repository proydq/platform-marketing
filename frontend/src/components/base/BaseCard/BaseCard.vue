<template>
  <div :class="cardClasses" v-bind="$attrs">
    <div v-if="$slots.header" class="om-base-card__header">
      <slot name="header" />
    </div>
    <div class="om-base-card__body">
      <slot />
    </div>
    <div v-if="$slots.footer" class="om-base-card__footer">
      <slot name="footer" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  shadow?: 'none' | 'hover' | 'always'
  padding?: 'none' | 'small' | 'default' | 'large'
  hoverable?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  shadow: 'hover',
  padding: 'default',
  hoverable: false
})

const cardClasses = computed(() => [
  'om-base-card',
  `om-base-card--shadow-${props.shadow}`,
  `om-base-card--padding-${props.padding}`,
  {
    'om-base-card--hoverable': props.hoverable
  }
])
</script>

<style scoped>
.om-base-card {
  background: var(--om-card-bg);
  border: var(--om-card-border);
  border-radius: var(--om-card-radius);
  backdrop-filter: var(--om-card-backdrop);
  transition: var(--om-card-transition);
  overflow: hidden;
}

/* 阴影变体 */
.om-base-card--shadow-none {
  box-shadow: none;
}

.om-base-card--shadow-always {
  box-shadow: var(--om-card-shadow);
}

.om-base-card--shadow-hover {
  box-shadow: var(--om-shadow-md);
}

.om-base-card--shadow-hover:hover,
.om-base-card--hoverable:hover {
  box-shadow: var(--om-card-shadow-hover);
  transform: translateY(-2px);
}

/* 内边距变体 */
.om-base-card--padding-none .om-base-card__body {
  padding: 0;
}

.om-base-card--padding-small .om-base-card__body {
  padding: var(--om-space-md);
}

.om-base-card--padding-default .om-base-card__body {
  padding: var(--om-space-lg);
}

.om-base-card--padding-large .om-base-card__body {
  padding: var(--om-space-xl);
}

/* 卡片结构 */
.om-base-card__header {
  padding: var(--om-space-lg) var(--om-space-lg) 0;
  border-bottom: 1px solid rgba(226, 232, 240, 0.5);
  margin-bottom: var(--om-space-md);
}

.om-base-card__body {
  padding: var(--om-space-lg);
}

.om-base-card__footer {
  padding: 0 var(--om-space-lg) var(--om-space-lg);
  border-top: 1px solid rgba(226, 232, 240, 0.5);
  margin-top: var(--om-space-md);
}

/* 无内边距时调整header和footer */
.om-base-card--padding-none .om-base-card__header,
.om-base-card--padding-none .om-base-card__footer {
  padding-left: var(--om-space-lg);
  padding-right: var(--om-space-lg);
}

.om-base-card--padding-small .om-base-card__header,
.om-base-card--padding-small .om-base-card__footer {
  padding-left: var(--om-space-md);
  padding-right: var(--om-space-md);
}

.om-base-card--padding-large .om-base-card__header,
.om-base-card--padding-large .om-base-card__footer {
  padding-left: var(--om-space-xl);
  padding-right: var(--om-space-xl);
}
</style>