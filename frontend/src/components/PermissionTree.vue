<script setup>
const props = defineProps(['roles'])
</script>

<template>
  <div class="permission-tree">

    <div v-for="role in props.roles" :key="role.id">{{ role.name }}</div>
  </div>
</template>
