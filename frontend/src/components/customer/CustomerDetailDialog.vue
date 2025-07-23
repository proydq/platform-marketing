<template>
  <el-dialog v-model="visible" title="客户详情" width="400px">
    <el-descriptions :column="1" border>
      <el-descriptions-item label="名称">{{ detail.name }}</el-descriptions-item>
      <el-descriptions-item label="邮箱">{{ detail.email }}</el-descriptions-item>
      <el-descriptions-item label="电话">{{ detail.phone }}</el-descriptions-item>
      <el-descriptions-item label="来源">{{ detail.source }}</el-descriptions-item>
      <el-descriptions-item label="状态">{{ detail.status }}</el-descriptions-item>
      <el-descriptions-item label="备注">{{ detail.remark }}</el-descriptions-item>
    </el-descriptions>
  </el-dialog>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { getCustomer } from '@/api/customer'

const visible = ref(false)
const detail = reactive({
  name: '',
  email: '',
  phone: '',
  source: '',
  status: '',
  remark: ''
})

function open(id) {
  if (!id) return
  getCustomer(id).then(res => {
    Object.assign(detail, res.data || {})
    visible.value = true
  })
}

defineExpose({ open })
</script>

<style scoped>
</style>
