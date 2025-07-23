<template>
  <div class="page-wrapper">
    <div class="toolbar mb-4 flex gap-2 items-center">
      <el-input v-model="search" placeholder="搜索活动" clearable style="width: 200px" @keyup.enter="loadData" />
      <el-button type="primary" icon="Search" @click="loadData">搜索</el-button>
      <el-button type="primary" icon="Plus" @click="openEdit(false)">新增活动</el-button>
    </div>
    <el-table :data="campaigns" border size="small" style="width:100%">
      <el-table-column prop="name" label="名称" />
      <el-table-column label="渠道">
        <template #default="{ row }">
          <el-tag v-for="c in row.channels" :key="c" size="small" class="mr-1">{{ c }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="时间" width="220">
        <template #default="{ row }">
          {{ row.startTime }} - {{ row.endTime }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="120" />
      <el-table-column label="操作" width="260">
        <template #default="{ row }">
          <el-button size="small" @click="openDetail(row.id)">查看</el-button>
          <el-button size="small" type="primary" @click="openEdit(true, row)">编辑</el-button>
          <el-button size="small" type="success" @click="publish(row.id)">发布</el-button>
          <el-button size="small" @click="togglePause(row)">{{ row.status === 'paused' ? '启动' : '暂停' }}</el-button>
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
        @current-change="loadData"
        @size-change="loadData"
      />
    </div>

    <el-drawer v-model="editDrawer" :title="editMode ? '编辑活动' : '新增活动'" direction="rtl" size="40%">
      <el-form :model="form" label-width="90px">
        <el-form-item label="名称">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="渠道">
          <el-select v-model="form.channels" multiple style="width:100%">
            <el-option label="Email" value="email" />
            <el-option label="SMS" value="sms" />
            <el-option label="Social" value="social" />
          </el-select>
        </el-form-item>
        <el-form-item label="开始时间">
          <el-date-picker v-model="form.startTime" type="datetime" style="width:100%" />
        </el-form-item>
        <el-form-item label="结束时间">
          <el-date-picker v-model="form.endTime" type="datetime" style="width:100%" />
        </el-form-item>
        <el-form-item label="内容">
          <div v-for="(c,i) in form.contents" :key="i" style="margin-bottom:4px">
            <el-input v-model="form.contents[i]" type="textarea" />
          </div>
          <el-button size="small" @click="form.contents.push('')">增加内容</el-button>
        </el-form-item>
        <el-form-item label="周期">
          <el-select v-model="form.cycle" style="width:100%">
            <el-option label="一次" value="once" />
            <el-option label="每天" value="daily" />
            <el-option label="每周" value="weekly" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDrawer = false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-drawer>

    <el-drawer v-model="detailDrawer" title="活动详情" direction="rtl" size="40%">
      <template v-if="current">
        <el-descriptions :title="current.name" column="1" border style="margin-bottom:20px">
          <el-descriptions-item label="状态">{{ current.status }}</el-descriptions-item>
          <el-descriptions-item label="渠道">
            <el-tag v-for="c in current.channels" :key="c" size="small" class="mr-1">{{ c }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="时间">{{ current.startTime }} - {{ current.endTime }}</el-descriptions-item>
        </el-descriptions>
        <div v-for="(c, i) in current.contents" :key="i" style="margin-bottom:10px" v-html="c" />
      </template>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getCampaignList,
  getCampaignDetail,
  createCampaign,
  updateCampaign,
  deleteCampaign,
  publishCampaign,
  toggleCampaignStatus
} from '@/api/campaign'

const campaigns = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
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

async function loadData() {
  const res = await getCampaignList({ page: page.value - 1, size: size.value, keyword: search.value })
  campaigns.value = res.data?.content || []
  total.value = res.data?.totalElements || res.data?.total || 0
}
onMounted(loadData)

function openEdit(edit, row) {
  editMode.value = edit
  if (edit && row) {
    Object.assign(form, row)
    if (!form.contents || !form.contents.length) form.contents = ['']
  } else {
    Object.assign(form, { id: null, name: '', channels: [], startTime: '', endTime: '', contents: [''], cycle: 'once' })
  }
  editDrawer.value = true
}

async function save() {
  if (editMode.value) {
    await updateCampaign(form.id, { ...form })
  } else {
    await createCampaign({ ...form })
  }
  ElMessage.success('保存成功')
  editDrawer.value = false
  loadData()
}

async function openDetail(id) {
  const res = await getCampaignDetail(id)
  current.value = res.data
  if (!current.value.contents) current.value.contents = []
  detailDrawer.value = true
}

async function publish(id) {
  await publishCampaign({ id })
  ElMessage.success('已发布')
  loadData()
}

async function togglePause(row) {
  await toggleCampaignStatus({ id: row.id })
  ElMessage.success('操作成功')
  loadData()
}

async function remove(id) {
  await ElMessageBox.confirm('确认删除该活动吗？', '提示', { type: 'warning' })
  await deleteCampaign(id)
  ElMessage.success('删除成功')
  loadData()
}
</script>

<style scoped>
.page-wrapper {
  padding: 20px;
}
</style>

