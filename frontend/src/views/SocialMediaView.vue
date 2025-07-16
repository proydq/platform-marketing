<script setup>
import { ref, onMounted } from 'vue'
import accountsData from '../mock/socialAccounts.json'

const accounts = ref([])

onMounted(() => {
  accounts.value = accountsData
})

function addAccount() {
  accounts.value.push({ platform: '', account: '', status: '未绑定', bindTime: '' })
}

function removeAccount(row) {
  accounts.value = accounts.value.filter(a => a !== row)
}
</script>

<template>
  <el-card>
    <h2>📄 社交媒体页面</h2>
    <div class="action-buttons">
      <el-button type="primary" @click="addAccount">添加账号</el-button>
    </div>
    <el-table :data="accounts" style="width:100%">
      <el-table-column prop="platform" label="平台" width="120" />
      <el-table-column prop="account" label="账号" width="180" />
      <el-table-column prop="status" label="状态" width="100" />
      <el-table-column prop="bindTime" label="绑定时间" width="180" />
      <el-table-column label="操作" width="100">
        <template #default="{ row }">
          <el-button type="text" @click="removeAccount(row)">解绑</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>

