<script setup>
import { ref, onMounted } from 'vue'
import previewDataJson from '../mock/previewData.json'
import tasksJson from '../mock/crawlTasks.json'

const formVisible = ref(false)
const previewDrawer = ref(false)
const previewData = ref([])
const tasks = ref([])
const currentPreview = ref([])

onMounted(() => {
  previewData.value = previewDataJson
  tasks.value = tasksJson
})

function openCreate() {
  formVisible.value = true
}

function showPreview() {
  currentPreview.value = previewData.value
  previewDrawer.value = true
}
</script>

<template>
  <div class="page-wrapper">
    <el-row class="action-buttons" justify="space-between" align="middle">
      <el-space>
        <el-button type="primary" @click="openCreate"><span class="icon">➕</span>新建任务</el-button>
        <el-button type="success" @click="showPreview"><span class="icon">📄</span>预览数据</el-button>
      </el-space>
    </el-row>

    <el-card class="chart-container">
      <h3 style="margin-bottom:20px;">快速抓取配置</h3>
      <QuickCrawlForm />
    </el-card>

    <el-card class="chart-container">
      <h3 style="margin-bottom:20px;">任务列表</h3>
      <el-table :data="tasks" style="width:100%">
        <el-table-column prop="name" label="任务名称" min-width="160" show-overflow-tooltip />
        <el-table-column prop="website" label="平台" width="120" />
        <el-table-column prop="status" label="状态" width="120" align="center">
          <template #default="scope">
            <span :class="'status-badge status-' + scope.row.status">{{ scope.row.status }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="progress" label="进度" width="120" align="center">
          <template #default="scope">
            <div class="progress-ring">{{ scope.row.progress }}%</div>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="160" />
      </el-table>
    </el-card>

    <el-dialog v-model="formVisible" title="新建抓取任务" width="600px">
      <QuickCrawlForm />
    </el-dialog>

    <el-drawer v-model="previewDrawer" title="抓取结果预览" size="50%">
      <el-table :data="currentPreview" max-height="400" style="width:100%">
        <el-table-column prop="companyName" label="公司" />
        <el-table-column prop="contactPerson" label="联系人" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="phone" label="电话" />
        <el-table-column prop="industry" label="行业" />
        <el-table-column prop="location" label="位置" />
      </el-table>
    </el-drawer>
  </div>
</template>
