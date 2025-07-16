<script setup>
import { ref } from 'vue'

const form = ref({ keyword: '', tone: '', use: '' })
const result = ref('')
const dialogVisible = ref(false)

function generate() {
  result.value = form.value.keyword
    ? `根据 ${form.value.keyword} 生成的${form.value.tone}内容，适用于${form.value.use}`
    : ''
  dialogVisible.value = true
}
</script>

<template>
  <el-card>
    <h2>📄 AI内容生成</h2>
    <el-form :model="form" label-width="100px">
      <el-form-item label="关键词">
        <el-input v-model="form.keyword" placeholder="输入关键词" />
      </el-form-item>
      <el-form-item label="语调">
        <el-select v-model="form.tone" placeholder="选择语调" style="width:180px">
          <el-option label="正式" value="正式" />
          <el-option label="活泼" value="活泼" />
          <el-option label="简洁" value="简洁" />
        </el-select>
      </el-form-item>
      <el-form-item label="用途">
        <el-select v-model="form.use" placeholder="选择用途" style="width:180px">
          <el-option label="邮件" value="邮件" />
          <el-option label="社交媒体" value="社交媒体" />
          <el-option label="博客" value="博客" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="generate"><span class="icon">📝</span>生成</el-button>
      </el-form-item>
    </el-form>

    <el-card v-if="result" style="margin-top:20px;">
      <h4>生成结果</h4>
      <p>{{ result }}</p>
    </el-card>

    <el-dialog v-model="dialogVisible" title="编辑内容" width="600px">
      <el-input type="textarea" v-model="result" rows="8" />
      <template #footer>
        <el-button @click="dialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="dialogVisible = false">保存</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>
