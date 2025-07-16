<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import campaignData from '../mock/campaignList.json'

const campaigns = ref([])
const filter = ref('all')
const dialogVisible = ref(false)
const isEdit = ref(false)

const form = reactive({
  id: null,
  name: '',
  channels: [],
  startTime: '',
  endTime: '',
  description: ''
})

const channelOptions = [
  { label: '邮件', value: 'email' },
  { label: '社交媒体', value: 'social' },
  { label: '短信', value: 'sms' }
]

onMounted(() => {
  campaigns.value = campaignData
})

const filteredList = computed(() => {
  if (filter.value === 'all') return campaigns.value
  return campaigns.value.filter(c => c.status === filter.value)
})

function openCreate() {
  isEdit.value = false
  Object.assign(form, { id: null, name: '', channels: [], startTime: '', endTime: '', description: '' })
  dialogVisible.value = true
}

function openEdit(row) {
  isEdit.value = true
  Object.assign(form, row)
  dialogVisible.value = true
}

function saveCampaign() {
  if (!form.name) {
    ElMessage.warning('请输入活动名称')
    return
  }
  if (isEdit.value) {
    const idx = campaigns.value.findIndex(c => c.id === form.id)
    if (idx !== -1) campaigns.value[idx] = { ...form }
  } else {
    const newId = Math.max(0, ...campaigns.value.map(c => c.id || 0)) + 1
    campaigns.value.push({ ...form, id: newId })
  }
  dialogVisible.value = false
}

function deleteCampaign(row) {
  campaigns.value = campaigns.value.filter(c => c.id !== row.id)
}

function viewCampaign(row) {
  ElMessage.info(row.description || '无描述')
}

function setFilter(status) {
  filter.value = status
}
</script>

<template>
  <div>
    <el-card class="action-buttons" style="margin-bottom:20px;">
      <div style="display:flex;justify-content:space-between;align-items:center;">
        <h2>📄 营销活动中心</h2>
        <div>
          <el-button-group>
            <el-button :plain="filter !== 'all'" @click="setFilter('all')">全部</el-button>
            <el-button :plain="filter !== 'running'" @click="setFilter('running')">进行中</el-button>
            <el-button :plain="filter !== 'success'" @click="setFilter('success')">已结束</el-button>
          </el-button-group>
          <el-button type="primary" style="margin-left:10px;" @click="openCreate">创建活动</el-button>
        </div>
      </div>
    </el-card>

    <el-card>
      <el-table :data="filteredList" style="width:100%">
        <el-table-column prop="name" label="活动名称" />
        <el-table-column prop="channels" label="渠道" width="160">
          <template #default="scope">
            <el-tag v-for="ch in scope.row.channels" :key="ch" size="small" style="margin-right:4px;">{{ ch }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="120">
          <template #default="scope">
            <span :class="'status-badge status-' + scope.row.status">{{ scope.row.status }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="120" />
        <el-table-column prop="endTime" label="结束时间" width="120" />
        <el-table-column label="操作" width="180">
          <template #default="scope">
            <el-button type="text" size="small" @click="viewCampaign(scope.row)">查看</el-button>
            <el-button type="text" size="small" @click="openEdit(scope.row)">编辑</el-button>
            <el-button type="text" size="small" @click="deleteCampaign(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑活动' : '新建活动'">
      <el-form :model="form" label-width="80px">
        <el-form-item label="名称">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="渠道">
          <el-select v-model="form.channels" multiple style="width:100%">
            <el-option v-for="c in channelOptions" :key="c.value" :label="c.label" :value="c.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="开始时间">
          <el-date-picker v-model="form.startTime" type="date" style="width:100%" />
        </el-form-item>
        <el-form-item label="结束时间">
          <el-date-picker v-model="form.endTime" type="date" style="width:100%" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input type="textarea" v-model="form.description" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveCampaign">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>
