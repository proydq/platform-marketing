<template>
  <div class="page-wrapper">
    <div class="action-buttons">
      <el-button type="primary" @click="openDialog(false)">
        <span class="icon">➕</span>新建任务
      </el-button>
      <el-select v-model="filterStatus" placeholder="状态" style="width: 120px">
        <el-option label="全部" value="" />
        <el-option label="进行中" value="running" />
        <el-option label="成功" value="success" />
        <el-option label="失败" value="error" />
        <el-option label="已暂停" value="paused" />
      </el-select>
      <el-select
        v-model="filterTags"
        multiple
        placeholder="标签"
        style="min-width: 180px"
      >
        <el-option v-for="t in allTags" :key="t" :label="t" :value="t" />
      </el-select>
      <el-input
        v-model="searchKey"
        placeholder="关键词搜索"
        clearable
        style="width: 200px"
      />
    </div>

    <el-card class="task-list">
      <el-table :data="filtered" style="width: 100%">
        <el-table-column prop="name" label="任务名称" min-width="160" />
        <el-table-column prop="cycle" label="周期" width="80" />
        <el-table-column label="状态" width="120">
          <template #default="{ row }">
            <span :class="'status-badge status-' + row.status">{{
              row.status
            }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="lastRun" label="上次执行时间" width="160" />
        <el-table-column label="操作" width="220">
          <template #default="{ row }">
            <el-button type="text" @click="viewDetail(row)">查看</el-button>
            <el-button type="text" @click="openDialog(true, row)"
              >编辑</el-button
            >
            <el-switch
              v-model="row.enabled"
              @change="toggleEnabled(row)"
              size="small"
              style="margin: 0 5px"
            />
            <el-button
              type="text"
              style="color: #f56c6c"
              @click="removeTask(row)"
              >删除</el-button
            >
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 任务编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑任务' : '新建任务'"
      width="600px"
    >
      <el-form :model="form" label-width="90px">
        <el-form-item label="任务名称"
          ><el-input v-model="form.name"
        /></el-form-item>
        <el-form-item label="描述"
          ><el-input v-model="form.desc"
        /></el-form-item>
        <el-form-item label="执行周期">
          <el-select v-model="form.cycle">
            <el-option label="每天" value="每天" />
            <el-option label="每周" value="每周" />
            <el-option label="每月" value="每月" />
          </el-select>
        </el-form-item>
        <el-form-item label="起始时间">
          <el-date-picker
            v-model="form.startTime"
            type="datetime"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="是否启用"
          ><el-switch v-model="form.enabled"
        /></el-form-item>
        <el-form-item label="营销动作">
          <el-select v-model="form.actions" multiple placeholder="选择动作">
            <el-option label="发送邮件" value="发送邮件" />
            <el-option label="社媒发布" value="社媒发布" />
            <el-option label="生成报表" value="生成报表" />
          </el-select>
        </el-form-item>
        <el-form-item label="标签">
          <el-select
            v-model="form.tags"
            multiple
            filterable
            allow-create
            default-first-option
            placeholder="请输入或选择标签"
          >
            <el-option v-for="t in allTags" :key="t" :label="t" :value="t" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveForm">保存</el-button>
      </template>
    </el-dialog>

    <!-- 任务详情抽屉 -->
    <el-drawer v-model="drawerVisible" title="任务详情" size="40%">
      <h3>{{ currentDetail.name }}</h3>
      <p>{{ currentDetail.desc }}</p>
      <el-descriptions :column="1" style="margin-top: 10px">
        <el-descriptions-item label="周期">{{
          currentDetail.cycle
        }}</el-descriptions-item>
        <el-descriptions-item label="启用">{{
          currentDetail.enabled ? "是" : "否"
        }}</el-descriptions-item>
        <el-descriptions-item label="上次执行">{{
          currentDetail.lastRun
        }}</el-descriptions-item>
        <el-descriptions-item label="营销动作">
          <el-tag
            v-for="a in currentDetail.actions"
            :key="a"
            size="small"
            style="margin-right: 4px"
            >{{ a }}</el-tag
          >
        </el-descriptions-item>
        <el-descriptions-item label="标签">
          <el-tag
            v-for="t in normalizeStringArray(currentDetail.tags)"
            :key="t"
            size="small"
            style="margin-right: 4px"
          >
            {{ t }}
          </el-tag>
        </el-descriptions-item>
      </el-descriptions>
      <h4 style="margin: 20px 0 10px">运行日志</h4>
      <el-timeline>
        <el-timeline-item v-for="n in 3" :key="n" timestamp="2024-06-10 08:00">
          执行日志 {{ n }}
        </el-timeline-item>
      </el-timeline>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import {
  getTaskList,
  createTask,
  updateTask,
  deleteTask,
  toggleTask,
  getTaskDetail,
} from "@/api/scheduleTask";

const tasks = ref([]);
const allTags = ref([]);
const filterStatus = ref("");
const filterTags = ref([]);
const searchKey = ref("");

const dialogVisible = ref(false);
const drawerVisible = ref(false);
const isEdit = ref(false);

const form = ref({
  id: null,
  name: "",
  desc: "",
  cycle: "每天",
  startTime: "",
  enabled: true,
  actions: [],
  tags: [],
});

const currentDetail = ref({});

function normalizeStringArray(val) {
  if (!Array.isArray(val)) return [];
  return val
    .flatMap((item) => {
      try {
        const parsed = JSON.parse(item);
        return Array.isArray(parsed) ? parsed : [parsed];
      } catch {
        return [item];
      }
    })
    .map((str) => String(str).trim());
}

function loadTaskList() {
  getTaskList({
    status: filterStatus.value,
    tags: filterTags.value,
    keyword: searchKey.value,
  }).then((res) => {
    tasks.value = res.data.map((t) => ({
      ...t,
      tags: normalizeStringArray(t.tags),
      actions: normalizeStringArray(t.actions),
    }));
    const tagSet = new Set();
    tasks.value.forEach((t) => t.tags.forEach((tag) => tagSet.add(tag)));
    allTags.value = Array.from(tagSet);
  });
}

onMounted(loadTaskList);
watch([filterStatus, filterTags, searchKey], loadTaskList);

function openDialog(edit, row) {
  isEdit.value = edit;
  form.value =
    edit && row
      ? {
          ...row,
          tags: normalizeStringArray(row.tags),
          actions: normalizeStringArray(row.actions),
        }
      : {
          id: null,
          name: "",
          desc: "",
          cycle: "每天",
          startTime: "",
          enabled: true,
          actions: [],
          tags: [],
        };
  dialogVisible.value = true;
}

function saveForm() {
  const payload = { ...form.value };
  const action = isEdit.value ? updateTask : createTask;
  action(payload).then(() => {
    ElMessage.success(isEdit.value ? "更新成功" : "创建成功");
    dialogVisible.value = false;
    loadTaskList();
  });
}

function removeTask(row) {
  ElMessageBox.confirm("确定删除该任务吗?", "提示", { type: "warning" })
    .then(() =>
      deleteTask(row.id).then(() => {
        ElMessage.success("已删除");
        loadTaskList();
      })
    )
    .catch(() => {});
}

function toggleEnabled(row) {
  toggleTask(row.id).then(() => {
    ElMessage.success("状态已切换");
    loadTaskList();
  });
}

function viewDetail(row) {
  getTaskDetail(row.id).then((res) => {
    currentDetail.value = {
      ...res.data,
      tags: normalizeStringArray(res.data.tags),
      actions: normalizeStringArray(res.data.actions),
    };
    drawerVisible.value = true;
  });
}

const filtered = computed(() => tasks.value);
</script>

<style scoped>
.task-list {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.task-list .el-card__body {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  padding: 0;
}

.task-list .el-table {
  flex: 1;
  overflow-y: auto;
}
</style>
