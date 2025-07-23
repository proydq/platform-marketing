<template>
  <el-card class="page-card">
    <div class="toolbar mb-4 flex gap-2 items-center">
      <el-input v-model="query.keyword" placeholder="关键词" clearable style="width: 200px" @keyup.enter="fetchData" />
      <el-select v-model="query.channel" placeholder="渠道" clearable style="width: 120px">
        <el-option label="Email" value="email" />
        <el-option label="SMS" value="sms" />
        <el-option label="Social" value="social" />
      </el-select>
      <el-select v-model="query.status" placeholder="状态" clearable style="width: 120px">
        <el-option label="草稿" value="draft" />
        <el-option label="进行中" value="running" />
        <el-option label="暂停" value="paused" />
        <el-option label="结束" value="ended" />
      </el-select>
      <el-button type="primary" icon="Search" @click="fetchData">搜索</el-button>
      <el-button type="primary" icon="Plus" @click="openAdd">新增活动</el-button>
    </div>

    <el-table :data="list" border size="small" v-loading="loading" style="width: 100%">
      <el-table-column prop="name" label="活动名称" />
      <el-table-column prop="channel" label="渠道" width="120" />
      <el-table-column prop="status" label="状态" width="120">
        <template #default="{ row }">
          <el-tag :type="tagType(row.status)">{{ statusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="时间" width="240">
        <template #default="{ row }">
          {{ formatTime(row.startTime) }} - {{ formatTime(row.endTime) }}
        </template>
      </el-table-column>
      <el-table-column prop="description" label="描述" />
      <el-table-column label="操作" width="220">
        <template #default="{ row }">
          <el-button size="small" type="primary" @click="openEdit(row)">编辑</el-button>
          <el-button size="small" @click="toggleStatus(row)">{{ row.status === 'paused' ? '启动' : '暂停' }}</el-button>
          <el-button size="small" type="danger" @click="remove(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="text-right mt-4">
      <el-pagination
        background
        v-model:current-page="page"
        v-model:page-size="size"
        :total="total"
        layout="total, prev, pager, next"
        @current-change="fetchData"
      />
    </div>

    <el-dialog v-model="dialogVisible" width="500px">
      <template #title>
        <strong>{{ isEdit ? '编辑活动' : '新增活动' }}</strong>
      </template>
      <el-form :model="form" label-width="90px">
        <el-form-item label="名称">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="渠道">
          <el-select v-model="form.channel" style="width: 100%">
            <el-option label="Email" value="email" />
            <el-option label="SMS" value="sms" />
            <el-option label="Social" value="social" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status" style="width: 100%">
            <el-option label="草稿" value="draft" />
            <el-option label="进行中" value="running" />
            <el-option label="暂停" value="paused" />
            <el-option label="结束" value="ended" />
          </el-select>
        </el-form-item>
        <el-form-item label="开始时间">
          <el-date-picker v-model="form.startTime" type="datetime" style="width: 100%" />
        </el-form-item>
        <el-form-item label="结束时间">
          <el-date-picker v-model="form.endTime" type="datetime" style="width: 100%" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import marketingApi from '@/api/marketingCampaign'

const list = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const loading = ref(false)

const query = reactive({ keyword: '', channel: '', status: '' })

const dialogVisible = ref(false)
const isEdit = ref(false)
const saving = ref(false)

const form = reactive({
  id: '',
  name: '',
  channel: '',
  status: 'draft',
  startTime: '',
  endTime: '',
  description: ''
})

onMounted(fetchData)

function fetchData() {
  loading.value = true
  marketingApi
    .list({
      page: page.value - 1,
      size: size.value,
      keyword: query.keyword,
      channel: query.channel,
      status: query.status
    })
    .then(res => {
      list.value = res.records || []
      total.value = res.total || 0
    })
    .finally(() => (loading.value = false))
}

function openAdd() {
  isEdit.value = false
  Object.assign(form, {
    id: '',
    name: '',
    channel: '',
    status: 'draft',
    startTime: '',
    endTime: '',
    description: ''
  })
  dialogVisible.value = true
}

function openEdit(row) {
  isEdit.value = true
  Object.assign(form, row)
  dialogVisible.value = true
}

function save() {
  saving.value = true
  const handler = isEdit.value ? marketingApi.update : marketingApi.create
  handler({ ...form })
    .then(() => {
      ElMessage.success('保存成功')
      dialogVisible.value = false
      fetchData()
    })
    .finally(() => (saving.value = false))
}

function remove(id) {
  ElMessageBox.confirm('确定删除该活动吗？', '警告', { type: 'warning' })
    .then(() => marketingApi.delete(id))
    .then(() => {
      ElMessage.success('删除成功')
      fetchData()
    })
}

function toggleStatus(row) {
  const newStatus = row.status === 'paused' ? 'running' : 'paused'
  marketingApi.updateStatus(row.id, newStatus).then(() => {
    row.status = newStatus
    ElMessage.success('状态已更新')
  })
}

function formatTime(t) {
  if (!t) return ''
  return String(t).replace('T', ' ').substring(0, 16)
}

function statusText(s) {
  const map = { draft: '草稿', running: '进行中', paused: '暂停', ended: '结束' }
  return map[s] || s
}

function tagType(s) {
  const map = { running: 'success', paused: 'warning', ended: 'info' }
  return map[s] || ''
}
</script>

<style scoped>
</style>

