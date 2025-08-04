<script setup>
import { ref, onMounted } from "vue";
import { useI18n } from "vue-i18n";
import StatCard from "@/components/StatCard.vue";
import LineChart from "@/components/charts/LineChart.vue";

import {
  getDashboardStats,
  getSendTrend,
  getCustomerTrend,
  getRecentTasks,
  getTaskDetail,
  getKeywordSearchCount,
} from "@/api/dashboard";

const stats = ref({});
const tasks = ref([]);
const drawerVisible = ref(false);
const currentTask = ref({});
const chartTab = ref("send");
const sendTrend = ref([]);
const customerTrend = ref([]);

const keyword = ref("");
const keywordStat = ref(null);
const { t } = useI18n();

onMounted(() => {
  getDashboardStats().then((res) => (stats.value = res));
  getSendTrend().then((res) => (sendTrend.value = res));
  getCustomerTrend().then((res) => (customerTrend.value = res));
  getRecentTasks().then((res) => (tasks.value = res));
});

function viewTask(row) {
  getTaskDetail(row.id).then((res) => {
    currentTask.value = res;
    drawerVisible.value = true;
  });
}

function searchKeyword() {
  if (!keyword.value) return;
  getKeywordSearchCount(keyword.value).then((res) => {
    keywordStat.value = res.data;
  });
}
</script>

<template>
  <div class="page-wrapper">
    <div class="dashboard-cards">
      <StatCard title="总客户数" :value="stats.totalCustomers" />
      <StatCard title="今日邮件发送" :value="stats.emailsSent" />
      <StatCard title="邮件打开率" :value="stats.openRate + '%'" />
      <StatCard title="运行中任务" :value="stats.runningTasks" />
      <StatCard
        v-if="keywordStat"
        :title="`${t('dashboard.keywordSearchCount')} - ${keywordStat.keyword}`"
        :value="keywordStat.count"
      />
    </div>

    <div class="keyword-search">
      <el-input
        v-model="keyword"
        :placeholder="t('dashboard.keyword')"
        class="keyword-input"
      />
      <el-button type="primary" @click="searchKeyword">
        {{ t('dashboard.search') }}
      </el-button>
    </div>

    <el-card class="chart-container">
      <el-tabs v-model="chartTab">
        <el-tab-pane label="邮件发送量" name="send">
          <LineChart :data="sendTrend" />
        </el-tab-pane>
        <el-tab-pane label="客户增长" name="customer">
          <LineChart :data="customerTrend" />
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <el-card class="task-list">
      <h3 style="margin-bottom: 20px">最近任务</h3>
      <el-table :data="tasks" style="width: 100%">
        <el-table-column
          prop="name"
          label="任务名称"
          min-width="150"
          show-overflow-tooltip
        />
        <el-table-column prop="status" label="状态" width="120" align="center">
          <template #default="scope">
            <span :class="'status-badge status-' + scope.row.status">{{
              scope.row.status
            }}</span>
          </template>
        </el-table-column>
        <el-table-column
          prop="progress"
          label="进度"
          width="120"
          align="center"
        >
          <template #default="scope">
            <div class="progress-ring">{{ scope.row.progress }}%</div>
          </template>
        </el-table-column>
        <el-table-column>
          <template #default="scope">
            <el-button type="text" @click="viewTask(scope.row)">查看</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-drawer v-model="drawerVisible" title="任务详情" size="30%">
      <p>名称：{{ currentTask.name }}</p>
      <p>状态：{{ currentTask.status }}</p>
      <el-steps
        :active="
          currentTask.progress === 100 ? 3 : currentTask.progress > 0 ? 2 : 1
        "
        finish-status="success"
      >
        <el-step title="创建" />
        <el-step title="执行" />
        <el-step title="完成" />
      </el-steps>
    </el-drawer>
  </div>
</template>

<style scoped>
.keyword-search {
  margin: 20px 0;
  display: flex;
  gap: 10px;
  max-width: 400px;
}
.keyword-input {
  flex: 1;
}
</style>
