<template>
  <div class="page-wrapper">
    <div class="action-buttons">
      <el-input v-model="keyword" placeholder="输入关键词" style="width:200px" />
      <el-select v-model="styleOpt" placeholder="风格" style="width:120px">
        <el-option label="正式" value="正式" />
        <el-option label="活泼" value="活泼" />
        <el-option label="简洁" value="简洁" />
      </el-select>
      <el-select v-model="useOpt" placeholder="用途" style="width:120px">
        <el-option label="社交媒体" value="社交媒体" />
        <el-option label="邮件" value="邮件" />
        <el-option label="博客" value="博客" />
      </el-select>
      <el-button type="primary" @click="generateContent"><span class="icon">⚡</span>生成内容</el-button>
    </div>

    <el-card>
      <el-table :data="contents" style="width:100%">
        <el-table-column prop="title" label="标题" min-width="160" />
        <el-table-column prop="summary" label="摘要" />
        <el-table-column prop="style" label="风格" width="80" />
        <el-table-column prop="use" label="用途" width="100" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <span :class="['status-badge', statusClass(row.status)]">{{ row.status }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220">
          <template #default="{ row }">
            <el-button type="text" @click="viewDetail(row)">查看</el-button>
            <el-button type="text" @click="openEditor(true, row)">编辑</el-button>
            <el-button type="text" @click="publishRow(row)">发布</el-button>
            <el-button type="text" style="color:#f56c6c" @click="removeRow(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-drawer v-model="editorDrawer" :title="isEdit ? '编辑文案' : '新建文案'" size="40%" direction="rtl">
      <el-form :model="editorForm" label-width="90px" class="form-section">
        <el-form-item label="标题">
          <el-input v-model="editorForm.title" />
        </el-form-item>
        <el-form-item label="正文">
          <RichTextEditor v-model="editorForm.content" />
        </el-form-item>
        <el-form-item label="标签">
          <el-input v-model="editorForm.tags" placeholder="使用逗号分隔" />
        </el-form-item>
        <el-form-item label="用途">
          <el-select v-model="editorForm.use" placeholder="选择用途">
            <el-option label="社交媒体" value="社交媒体" />
            <el-option label="邮件" value="邮件" />
            <el-option label="博客" value="博客" />
          </el-select>
        </el-form-item>
        <el-form-item label="发布渠道">
          <el-select v-model="editorForm.channel" placeholder="选择渠道">
            <el-option label="Facebook" value="Facebook" />
            <el-option label="Email" value="Email" />
            <el-option label="Twitter" value="Twitter" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editorDrawer = false">取消</el-button>
        <el-button @click="saveDraft">保存为草稿</el-button>
        <el-button type="primary" @click="publishNow">立即发布</el-button>
      </template>
    </el-drawer>

    <el-drawer v-model="detailDrawer" title="文案详情" size="40%" direction="rtl">
      <h3>{{ currentDetail.title }}</h3>
      <el-descriptions :column="1" style="margin:10px 0">
        <el-descriptions-item label="风格">{{ currentDetail.style }}</el-descriptions-item>
        <el-descriptions-item label="用途">{{ currentDetail.use }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <span :class="['status-badge', statusClass(currentDetail.status)]">{{ currentDetail.status }}</span>
        </el-descriptions-item>
      </el-descriptions>
      <div style="max-height:300px;overflow:auto;" v-html="currentDetail.content"></div>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import RichTextEditor from '../components/RichTextEditor.vue'
import data from '../mock/generatedContent.json'

const keyword = ref('')
const styleOpt = ref('')
const useOpt = ref('')

const contents = ref([])

const editorDrawer = ref(false)
const detailDrawer = ref(false)
const isEdit = ref(false)

const editorForm = ref({
  id: null,
  title: '',
  content: '',
  tags: '',
  use: '',
  channel: '',
  style: '',
  status: '草稿'
})

const currentDetail = ref({})

onMounted(() => {
  contents.value = data
})

function generateContent() {
  if (!keyword.value) {
    ElMessage.warning('请输入关键词')
    return
  }
  const baseId = contents.value.length ? Math.max(...contents.value.map(c => c.id)) + 1 : 1
  for (let i = 0; i < 3; i++) {
    const text = `${keyword.value} 的${styleOpt.value || '默认'}${useOpt.value || ''}文案示例${i + 1}`
    contents.value.push({
      id: baseId + i,
      title: `${keyword.value} 文案${i + 1}`,
      summary: text.slice(0, 20),
      style: styleOpt.value,
      use: useOpt.value,
      status: '未发布',
      content: `<p>${text}</p>`
    })
  }
  ElMessage.success('生成完成')
}

function statusClass(status) {
  if (status === '已发布') return 'status-success'
  if (status === '草稿') return 'status-pending'
  if (status === '未发布') return 'status-paused'
  if (status === '失败') return 'status-error'
  return ''
}

function openEditor(edit, row) {
  isEdit.value = edit
  if (edit && row) {
    editorForm.value = { ...row }
  } else {
    editorForm.value = { id: null, title: '', content: '', tags: '', use: '', channel: '', style: '', status: '草稿' }
  }
  editorDrawer.value = true
}

function saveDraft() {
  if (isEdit.value && editorForm.value.id) {
    const idx = contents.value.findIndex(c => c.id === editorForm.value.id)
    contents.value.splice(idx, 1, { ...editorForm.value, summary: editorForm.value.content.slice(0, 20), status: '草稿' })
  } else {
    const id = contents.value.length ? Math.max(...contents.value.map(c => c.id)) + 1 : 1
    contents.value.push({ ...editorForm.value, id, summary: editorForm.value.content.slice(0, 20), status: '草稿' })
  }
  editorDrawer.value = false
  ElMessage.success('已保存草稿')
}

function publishNow() {
  if (isEdit.value && editorForm.value.id) {
    const idx = contents.value.findIndex(c => c.id === editorForm.value.id)
    contents.value.splice(idx, 1, { ...editorForm.value, summary: editorForm.value.content.slice(0, 20), status: '已发布' })
  } else {
    const id = contents.value.length ? Math.max(...contents.value.map(c => c.id)) + 1 : 1
    contents.value.push({ ...editorForm.value, id, summary: editorForm.value.content.slice(0, 20), status: '已发布' })
  }
  editorDrawer.value = false
  ElMessage.success('已发布')
}

function publishRow(row) {
  row.status = '已发布'
  ElMessage.success('已发布')
}

function removeRow(row) {
  ElMessageBox.confirm('确定删除该文案吗?', '提示', { type: 'warning' })
    .then(() => {
      contents.value = contents.value.filter(c => c.id !== row.id)
      ElMessage.success('已删除')
    })
    .catch(() => {})
}

function viewDetail(row) {
  currentDetail.value = { ...row }
  detailDrawer.value = true
}
</script>
