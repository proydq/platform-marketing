<template>
  <div class="page-wrapper" style="background:#f6f9fc;">
    <h2 class="section-title" style="font-size:20px;margin-bottom:16px;">
      <el-icon><Bell /></el-icon>{{ $t('notification.center') }}
    </h2>

    <el-card class="section-card">
      <el-row class="action-buttons" justify="space-between" align="middle">
        <el-space>
          <el-select v-model="typeFilter" :placeholder="$t('notification.type')" style="width:120px">
            <el-option :label="$t('common.all')" value="" />
            <el-option label="Message" value="message" />
            <el-option label="Task" value="task" />
            <el-option label="System" value="system" />
          </el-select>
          <el-select v-model="statusFilter" :placeholder="$t('notification.status')" style="width:120px">
            <el-option :label="$t('common.all')" value="" />
            <el-option :label="$t('notification.unread')" value="unread" />
            <el-option :label="$t('notification.read')" value="read" />
          </el-select>
        </el-space>
        <el-space>
          <el-button size="small" :disabled="!selected.length" @click="markSelectedRead">{{ $t('notification.markRead') }}</el-button>
          <el-button size="small" type="danger" :disabled="!selected.length" @click="deleteSelected">{{ $t('notification.delete') }}</el-button>
        </el-space>
      </el-row>
    </el-card>

    <div class="notification-list">
      <transition-group name="fade-list" tag="div">
        <el-card v-for="item in filtered" :key="item.id" class="notify-card" body-style="padding:0;">
          <el-row class="notify-row" align="middle">
            <el-col :span="1">
              <el-checkbox :model-value="selected.includes(item.id)" @change="toggleSelect(item.id)" />
            </el-col>
            <el-col :span="1" class="icon-col">{{ iconFor(item) }}</el-col>
            <el-col :span="11">
              <div class="title-line">
                <span class="title">{{ item.title }}</span>
                <span
                  class="status-badge"
                  :class="item.status === 'unread' ? 'status-unread' : 'status-read'"
                >
                  {{ item.status === 'unread' ? $t('notification.unread') : $t('notification.read') }}
                </span>
              </div>
              <div class="desc text-gray">{{ item.content }}</div>
            </el-col>
            <el-col :span="6" class="time-col">{{ formatTime(item.time) }}</el-col>
            <el-col :span="5" class="action-col">
              <el-button text size="small" @click.stop="view(item)">{{ $t('notification.view') }}</el-button>
              <el-button text size="small" @click.stop="markRead(item)">{{ $t('notification.markRead') }}</el-button>
              <el-button text size="small" style="color:#f56c6c" @click.stop="remove(item)">{{ $t('notification.delete') }}</el-button>
            </el-col>
          </el-row>
        </el-card>
      </transition-group>
    </div>

    <el-drawer v-model="drawer" :title="$t('notification.detail')" size="30%" direction="rtl">
      <h3>{{ current.title }}</h3>
      <p class="text-gray">{{ formatTime(current.time) }}</p>
      <p style="margin:10px 0;">{{ current.content }}</p>
      <a v-if="current.link" :href="current.link" target="_blank" style="color:#409eff;">{{ $t('notification.view') }}</a>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Bell } from '@element-plus/icons-vue'
import { useI18n } from 'vue-i18n'
import data from '../mock/notifications.json'

const list = ref([...data])
const typeFilter = ref('')
const statusFilter = ref('')
const selected = ref([])

const { t } = useI18n()

const drawer = ref(false)
const current = ref({})

const iconMap = { message: '🔔', task: '📋', system: '⚠️' }

const filtered = computed(() => {
  return list.value.filter(n => {
    const typeOk = !typeFilter.value || n.type === typeFilter.value
    const statusOk = !statusFilter.value || n.status === statusFilter.value
    return typeOk && statusOk
  })
})

function formatTime(t) {
  const time = new Date(t)
  const diff = Date.now() - time.getTime()
  const mins = Math.floor(diff / 60000)
  if (mins < 60) return `${mins}${t('notification.minutesAgo')}`
  const Y = time.getFullYear()
  const M = String(time.getMonth() + 1).padStart(2, '0')
  const D = String(time.getDate()).padStart(2, '0')
  const h = String(time.getHours()).padStart(2, '0')
  const m = String(time.getMinutes()).padStart(2, '0')
  return `${Y}-${M}-${D} ${h}:${m}`
}

function iconFor(item) {
  return iconMap[item.type] || '🔔'
}

function toggleSelect(id) {
  const i = selected.value.indexOf(id)
  if (i === -1) selected.value.push(id)
  else selected.value.splice(i, 1)
}

function view(item) {
  current.value = item
  drawer.value = true
  if (item.status === 'unread') item.status = 'read'
}

function markRead(item) {
  if (item.status !== 'read') {
    item.status = 'read'
    ElMessage.success(t('notification.markRead'))
  }
  selected.value = selected.value.filter(i => i !== item.id)
}

function remove(item) {
  list.value = list.value.filter(n => n.id !== item.id)
  selected.value = selected.value.filter(i => i !== item.id)
  ElMessage.success(t('notification.delete'))
}

function markSelectedRead() {
  list.value.forEach(n => {
    if (selected.value.includes(n.id)) n.status = 'read'
  })
  selected.value = []
  ElMessage.success(t('notification.markRead'))
}

function deleteSelected() {
  list.value = list.value.filter(n => !selected.value.includes(n.id))
  selected.value = []
  ElMessage.success(t('notification.delete'))
}
</script>

<style scoped>
.text-gray {
  color: #666;
}
.notification-list {
  margin-top: 16px;
}
.notify-card {
  margin-bottom: 16px;
}
.notify-row {
  background: #fff;
  padding: 0 16px;
  height: 80px;
  display: flex;
  align-items: center;
  transition: background-color 0.3s;
}
.notify-row:hover {
  background: #f5f7fa;
}
.action-col {
  text-align: right;
}
.action-col .el-button + .el-button {
  margin-left: 8px;
}
.title-line {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
}
</style>
