<template>
  <div class="page-wrapper">
    <el-card class="task-card">
      <el-row class="action-buttons" justify="space-between" align="middle">
        <el-space>
          <el-button type="primary" @click="openCreate"> 新增任务 </el-button>
          <el-button type="success" @click="showPreview"> 预览数据 </el-button>
        </el-space>
      </el-row>
      <QuickCrawlForm />
      <div class="task-table-wrapper">
        <el-table
          :data="tasks"
          height="100%"
          style="width: 100%"
          stripe
          size="small"
        >
          <el-table-column
            prop="name"
            label="任务名称"
            min-width="160"
            show-overflow-tooltip
          />
          <el-table-column prop="platform" label="平台" min-width="120">
            <template #default="{ row }">
              {{
                Array.isArray(row.platform)
                  ? row.platform.join(", ")
                  : row.platform
              }}
            </template>
          </el-table-column>
          <el-table-column
            prop="status"
            label="状态"
            width="120"
            align="center"
          >
            <template #default="{ row }">
              <el-tag :type="tagType(row.status)" size="small">{{
                row.status
              }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column
            prop="progress"
            label="进度"
            width="120"
            align="center"
          >
            <template #default="{ row }">
              <ProgressRing :percentage="row.progress" />
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" min-width="160" />
          <el-table-column label="操作" width="160" align="center">
            <template #default="scope">
              <el-button type="text" @click="editRow(scope.row)"
                >编辑</el-button
              >
              <el-button
                type="text"
                style="color: #f56c6c"
                @click="removeRow(scope.row)"
                >删除</el-button
              >
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-card>

    <!-- 表单抽屉 -->
    <el-drawer v-model="formDrawer" title="新建抓取任务" size="40%">
      <el-form :model="form" label-width="90px">
        <el-form-item label="任务名称">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="平台选择">
          <el-select v-model="form.platform" multiple style="width: 100%">
            <el-option
              v-for="p in platforms"
              :key="p.value"
              :label="p.label"
              :value="p.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="抓取类型">
          <el-select v-model="form.type">
            <el-option label="客户信息" value="customer" />
            <el-option label="留言" value="comment" />
            <el-option label="文章" value="article" />
          </el-select>
        </el-form-item>
        <el-form-item label="执行周期">
          <el-radio-group v-model="form.cycle">
            <el-radio label="daily">每天</el-radio>
            <el-radio label="weekly">每周</el-radio>
            <el-radio label="once">一次性</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="抓取字段">
          <el-checkbox-group v-model="form.fields">
            <el-checkbox label="姓名" />
            <el-checkbox label="邮箱" />
            <el-checkbox label="手机号" />
            <el-checkbox label="公司" />
          </el-checkbox-group>
        </el-form-item>
        <el-form-item label="预计条数">
          <el-input-number v-model="form.amount" :min="1" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="formDrawer = false">取消</el-button>
        <el-button type="primary" @click="saveTask">保存</el-button>
      </template>
    </el-drawer>

    <!-- 预览弹窗 -->
    <el-dialog v-model="previewDialog" title="数据预览" width="60%">
      <el-table :data="previewData" max-height="400" style="width: 100%">
        <el-table-column prop="name" label="姓名" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="source" label="来源" />
        <el-table-column prop="time" label="抓取时间" />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import ProgressRing from "@/components/ProgressRing.vue";
import {
  getCustomerList,
  createCustomer,
  deleteCustomer,
} from "@/api/customerCollect";

const tasks = ref([]);
const previewData = ref([]);
const formDrawer = ref(false);
const previewDialog = ref(false);

const platforms = [
  { label: "LinkedIn", value: "linkedin" },
  { label: "Facebook", value: "facebook" },
];

const form = ref({
  name: "",
  platform: [],
  type: "customer",
  cycle: "once",
  fields: [],
  amount: 100,
});

const editing = ref(false);
const currentId = ref(null);

onMounted(loadData);

async function loadData() {
  const res = await getCustomerList();
  tasks.value = res.data?.rows || [];
}

function openCreate() {
  editing.value = false;
  currentId.value = null;
  form.value = {
    name: "",
    platform: [],
    type: "customer",
    cycle: "once",
    fields: [],
    amount: 100,
  };
  formDrawer.value = true;
}

function editRow(row) {
  editing.value = true;
  currentId.value = row.id;
  form.value = {
    name: row.name,
    platform: Array.isArray(row.platform)
      ? row.platform
      : String(row.platform).split(","),
    type: row.type || "customer",
    cycle: row.cycle || "once",
    fields: row.fields || [],
    amount: row.amount || 100,
  };
  formDrawer.value = true;
}

async function removeRow(row) {
  await deleteCustomer(row.id);
  loadData();
}

async function saveTask() {
  const payload = {
    name: form.value.name,
    platform: form.value.platform,
    type: form.value.type,
    cycle: form.value.cycle,
    fields: form.value.fields,
    amount: form.value.amount,
  };
  if (editing.value && currentId.value) {
    // 你可以实现 update 接口
  } else {
    await createCustomer(payload);
  }
  formDrawer.value = false;
  loadData();
}

function showPreview() {
  previewDialog.value = true;
}

function tagType(status) {
  if (status === "success") return "success";
  if (status === "running") return "warning";
  if (status === "error") return "danger";
  return "info";
}
</script>

<style scoped>
.page-wrapper {
  display: flex;
  flex-direction: column;
  height: 100vh;
  padding: 16px;
  gap: 16px;
  box-sizing: border-box;
  background-color: #f5f7fa;
}

.chart-container {
  border-radius: 12px;
}

.task-card {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  border-radius: 12px;
}

.task-card .el-card__body {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 16px;
}

.task-table-wrapper {
  flex: 1;
  overflow-y: auto;
}
</style>
