<template>
  <div>
    <el-card class="action-buttons" style="margin-bottom:20px;">
      <el-row :gutter="20" align="middle">
        <el-col :span="12" class="flex items-center">
          <el-input v-model="search" placeholder="搜索活动" prefix-icon="Search" style="width:220px;margin-right:10px" clearable />
          <el-select v-model="filterChannel" placeholder="渠道" clearable style="width:120px;margin-right:10px">
            <el-option label="邮件" value="email" />
            <el-option label="社交媒体" value="social" />
            <el-option label="短信" value="sms" />
          </el-select>
          <el-select v-model="filterTags" multiple collapse-tags placeholder="标签" style="width:200px">
            <el-option label="促销" value="promo" />
            <el-option label="新品" value="new" />
            <el-option label="节日" value="holiday" />
          </el-select>
        </el-col>
        <el-col :span="12" style="text-align:right;">
          <el-button-group>
            <el-button :plain="filterStatus !== 'all'" @click="setStatus('all')">全部</el-button>
            <el-button :plain="filterStatus !== 'running'" @click="setStatus('running')">进行中</el-button>
            <el-button :plain="filterStatus !== 'success'" @click="setStatus('success')">已结束</el-button>
          </el-button-group>
          <el-button type="primary" style="margin-left:10px" @click="openCreate">➕ 新建活动</el-button>
        </el-col>
      </el-row>
    </el-card>

    <el-card>
      <el-table :data="filteredList" style="width:100%">
        <el-table-column prop="name" label="名称" min-width="160" />
        <el-table-column prop="status" label="状态" width="120">
          <template #default="scope">
            <span :class="'status-badge status-' + scope.row.status">{{ statusText(scope.row.status) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="channels" label="渠道" width="150">
          <template #default="scope">
            <el-tag v-for="c in scope.row.channels" :key="c" size="small" style="margin-right:4px">{{ channelName(c) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始" width="120" />
        <el-table-column prop="endTime" label="结束" width="120" />
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button type="text" size="small" @click="viewCampaign(scope.row)">查看</el-button>
            <el-button type="text" size="small" @click="openEdit(scope.row)">编辑</el-button>
            <el-popconfirm title="确定删除?" @confirm="deleteCampaign(scope.row)">
              <el-button type="text" size="small" style="color:#f56c6c">删除</el-button>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑活动' : '新建活动'" width="600px">
      <el-form :model="form" label-width="80px" :rules="rules" ref="formRef">
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="渠道" prop="channels">
          <el-select v-model="form.channels" multiple style="width:100%">
            <el-option v-for="c in channelOptions" :key="c.value" :label="c.label" :value="c.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="时间" required>
          <el-date-picker v-model="form.startTime" type="date" placeholder="开始" style="width:45%;margin-right:10px" />
          <el-date-picker v-model="form.endTime" type="date" placeholder="结束" style="width:45%" />
        </el-form-item>
        <el-form-item label="附件">
          <el-upload v-model:file-list="form.attachments" :auto-upload="false" list-type="text">
            <el-button type="primary">上传文件</el-button>
          </el-upload>
        </el-form-item>
        <el-form-item label="描述">
          <el-input type="textarea" v-model="form.description" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveCampaign">保存</el-button>
      </template>
    </el-dialog>

    <el-drawer v-model="drawerVisible" title="活动详情" direction="rtl" size="40%">
      <template v-if="detail">
        <el-descriptions :title="detail.name" column="1" border style="margin-bottom:20px">
          <el-descriptions-item label="状态">
            <el-tag>{{ statusText(detail.status) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="渠道">
            <el-tag v-for="c in detail.channels" :key="c" style="margin-right:4px">{{ channelName(c) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="起止时间">{{ detail.startTime }} - {{ detail.endTime }}</el-descriptions-item>
        </el-descriptions>
        <el-row :gutter="20" style="margin-bottom:20px">
          <el-col :span="8">
            <el-card>
              <div class="stat-number">{{ detail.metrics.sent }}</div>
              <div class="stat-label">发送量</div>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card>
              <div class="stat-number">{{ detail.metrics.opens }}</div>
              <div class="stat-label">打开量</div>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card>
              <div class="stat-number">{{ detail.metrics.clicks }}</div>
              <div class="stat-label">点击量</div>
            </el-card>
          </el-col>
        </el-row>
        <el-steps :active="activeStep" finish-status="success" style="margin-bottom:20px">
          <el-step v-for="(s,i) in detail.steps" :key="i" :title="s" />
        </el-steps>
        <el-upload :file-list="detail.attachments" list-type="text" disabled />
        <p style="margin-top:20px">{{ detail.description }}</p>
      </template>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import campaignData from '../mock/campaignList.json'
import campaignDetails from '../mock/campaignDetail.json'

const campaigns = ref([])
const search = ref('')
const filterStatus = ref('all')
const filterChannel = ref('')
const filterTags = ref([])

const dialogVisible = ref(false)
const drawerVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const form = reactive({
  id: null,
  name: '',
  channels: [],
  startTime: '',
  endTime: '',
  attachments: [],
  description: ''
})

const rules = {
  name: [{ required: true, message: '请输入名称', trigger: 'blur' }],
  channels: [{ required: true, message: '请选择渠道', trigger: 'change' }]
}

const channelOptions = [
  { label: '邮件', value: 'email' },
  { label: '社交媒体', value: 'social' },
  { label: '短信', value: 'sms' }
]

const detail = ref(null)
const activeStep = ref(0)

onMounted(() => {
  campaigns.value = campaignData
})

const filteredList = computed(() => {
  let list = campaigns.value
  if (search.value) {
    list = list.filter(c => c.name.includes(search.value))
  }
  if (filterStatus.value !== 'all') {
    list = list.filter(c => c.status === filterStatus.value)
  }
  if (filterChannel.value) {
    list = list.filter(c => c.channels.includes(filterChannel.value))
  }
  return list
})

function statusText(val) {
  const map = { running: '进行中', success: '已结束', pending: '未开始' }
  return map[val] || val
}

function channelName(val) {
  const map = { email: '邮件', social: '社交媒体', sms: '短信' }
  return map[val] || val
}

function setStatus(st) {
  filterStatus.value = st
}

function openCreate() {
  isEdit.value = false
  Object.assign(form, { id: null, name: '', channels: [], startTime: '', endTime: '', attachments: [], description: '' })
  dialogVisible.value = true
}

function openEdit(row) {
  isEdit.value = true
  Object.assign(form, JSON.parse(JSON.stringify(row)), { attachments: [] })
  dialogVisible.value = true
}

function saveCampaign() {
  formRef.value.validate((valid) => {
    if (!valid) return
    if (isEdit.value) {
      const idx = campaigns.value.findIndex(c => c.id === form.id)
      if (idx !== -1) campaigns.value[idx] = { ...form }
    } else {
      const newId = Math.max(0, ...campaigns.value.map(c => c.id || 0)) + 1
      campaigns.value.push({ ...form, id: newId })
    }
    dialogVisible.value = false
    ElMessage.success('保存成功')
  })
}

function deleteCampaign(row) {
  campaigns.value = campaigns.value.filter(c => c.id !== row.id)
  ElMessage.success('已删除')
}

function viewCampaign(row) {
  detail.value = { ...row, ...campaignDetails.find(d => d.id === row.id) }
  activeStep.value = detail.value.steps ? detail.value.steps.length - 1 : 0
  drawerVisible.value = true
}
</script>
