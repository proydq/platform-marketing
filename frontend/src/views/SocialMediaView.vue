<script setup>
import { ref, onMounted } from 'vue'
import accountsJson from '../mock/socialAccounts.json'
import postsJson from '../mock/socialPosts.json'

const accounts = ref([])
const posts = ref([])
const dialogVisible = ref(false)
const newAccount = ref({ platform: '', account: '' })

onMounted(() => {
  accounts.value = accountsJson
  posts.value = postsJson
})

function addAccount() {
  accounts.value.push({ ...newAccount.value, status: '已绑定', bindTime: new Date().toISOString().slice(0,10) })
  newAccount.value = { platform: '', account: '' }
  dialogVisible.value = false
}

function removeAccount(row) {
  accounts.value = accounts.value.filter(a => a !== row)
}
</script>

<template>
  <div>
    <div class="action-buttons">
      <el-button type="primary" @click="dialogVisible = true"><span class="icon">➕</span>添加账号</el-button>
    </div>

    <el-card class="chart-container">
      <h3>已绑定账号</h3>
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

    <el-card class="chart-container">
      <h3>推文计划</h3>
      <el-table :data="posts" style="width:100%">
        <el-table-column prop="platform" label="平台" width="120" />
        <el-table-column prop="content" label="内容" />
        <el-table-column prop="status" label="状态" width="120">
          <template #default="scope">
            <span :class="'status-badge status-' + scope.row.status">{{ scope.row.status }}</span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" title="添加账号" width="400px">
      <el-form :model="newAccount" label-width="80px">
        <el-form-item label="平台">
          <el-select v-model="newAccount.platform" placeholder="选择平台">
            <el-option label="Facebook" value="Facebook" />
            <el-option label="Twitter" value="Twitter" />
          </el-select>
        </el-form-item>
        <el-form-item label="账号">
          <el-input v-model="newAccount.account" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="addAccount">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>
