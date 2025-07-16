<script setup>
import { ref } from 'vue'

const siteForm = ref({ siteName: '海外营销系统', domain: 'example.com' })
const securityForm = ref({ passwordLength: 8, twoFactor: false })
</script>

<template>
  <el-card>
    <h2>📄 系统设置页面</h2>
    <el-tabs tab-position="left">
      <el-tab-pane label="站点设置">
        <el-form :model="siteForm" label-width="80px">
          <el-form-item label="站点名称">
            <el-input v-model="siteForm.siteName" />
          </el-form-item>
          <el-form-item label="域名">
            <el-input v-model="siteForm.domain" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary">保存</el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>
      <el-tab-pane label="安全设置">
        <el-form :model="securityForm" label-width="80px">
          <el-form-item label="密码长度">
            <el-input-number v-model="securityForm.passwordLength" :min="6" />
          </el-form-item>
          <el-form-item label="两步验证">
            <el-switch v-model="securityForm.twoFactor" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary">保存</el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>
    </el-tabs>
  </el-card>
</template>

