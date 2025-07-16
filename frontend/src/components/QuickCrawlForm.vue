<script setup>
import { ref } from 'vue'
import preview from '../mock/previewData.json'

const form = ref({
  website: '',
  keywords: '',
  region: [],
  count: 100,
  frequency: 'immediate',
  quality: 'high'
})

const websites = [
  { label: 'LinkedIn', value: 'linkedin' },
  { label: 'Google Maps', value: 'googlemaps' },
  { label: 'Yellow Pages', value: 'yellowpages' },
  { label: 'Facebook', value: 'facebook' }
]

function submit() {
  // mock submit
  console.log('start crawl', form.value)
}
</script>

<template>
  <el-form :model="form" label-width="80px">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-form-item label="目标网站">
          <el-select v-model="form.website" placeholder="选择目标网站" style="width:100%">
            <el-option v-for="w in websites" :key="w.value" :label="w.label" :value="w.value" />
          </el-select>
        </el-form-item>
      </el-col>
      <el-col :span="8">
        <el-form-item label="关键词">
          <el-input v-model="form.keywords" placeholder="输入关键词" />
        </el-form-item>
      </el-col>
      <el-col :span="8">
        <el-form-item label="目标地区">
          <el-cascader v-model="form.region" :options="[]" placeholder="选择地区" style="width:100%" />
        </el-form-item>
      </el-col>
    </el-row>
    <el-row :gutter="20">
      <el-col :span="6">
        <el-form-item label="数量">
          <el-input-number v-model="form.count" :min="1" :max="10000" style="width:100%" />
        </el-form-item>
      </el-col>
      <el-col :span="6">
        <el-form-item label="频率">
          <el-select v-model="form.frequency" style="width:100%">
            <el-option label="立即执行" value="immediate" />
            <el-option label="每小时" value="hourly" />
            <el-option label="每天" value="daily" />
            <el-option label="每周" value="weekly" />
          </el-select>
        </el-form-item>
      </el-col>
      <el-col :span="6">
        <el-form-item label="质量">
          <el-select v-model="form.quality" style="width:100%">
            <el-option label="高质量" value="high" />
            <el-option label="中等质量" value="medium" />
            <el-option label="全部数据" value="all" />
          </el-select>
        </el-form-item>
      </el-col>
      <el-col :span="6">
        <el-form-item label=" ">
          <el-button type="primary" style="width:100%;margin-top:2px" @click="submit">
            <span class="icon">🚀</span>开始抓取
          </el-button>
        </el-form-item>
      </el-col>
    </el-row>
  </el-form>
</template>
