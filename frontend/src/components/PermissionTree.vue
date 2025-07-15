<script setup>
const props = defineProps(['roles'])
</script>

<template>
  <div>
    <div v-for="role in props.roles" :key="role.id">{{ role.name }}</div>
  </div>
</template>
