<template>
  <el-select v-model="model" style="width:100%">
    <el-option :label="$t('schedule.daily')" value="daily" />
    <el-option :label="$t('schedule.weekly')" value="weekly" />
    <el-option :label="$t('schedule.monthly')" value="monthly" />
  </el-select>
</template>
<script setup>
import { useI18n } from 'vue-i18n'
const { t } = useI18n()
const model = defineModel()
</script>
