<template>
  <div class="page-wrapper">
    <el-card class="card-container">
      <h2 style="margin-bottom:20px;">
        <span class="icon">🔔</span> {{ $t('notification.center') }}
      </h2>

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

      <el-divider />

      <el-space direction="vertical" style="width:100%;" :size="16">
        <transition-group name="fade-list" tag="div">
          <el-card
            v-for="item in filtered"
            :key="item.id"
            class="notify-card"
            shadow="hover"
            style="margin-bottom:10px;"
          >
            <template #header>
              <div style="display:flex;align-items:center;gap:6px;">
                <el-checkbox :model-value="selected.includes(item.id)" @change="toggleSelect(item.id)" />
                <span>{{ iconFor(item) }}</span>
                <span style="flex:1">{{ item.title }}</span>
                <span
                  :class="['status-badge', item.status === 'unread' ? 'status-unread' : 'status-read']"
                >{{ item.status === 'unread' ? $t('notification.unread') : $t('notification.read') }}</span>
              </div>
            </template>

            <div>{{ item.content }}</div>
            <div style="text-align:right;margin-top:10px;">
              {{ formatTime(item.time) }}
              <el-button text size="small" @click.stop="view(item)">{{ $t('notification.view') }}</el-button>
              <el-button text size="small" @click.stop="markRead(item)">{{ $t('notification.markRead') }}</el-button>
              <el-button text size="small" style="color:#f56c6c" @click.stop="remove(item)">{{ $t('notification.delete') }}</el-button>
            </div>
          </el-card>
        </transition-group>
      </el-space>

      <el-drawer v-model="drawer" :title="$t('notification.detail')" size="30%" direction="rtl">
        <h3>{{ current.title }}</h3>
        <p class="text-gray">{{ formatTime(current.time) }}</p>
        <p style="margin:10px 0;">{{ current.content }}</p>
        <a v-if="current.link" :href="current.link" target="_blank" style="color:#409eff;">{{ $t('notification.view') }}</a>
      </el-drawer>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
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
</style>

