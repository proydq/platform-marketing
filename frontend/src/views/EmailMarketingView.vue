<script setup>
import { ref, onMounted } from 'vue'
import RichTextEditor from '../components/RichTextEditor.vue'
import campaignsJson from '../mock/emailCampaigns.json'

const campaigns = ref([])
const formVisible = ref(false)
const form = ref({ title: '', group: '', content: '' })
const groups = ['全部客户', '潜在客户', 'VIP']

onMounted(() => {
  campaigns.value = campaignsJson
})

function openCreate() {
  form.value = { title: '', group: '', content: '' }
  formVisible.value = true
}
</script>

<template>
  <div class="page-wrapper">
    <div class="action-buttons">
      <el-button type="primary" @click="openCreate"><span class="icon">➕</span>新建邮件</el-button>
    </div>

    <el-card class="chart-container">
      <el-table :data="campaigns" style="width:100%">
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="group" label="收件人组" />
        <el-table-column prop="status" label="状态" width="120">
          <template #default="scope">
            <span :class="'status-badge status-' + scope.row.status">{{ scope.row.status }}</span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="formVisible" title="邮件编辑" width="700px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="邮件标题">
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="收件人组">
          <el-select v-model="form.group" placeholder="选择组">
            <el-option v-for="g in groups" :key="g" :label="g" :value="g" />
          </el-select>
        </el-form-item>
        <el-form-item label="正文">
          <RichTextEditor v-model="form.content" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="formVisible = false">取消</el-button>
        <el-button type="primary" @click="formVisible = false">发送</el-button>
      </template>
    </el-dialog>
  </div>
</template>
