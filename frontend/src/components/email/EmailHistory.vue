<template>
  <div class="email-history">
    <div class="history-header">
      <h3>📧 邮件发送记录</h3>
      <el-button @click="refreshRecords" :loading="loading" size="small">
        <el-icon><Refresh /></el-icon>
        刷新
      </el-button>
    </div>
    
    <el-table :data="records" style="width:100%" v-loading="loading">
      <el-table-column prop="subject" label="邮件主题" min-width="200" />
      <el-table-column prop="recipients" label="收件人" width="120">
        <template #default="{ row }">
          <el-tag type="info" size="small">{{ row.recipients?.length || 0 }}人</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="sentTime" label="发送时间" width="180">
        <template #default="{ row }">
          {{ formatTime(row.sentTime) }}
        </template>
      </el-table-column>
      <el-table-column label="状态" width="120">
        <template #default="{ row }">
          <el-tag type="success" size="small">已发送</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button type="text" size="small" @click="viewDetail(row)">
            <el-icon><View /></el-icon>
            详情
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <div class="pagination" v-if="total > 0">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
    
    <!-- 详情对话框 -->
    <el-dialog v-model="detailVisible" title="邮件详情" width="600px">
      <div v-if="currentRecord">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="邮件主题">{{ currentRecord.subject }}</el-descriptions-item>
          <el-descriptions-item label="发送时间">{{ formatTime(currentRecord.sentTime) }}</el-descriptions-item>
          <el-descriptions-item label="收件人数量">{{ currentRecord.recipients?.length || 0 }}</el-descriptions-item>
          <el-descriptions-item label="收件人列表">
            <el-tag v-for="email in currentRecord.recipients" :key="email" size="small" style="margin-right: 5px; margin-bottom: 5px;">
              {{ email }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="邮件内容">
            <div v-html="currentRecord.content" style="max-height: 300px; overflow-y: auto;"></div>
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getEmailSendRecords } from '@/api/email'
import { Refresh, View } from '@element-plus/icons-vue'

const records = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const detailVisible = ref(false)
const currentRecord = ref(null)

const loadRecords = async () => {
  loading.value = true
  try {
    const res = await getEmailSendRecords(currentPage.value, pageSize.value)
    if (res.data) {
      records.value = res.data.content || []
      total.value = res.data.totalElements || 0
    }
  } catch (error) {
    console.error('获取邮件记录失败:', error)
    records.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

const refreshRecords = () => {
  loadRecords()
}

const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  loadRecords()
}

const handleCurrentChange = (page) => {
  currentPage.value = page
  loadRecords()
}

const viewDetail = (record) => {
  currentRecord.value = record
  detailVisible.value = true
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleString('zh-CN')
}

onMounted(() => {
  loadRecords()
})
</script>

<style scoped>
.email-history {
  padding: 20px;
}

.history-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.history-header h3 {
  margin: 0;
  color: #333;
  font-size: 18px;
}

.pagination {
  margin-top: 20px;
  text-align: center;
}
</style>
