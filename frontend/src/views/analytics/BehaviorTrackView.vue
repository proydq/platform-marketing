<script setup>
import { ref, onMounted, computed } from 'vue'
import logs from '@/mock/behaviorLog.json'

const list = ref([])
const actionFilter = ref('')
const drawer = ref(false)
const current = ref({})

onMounted(() => { list.value = logs })

const filtered = computed(() => {
  if (!actionFilter.value) return list.value
  return list.value.filter(i => i.action.includes(actionFilter.value))
})

function view(row) { current.value = row; drawer.value = true }
</script>

<template>
  <div class="page-wrapper">
  <el-card>
    <h2>📄 行为追踪</h2>
    <div class="action-buttons">
      <el-input v-model="actionFilter" placeholder="按操作筛选" style="width:200px" />
    </div>
    <el-table :data="filtered" style="width:100%">
      <el-table-column prop="customerId" label="客户ID" width="100" />
      <el-table-column prop="action" label="操作" width="120" />
      <el-table-column prop="time" label="时间" width="180" />
      <el-table-column prop="ip" label="IP" width="150" />
      <el-table-column prop="page" label="来源页面" />
      <el-table-column width="80">
        <template #default="scope">
          <el-button type="text" @click="view(scope.row)">查看</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>

  <el-drawer v-model="drawer" title="行为详情" size="30%">
    <p>客户ID：{{ current.customerId }}</p>
    <p>操作：{{ current.action }}</p>
    <p>时间：{{ current.time }}</p>
    <p>IP：{{ current.ip }}</p>
    <p>来源页面：{{ current.page }}</p>
  </el-drawer>
</div>

</template>
