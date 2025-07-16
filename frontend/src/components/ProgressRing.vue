<template>
  <div class="progress-ring">{{ percentage }}%</div>
</template>
<script setup>
const props = defineProps({ percentage: { type: Number, default: 0 } })
</script>
