<script setup>
import { ref, onMounted } from 'vue'
import campaigns from '../mock/campaignList.json'
import CampaignStatusTag from '../components/CampaignStatusTag.vue'
const list = ref([])
onMounted(() => { list.value = campaigns })
</script>
<template>
  <el-card>
    <h2>📄 营销活动中心</h2>
    <el-table :data="list" style="width:100%">
      <el-table-column prop="name" label="活动名称" />
      <el-table-column prop="status" label="状态">
        <template #default="scope">
          <CampaignStatusTag :status="scope.row.status" />
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>
