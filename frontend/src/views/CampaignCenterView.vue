<template>
  <div class="page-wrapper">
    <el-card class="card-container">
      <h2 style="margin-bottom:20px;">
        <span class="icon">📊</span> {{ $t('campaign.center') }}
      </h2>
      <el-row class="action-buttons" justify="space-between" align="middle">
        <el-space>
          <el-select v-model="statusFilter" :placeholder="$t('campaign.status')" style="width:120px">
            <el-option :label="$t('common.all')" value="" />
            <el-option :label="$t('campaign.pending')" value="pending" />
            <el-option :label="$t('campaign.running')" value="running" />
            <el-option :label="$t('campaign.success')" value="success" />
            <el-option :label="$t('campaign.pause')" value="paused" />
          </el-select>
          <el-select v-model="channelFilter" :placeholder="$t('campaign.channel')" style="width:120px">
            <el-option :label="$t('common.all')" value="" />
            <el-option label="Email" value="email" />
            <el-option :label="$t('sidebar.socialMedia')" value="social" />
            <el-option label="Wechat" value="wechat" />
            <el-option :label="$t('common.custom')" value="custom" />
          </el-select>
          <el-input v-model="search" :placeholder="$t('campaign.search')" clearable style="width:200px" />
        </el-space>
        <el-button type="primary" @click="openEdit(false)"><span class="icon">➕</span>{{ $t('campaign.new') }}</el-button>
      </el-row>

      <el-table :data="filtered" style="width:100%;margin-top:20px;">
        <el-table-column prop="name" :label="$t('campaign.name')" min-width="150" />
        <el-table-column :label="$t('campaign.channel')" width="160">
          <template #default="{ row }">
            <el-tag v-for="c in row.channels" :key="c" size="small" style="margin-right:4px">{{ channelName(c) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column :label="$t('campaign.status')" width="120">
          <template #default="{ row }">
            <span :class="'status-badge status-' + row.status">{{ statusText(row.status) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="startTime" :label="$t('campaign.start')" width="140" />
        <el-table-column prop="endTime" :label="$t('campaign.end')" width="140" />
        <el-table-column :label="$t('campaign.operation')" width="260">
          <template #default="{ row }">
            <el-button type="text" size="small" @click="openDetail(row)">{{ $t('campaign.view') }}</el-button>
            <el-button type="text" size="small" @click="openEdit(true, row)">{{ $t('campaign.edit') }}</el-button>
            <el-button type="text" size="small" @click="publish(row)">{{ $t('campaign.publish') }}</el-button>
            <el-button type="text" size="small" @click="togglePause(row)">{{ row.status === 'paused' ? $t('campaign.resume') : $t('campaign.pause') }}</el-button>
            <el-popconfirm :title="$t('common.deleteConfirm')" @confirm="remove(row)">
              <el-button type="text" size="small" style="color:#f56c6c">{{ $t('campaign.delete') }}</el-button>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-drawer v-model="editDrawer" :title="editMode ? $t('campaign.edit') : $t('campaign.new')" size="40%">
      <el-form :model="form" label-width="90px" class="form-section">
        <el-form-item :label="$t('campaign.name')" required>
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item :label="$t('campaign.channel')" required>
          <el-select v-model="form.channels" multiple style="width:100%">
            <el-option label="Email" value="email" />
            <el-option :label="$t('sidebar.socialMedia')" value="social" />
            <el-option label="Wechat" value="wechat" />
            <el-option :label="$t('campaign.custom')" value="custom" />
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('campaign.start')">
          <el-date-picker v-model="form.startTime" type="datetime" style="width:100%" />
        </el-form-item>
        <el-form-item :label="$t('campaign.end')">
          <el-date-picker v-model="form.endTime" type="datetime" style="width:100%" />
        </el-form-item>
        <el-form-item :label="$t('campaign.content')">
          <div v-for="(c,i) in form.contents" :key="i" class="campaign-card" style="margin-bottom:10px;">
            <RichTextEditor v-model="form.contents[i]" />
          </div>
          <el-button size="small" @click="addContent">{{ $t('campaign.addContent') }}</el-button>
        </el-form-item>
        <el-form-item :label="$t('campaign.cycle')">
          <el-select v-model="form.cycle" style="width:100%">
            <el-option :label="$t('campaign.once')" value="once" />
            <el-option :label="$t('campaign.daily')" value="daily" />
            <el-option :label="$t('campaign.weekly')" value="weekly" />
          </el-select>
        </el-form-item>
        <div class="action-buttons" style="justify-content:flex-end;">
          <el-button @click="editDrawer=false">{{ $t('campaign.cancel') }}</el-button>
          <el-button type="primary" @click="save">{{ $t('campaign.save') }}</el-button>
        </div>
      </el-form>
    </el-drawer>

    <el-drawer v-model="detailDrawer" :title="$t('campaign.detail')" direction="rtl" size="40%">
      <template v-if="current">
        <el-descriptions :title="current.name" column="1" border style="margin-bottom:20px;">
          <el-descriptions-item :label="$t('campaign.status')">
            <span :class="'status-badge status-' + current.status">{{ statusText(current.status) }}</span>
          </el-descriptions-item>
          <el-descriptions-item :label="$t('campaign.channel')">
            <el-tag v-for="c in current.channels" :key="c" size="small" style="margin-right:4px">{{ channelName(c) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item :label="$t('campaign.start') + '/' + $t('campaign.end')">{{ current.startTime }} - {{ current.endTime }}</el-descriptions-item>
        </el-descriptions>
        <el-card class="chart-container" style="margin-bottom:20px;">
          <FunnelChart :data="metricsFunnel" />
        </el-card>
        <el-steps :active="current.steps ? current.steps.length : 0" finish-status="success" style="margin-bottom:20px;">
          <el-step v-for="(s,i) in current.steps" :key="i" :title="s" />
        </el-steps>
        <div v-for="(c,i) in current.contents" :key="i" style="margin-bottom:10px;" v-html="c" />
      </template>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useI18n } from 'vue-i18n'
import FunnelChart from '../components/charts/FunnelChart.vue'
import RichTextEditor from '../components/RichTextEditor.vue'
import campaignList from '../mock/campaignList.json'
import campaignDetails from '../mock/campaignDetail.json'

const campaigns = ref([])
const statusFilter = ref('')
const channelFilter = ref('')
const search = ref('')

const { t } = useI18n()
const editDrawer = ref(false)
const detailDrawer = ref(false)
const editMode = ref(false)
const current = ref(null)
const metricsFunnel = computed(() => {
  if (!current.value || !current.value.metrics) return []
  return [
    { step: t('campaign.sent'), value: current.value.metrics.sent },
    { step: t('campaign.opens'), value: current.value.metrics.opens },
    { step: t('campaign.clicks'), value: current.value.metrics.clicks }
  ]
})

const form = reactive({
  id: null,
  name: '',
  channels: [],
  startTime: '',
  endTime: '',
  contents: [''],
  cycle: 'once'
})

onMounted(() => {
  campaigns.value = campaignList
})

const filtered = computed(() => {
  return campaigns.value.filter(c => {
    const sOk = !statusFilter.value || c.status === statusFilter.value
    const cOk = !channelFilter.value || c.channels.includes(channelFilter.value)
    const qOk = !search.value || c.name.includes(search.value)
    return sOk && cOk && qOk
  })
})

function statusText(s) {
  const map = { pending: t('campaign.pending'), running: t('campaign.running'), success: t('campaign.success'), paused: t('campaign.pause') }
  return map[s] || s
}

function channelName(c) {
  const map = { email: 'Email', social: t('sidebar.socialMedia'), wechat: 'Wechat', custom: t('campaign.custom') }
  return map[c] || c
}

function openEdit(edit, row) {
  editMode.value = edit
  if (edit && row) {
    Object.assign(form, JSON.parse(JSON.stringify(row)))
    if (!form.contents || !form.contents.length) form.contents = ['']
  } else {
    Object.assign(form, { id: null, name: '', channels: [], startTime: '', endTime: '', contents: [''], cycle: 'once' })
  }
  editDrawer.value = true
}

function addContent() {
  form.contents.push('')
}

function save() {
  if (editMode.value) {
    const idx = campaigns.value.findIndex(c => c.id === form.id)
    if (idx !== -1) campaigns.value[idx] = JSON.parse(JSON.stringify(form))
  } else {
    form.id = Math.max(0, ...campaigns.value.map(c => c.id)) + 1
    campaigns.value.push(JSON.parse(JSON.stringify(form)))
  }
  editDrawer.value = false
  ElMessage.success(t('common.saveSuccess'))
}

function remove(row) {
  campaigns.value = campaigns.value.filter(c => c.id !== row.id)
  ElMessage.success(t('common.deleted'))
}

function openDetail(row) {
  current.value = { ...row, ...campaignDetails.find(d => d.id === row.id) }
  if (!current.value.contents) current.value.contents = row.contents || []
  detailDrawer.value = true
}


function publish(row) {
  row.status = 'running'
  ElMessage.success(t('campaign.publish'))
}

function togglePause(row) {
  row.status = row.status === 'paused' ? 'running' : 'paused'
  ElMessage.success(row.status === 'paused' ? t('campaign.pause') : t('campaign.resume'))
}
</script>
