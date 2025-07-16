<script setup>
import { ref } from 'vue'

const tab = ref('line')
</script>

<template>
  <div class="page-wrapper">
  <el-card>
    <h2>📄 报表分析</h2>
    <el-tabs v-model="tab">
      <el-tab-pane label="折线图" name="line">
        <div style="height:300px;display:flex;align-items:center;justify-content:center;color:#999;">📈 折线图占位</div>
      </el-tab-pane>
      <el-tab-pane label="柱状图" name="bar">
        <div style="height:300px;display:flex;align-items:center;justify-content:center;color:#999;">📊 柱状图占位</div>
      </el-tab-pane>
      <el-tab-pane label="饼图" name="pie">
        <div style="height:300px;display:flex;align-items:center;justify-content:center;color:#999;">🍰 饼图占位</div>
      </el-tab-pane>
    </el-tabs>
  </el-card>
</div>
</template>
