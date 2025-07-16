<script setup>
import { ref } from 'vue'
import RichTextEditor from '../components/RichTextEditor.vue'
const form = ref({ title: '', group: '', content: '' })
const groups = ['全部客户', '潜在客户', 'VIP']
</script>

<template>
  <el-card>
    <h2>📄 邮件营销页面</h2>
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
      <el-form-item>
        <el-button type="primary">发送</el-button>
      </el-form-item>
    </el-form>
    <div class="chart-container" style="margin-top:20px;">
      <h3>邮件预览</h3>
      <div>{{ form.content || '在此预览邮件内容...' }}</div>
    </div>
  </el-card>
</template>
