<template>
  <div class="page-wrapper">
    <el-card class="card-container">
      <h2 style="margin-bottom:20px;">
        <span class="icon">📊</span> 营销活动中心
      </h2>
      <el-row class="action-buttons" justify="space-between" align="middle">
        <el-space>
          <el-select v-model="statusFilter" placeholder="状态" style="width:120px">
            <el-option label="全部" value="" />
            <el-option label="未开始" value="pending" />
            <el-option label="进行中" value="running" />
            <el-option label="已完成" value="success" />
            <el-option label="暂停" value="paused" />
          </el-select>
          <el-select v-model="channelFilter" placeholder="渠道" style="width:120px">
            <el-option label="全部" value="" />
            <el-option label="邮件" value="email" />
            <el-option label="社媒" value="social" />
            <el-option label="微信" value="wechat" />
            <el-option label="自定义" value="custom" />
          </el-select>
          <el-input v-model="search" placeholder="搜索活动" clearable style="width:200px" />
        </el-space>
        <el-button type="primary" @click="openEdit(false)"><span class="icon">➕</span>新建活动</el-button>
      </el-row>

      <el-table :data="filtered" style="width:100%;margin-top:20px;">
        <el-table-column prop="name" label="名称" min-width="150" />
        <el-table-column label="渠道" width="160">
          <template #default="{ row }">
            <el-tag v-for="c in row.channels" :key="c" size="small" style="margin-right:4px">{{ channelName(c) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="120">
          <template #default="{ row }">
            <span :class="'status-badge status-' + row.status">{{ statusText(row.status) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始" width="140" />
        <el-table-column prop="endTime" label="结束" width="140" />
        <el-table-column label="操作" width="260">
          <template #default="{ row }">
            <el-button type="text" size="small" @click="openDetail(row)">查看</el-button>
            <el-button type="text" size="small" @click="openEdit(true, row)">编辑</el-button>
            <el-button type="text" size="small" @click="publish(row)">发布</el-button>
            <el-button type="text" size="small" @click="togglePause(row)">{{ row.status === 'paused' ? '恢复' : '暂停' }}</el-button>
            <el-popconfirm title="确定删除?" @confirm="remove(row)">
              <el-button type="text" size="small" style="color:#f56c6c">删除</el-button>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-drawer v-model="editDrawer" :title="editMode ? '编辑活动' : '新建活动'" size="40%">
      <el-form :model="form" label-width="90px" class="form-section">
        <el-form-item label="名称" required>
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="渠道" required>
          <el-select v-model="form.channels" multiple style="width:100%">
            <el-option label="邮件" value="email" />
            <el-option label="社媒" value="social" />
            <el-option label="微信" value="wechat" />
            <el-option label="自定义" value="custom" />
          </el-select>
        </el-form-item>
        <el-form-item label="开始时间">
          <el-date-picker v-model="form.startTime" type="datetime" style="width:100%" />
        </el-form-item>
        <el-form-item label="结束时间">
          <el-date-picker v-model="form.endTime" type="datetime" style="width:100%" />
        </el-form-item>
        <el-form-item label="内容">
          <div v-for="(c,i) in form.contents" :key="i" class="campaign-card" style="margin-bottom:10px;">
            <RichTextEditor v-model="form.contents[i]" />
          </div>
          <el-button size="small" @click="addContent">添加内容段</el-button>
        </el-form-item>
        <el-form-item label="周期设置">
          <el-select v-model="form.cycle" style="width:100%">
            <el-option label="单次" value="once" />
            <el-option label="每天" value="daily" />
            <el-option label="每周" value="weekly" />
          </el-select>
        </el-form-item>
        <div class="action-buttons" style="justify-content:flex-end;">
          <el-button @click="editDrawer=false">取消</el-button>
          <el-button type="primary" @click="save">保存</el-button>
        </div>
      </el-form>
    </el-drawer>

    <el-drawer v-model="detailDrawer" title="活动详情" direction="rtl" size="40%">
      <template v-if="current">
        <el-descriptions :title="current.name" column="1" border style="margin-bottom:20px;">
          <el-descriptions-item label="状态">
            <span :class="'status-badge status-' + current.status">{{ statusText(current.status) }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="渠道">
            <el-tag v-for="c in current.channels" :key="c" size="small" style="margin-right:4px">{{ channelName(c) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="起止时间">{{ current.startTime }} - {{ current.endTime }}</el-descriptions-item>
        </el-descriptions>
        <el-row :gutter="20" style="margin-bottom:20px;">
          <el-col :span="8">
            <el-card>
              <div class="stat-number">{{ current.metrics.sent }}</div>
              <div class="stat-label">发送量</div>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card>
              <div class="stat-number">{{ current.metrics.opens }}</div>
              <div class="stat-label">打开量</div>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card>
              <div class="stat-number">{{ current.metrics.clicks }}</div>
              <div class="stat-label">点击量</div>
            </el-card>
          </el-col>
        </el-row>
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
import RichTextEditor from '../components/RichTextEditor.vue'
import campaignList from '../mock/campaignList.json'
import campaignDetails from '../mock/campaignDetail.json'

const campaigns = ref([])
const statusFilter = ref('')
const channelFilter = ref('')
const search = ref('')

const editDrawer = ref(false)
const detailDrawer = ref(false)
const editMode = ref(false)
const current = ref(null)

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
  const map = { pending: '未开始', running: '进行中', success: '已完成', paused: '暂停' }
  return map[s] || s
}

function channelName(c) {
  const map = { email: '邮件', social: '社媒', wechat: '微信', custom: '自定义' }
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
  ElMessage.success('保存成功')
}

function remove(row) {
  campaigns.value = campaigns.value.filter(c => c.id !== row.id)
  ElMessage.success('已删除')
}

function openDetail(row) {
  current.value = { ...row, ...campaignDetails.find(d => d.id === row.id) }
  if (!current.value.contents) current.value.contents = row.contents || []
  detailDrawer.value = true
}

function publish(row) {
  row.status = 'running'
  ElMessage.success('已发布')
}

function togglePause(row) {
  row.status = row.status === 'paused' ? 'running' : 'paused'
  ElMessage.success(row.status === 'paused' ? '已暂停' : '已恢复')
}
</script>
