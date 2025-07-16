<template>
  <div class="page-wrapper">
    <div class="action-buttons">
      <el-select v-model="filterPlatform" placeholder="平台" style="width:120px">
        <el-option label="全部" value="" />
        <el-option v-for="p in platforms" :key="p" :label="p" :value="p" />
      </el-select>
      <el-input v-model="keyword" placeholder="搜索账号" clearable style="width:200px" />
      <el-button type="primary" @click="toggleAdd"><span class="icon">➕</span>添加账号</el-button>
    </div>

    <el-collapse v-model="addSection" style="margin-bottom:20px;">
      <el-collapse-item name="add">
        <template #title>新增账号</template>
        <el-form :model="addForm" label-width="90px" class="form-section" style="max-width:600px;margin-top:10px;">
          <el-form-item label="平台类型">
            <el-select v-model="addForm.platform" placeholder="选择平台">
              <el-option label="Facebook" value="Facebook" />
              <el-option label="LinkedIn" value="LinkedIn" />
              <el-option label="Twitter" value="Twitter" />
            </el-select>
          </el-form-item>
          <el-form-item label="账号名">
            <el-input v-model="addForm.name" />
          </el-form-item>
          <el-form-item label="AccessToken">
            <el-input v-model="addForm.token" />
          </el-form-item>
          <el-form-item label="备注">
            <el-input v-model="addForm.note" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="addAccount">添加</el-button>
          </el-form-item>
        </el-form>
      </el-collapse-item>
    </el-collapse>

    <el-row :gutter="20">
      <el-col v-for="acc in filteredAccounts" :key="acc.id" :xs="24" :sm="12" :md="8" style="margin-bottom:20px;">
        <el-card>
          <template #header>
            <div style="display:flex;justify-content:space-between;align-items:center;">
              <span>{{ acc.platform }}</span>
              <div>
                <el-tooltip content="编辑">
                  <el-button text size="small" @click="openDrawer(acc)"><el-icon><Edit /></el-icon></el-button>
                </el-tooltip>
                <el-tooltip content="解绑">
                  <el-button text size="small" style="color:#f56c6c" @click="unbind(acc)"><el-icon><Delete /></el-icon></el-button>
                </el-tooltip>
                <el-tooltip content="推送日志">
                  <el-button text size="small" @click="openLogs(acc)"><el-icon><Document /></el-icon></el-button>
                </el-tooltip>
              </div>
            </div>
          </template>
          <div style="margin-bottom:10px;">
            <span :class="['status-badge', acc.status === '已绑定' ? 'status-success' : 'status-error']">{{ acc.status }}</span>
          </div>
          <p>{{ acc.name }}</p>
          <p style="color:#999;font-size:12px;">绑定于 {{ acc.bindTime }}</p>
        </el-card>
      </el-col>
    </el-row>

    <el-drawer v-model="drawerVisible" title="编辑账号" size="30%">
      <el-form :model="editForm" label-width="90px" class="form-section">
        <el-form-item label="平台类型">
          <el-select v-model="editForm.platform">
            <el-option label="Facebook" value="Facebook" />
            <el-option label="LinkedIn" value="LinkedIn" />
            <el-option label="Twitter" value="Twitter" />
          </el-select>
        </el-form-item>
        <el-form-item label="账号名">
          <el-input v-model="editForm.name" />
        </el-form-item>
        <el-form-item label="AccessToken">
          <el-input v-model="editForm.token" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="editForm.note" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="editForm.statusSwitch" active-text="已绑定" inactive-text="已断开" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="saveEdit">保存</el-button>
        </el-form-item>
      </el-form>
    </el-drawer>

    <el-dialog v-model="logDialogVisible" title="推送日志" width="600px">
      <el-table :data="logsForAccount" style="width:100%">
        <el-table-column prop="time" label="时间" width="160" />
        <el-table-column prop="content" label="内容" />
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <span :class="['status-badge', row.status === '成功' ? 'status-success' : 'status-error']">{{ row.status }}</span>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Edit, Delete, Document } from '@element-plus/icons-vue'
import accountsData from '../mock/socialAccounts.json'
import logsData from '../mock/socialPushLogs.json'

const accounts = ref([])
const logs = ref([])
const filterPlatform = ref('')
const keyword = ref('')
const addSection = ref([])
const addForm = ref({ platform: '', name: '', token: '', note: '' })
const drawerVisible = ref(false)
const editForm = ref({ id: null, platform: '', name: '', token: '', note: '', status: '已绑定', bindTime: '', statusSwitch: true })
const logDialogVisible = ref(false)
const currentAccountId = ref(null)

onMounted(() => {
  accounts.value = accountsData.map(a => ({ ...a, statusSwitch: a.status === '已绑定' }))
  logs.value = logsData
})

const platforms = computed(() => {
  const set = new Set(accounts.value.map(a => a.platform))
  return Array.from(set)
})

const filteredAccounts = computed(() => {
  let list = accounts.value
  if (filterPlatform.value) list = list.filter(a => a.platform === filterPlatform.value)
  if (keyword.value) list = list.filter(a => a.name.includes(keyword.value))
  return list
})

function toggleAdd() {
  addSection.value = addSection.value.length ? [] : ['add']
}

function addAccount() {
  const id = accounts.value.length ? Math.max(...accounts.value.map(a => a.id)) + 1 : 1
  accounts.value.push({ ...addForm.value, id, status: '已绑定', bindTime: new Date().toISOString().split('T')[0], statusSwitch: true })
  addForm.value = { platform: '', name: '', token: '', note: '' }
  addSection.value = []
  ElMessage.success('账号已添加')
}

function openDrawer(acc) {
  editForm.value = { ...acc, statusSwitch: acc.status === '已绑定' }
  drawerVisible.value = true
}

function saveEdit() {
  const idx = accounts.value.findIndex(a => a.id === editForm.value.id)
  editForm.value.status = editForm.value.statusSwitch ? '已绑定' : '已断开'
  accounts.value.splice(idx, 1, { ...editForm.value })
  drawerVisible.value = false
  ElMessage.success('账号信息已更新')
}

function unbind(acc) {
  ElMessageBox.confirm('确定解绑该账号吗?', '提示', { type: 'warning' })
    .then(() => {
      accounts.value = accounts.value.filter(a => a.id !== acc.id)
      ElMessage.success('已解绑')
    })
    .catch(() => {})
}

function openLogs(acc) {
  currentAccountId.value = acc.id
  logDialogVisible.value = true
}

const logsForAccount = computed(() => logs.value.filter(l => l.accountId === currentAccountId.value))
</script>
