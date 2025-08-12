<template>
  <div class="crawl-container">
    <div class="page-header">
      <div class="header-content">
        <div class="header-text">
          <h1>客户抓取</h1>
          <p>管理并执行客户抓取任务</p>
        </div>
        <div class="header-actions">
          <el-button type="primary" size="large" @click="openCreate">
            <el-icon><Plus /></el-icon>
            新增任务
          </el-button>
          <el-button size="large" @click="showPreview">
            <el-icon><View /></el-icon>
            预览数据
          </el-button>
        </div>
      </div>
    </div>

    <el-card class="task-card" shadow="hover">
      <template #header>
        <div class="task-header">
          <div class="header-left">
            <div class="header-icon">
              <el-icon><List /></el-icon>
            </div>
            <div class="header-info">
              <h3>抓取任务列表</h3>
              <p>管理当前所有抓取任务</p>
            </div>
          </div>
        </div>
      </template>

      <el-table :data="tasks" stripe style="width: 100%" size="small">
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
                ? row.platform.join(', ')
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
            <el-tag :type="tagType(row.status)" size="small">
              {{ row.status }}
            </el-tag>
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
            <el-button type="text" @click="editRow(scope.row)">编辑</el-button>
            <el-button
              type="text"
              style="color: #f56c6c"
              @click="removeRow(scope.row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 表单抽屉 -->
    <el-drawer v-model="formDrawer" title="新建抓取任务" size="40%">
      <el-form :model="form" label-width="90px" :rules="rules" ref="formRef">
        <el-form-item label="任务名称" prop="taskName">
          <el-input v-model="form.taskName" placeholder="请输入任务名称" />
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
import { Plus, View, List } from "@element-plus/icons-vue";
import ProgressRing from "@/components/ProgressRing.vue";
import {
  getCustomerList,
  createCustomer,
  deleteCustomer,
  updateCustomer,
} from "@/api/customerCollect";

const tasks = ref([]);
const previewData = ref([]);
const formDrawer = ref(false);
const previewDialog = ref(false);

const formRef = ref();
const rules = {
  taskName: [{ required: true, message: "请输入任务名称", trigger: "blur" }],
};

const platforms = [
  { label: "LinkedIn", value: "linkedin" },
  { label: "Facebook", value: "facebook" },
];

const form = ref({
  taskName: "",
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
    taskName: "",
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
    taskName: row.name,
    platform: Array.isArray(row.platform)
      ? row.platform
      : String(row.platform || "")
          .split(",")
          .filter(Boolean),
    type: row.type || "customer",
    cycle: row.cycle || "once",
    fields: Array.isArray(row.fields)
      ? row.fields
      : String(row.fields || "")
          .split(",")
          .filter(Boolean),
    amount: row.amount || 100,
  };
  formDrawer.value = true;
}

async function removeRow(row) {
  await deleteCustomer(row.id);
  loadData();
}

async function saveTask() {
  try {
    await formRef.value.validate();
  } catch (err) {
    return;
  }
  const payload = {
    name: form.value.taskName,
    platform: form.value.platform.join(","), // 改成字符串
    fields: form.value.fields.join(","), // 改成字符串
    type: form.value.type,
    cycle: form.value.cycle,
    amount: form.value.amount,
  };
  if (editing.value && currentId.value) {
    await updateCustomer(currentId.value, payload);
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
.crawl-container {
  padding: 24px;
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  min-height: 100vh;
  position: relative;
}

.crawl-container::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(
    135deg,
    rgba(102, 126, 234, 0.02) 0%,
    rgba(118, 75, 162, 0.02) 100%
  );
  z-index: -1;
}

.page-header {
  margin-bottom: 32px;
  background: linear-gradient(
    135deg,
    rgba(255, 255, 255, 0.95) 0%,
    rgba(248, 250, 252, 0.95) 100%
  );
  border-radius: 20px;
  padding: 32px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.3);
  backdrop-filter: blur(10px);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 24px;
}

.header-text h1 {
  margin: 0 0 12px 0;
  font-size: 32px;
  font-weight: 800;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  letter-spacing: 0.5px;
}

.header-text p {
  margin: 0;
  color: #64748b;
  font-size: 16px;
  font-weight: 500;
  opacity: 0.8;
}

.header-actions {
  display: flex;
  gap: 12px;
  flex-shrink: 0;
}

.task-card {
  border-radius: 20px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.3);
  background: linear-gradient(
    135deg,
    rgba(255, 255, 255, 0.95) 0%,
    rgba(248, 250, 252, 0.95) 100%
  );
  backdrop-filter: blur(10px);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.task-card:hover {
  box-shadow: 0 12px 40px rgba(102, 126, 234, 0.12);
  transform: translateY(-2px);
}

.task-header {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 0;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.header-icon {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  color: #fff;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.header-icon .el-icon {
  font-size: 18px;
}

.header-info h3 {
  margin: 0 0 4px 0;
  font-size: 18px;
  font-weight: 700;
  color: #1e293b;
}

.header-info p {
  margin: 0;
  color: #64748b;
  font-size: 14px;
  font-weight: 500;
}
</style>

