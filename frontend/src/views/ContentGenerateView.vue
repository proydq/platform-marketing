<script setup>
import { ref } from 'vue'

const form = ref({ keyword: '', style: '' })
const result = ref('')

function generate() {
  result.value = form.value.keyword
    ? `根据 ${form.value.keyword} 生成的${form.value.style || ''}内容...`
    : ''
}
</script>

<template>
  <el-card>
    <h2>📄 内容生成页面</h2>
    <el-form class="action-buttons" :inline="true" :model="form">
      <el-form-item>
        <el-input v-model="form.keyword" placeholder="输入关键词" />
      </el-form-item>
      <el-form-item>
        <el-select v-model="form.style" placeholder="选择风格" style="width:160px">
          <el-option label="正式" value="正式" />
          <el-option label="活泼" value="活泼" />
          <el-option label="简洁" value="简洁" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="generate">生成</el-button>
      </el-form-item>
    </el-form>
    <el-card v-if="result" style="margin-top:20px;">{{ result }}</el-card>
  </el-card>
</template>
