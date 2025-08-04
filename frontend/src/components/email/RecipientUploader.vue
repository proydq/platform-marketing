<template>
  <div class="recipient-uploader">
    <el-upload
      :show-file-list="false"
      accept=".csv"
      :before-upload="parseCsv"
    >
      <el-button type="primary">{{ t('email.uploadCSV') }}</el-button>
    </el-upload>
    <ul v-if="recipients.length" class="recipient-list">
      <li v-for="r in recipients" :key="r.email">{{ r.name }} - {{ r.email }}</li>
    </ul>
  </div>
</template>
<script setup>
import { ref, watch } from 'vue'
import Papa from 'papaparse'
import { useI18n } from 'vue-i18n'
const props = defineProps({
  modelValue: { type: Array, default: () => [] }
})
const emit = defineEmits(['update:modelValue'])
const { t } = useI18n()

const recipients = ref(props.modelValue)
watch(() => props.modelValue, v => (recipients.value = v))
watch(recipients, v => emit('update:modelValue', v))

function parseCsv(file) {
  Papa.parse(file, {
    complete: (res) => {
      const data = res.data
        .filter((row) => row.length >= 2)
        .map((row) => ({ name: row[0], email: row[1] }))
        .filter((r) => /.+@.+\..+/.test(r.email))
      recipients.value = data
    }
  })
  return false
}
</script>
<style scoped>
.recipient-list {
  margin-top: 10px;
  padding-left: 20px;
}
</style>
