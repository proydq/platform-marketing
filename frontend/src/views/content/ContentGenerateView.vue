<template>
  <div class="page-wrapper">
    <!-- 筛选操作栏 -->
    <div class="action-buttons">
      <el-input
        v-model="keyword"
        placeholder="输入关键词"
        style="width: 200px"
      />
      <el-select v-model="styleOpt" placeholder="风格" style="width: 120px">
        <el-option label="正式" value="正式" />
        <el-option label="活泼" value="活泼" />
        <el-option label="简洁" value="简洁" />
      </el-select>
      <el-select v-model="useOpt" placeholder="用途" style="width: 120px">
        <el-option label="社交媒体" value="社交媒体" />
        <el-option label="邮件" value="邮件" />
        <el-option label="博客" value="博客" />
      </el-select>
      <el-button type="primary" @click="generateContent">
        <span class="icon">⚡</span>生成内容
      </el-button>
    </div>

    <!-- 表格区域 -->
    <el-card class="content-card">
      <div class="table-wrapper">
        <el-table :data="filtered" style="width: 100%">
          <el-table-column prop="name" label="标题" min-width="160" />
          <el-table-column prop="status" label="状态" width="100" />
          <el-table-column prop="startTime" label="开始时间" width="160" />
          <el-table-column prop="endTime" label="结束时间" width="160" />
          <el-table-column prop="style" label="风格" width="80" />
          <el-table-column prop="use" label="用途" width="100" />
          <el-table-column prop="status" label="状态" width="100" />
          <el-table-column label="操作" width="220">
            <template #default="{ row }">
              <el-button type="text" @click="openDetail(row)">查看</el-button>
              <el-button type="text" @click="openEdit(true, row)"
                >编辑</el-button
              >
              <el-button type="text" @click="publish(row)">发布</el-button>
              <el-button type="text" style="color: #f56c6c" @click="remove(row)"
                >删除</el-button
              >
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-card>

    <!-- 编辑 Drawer -->
    <el-drawer
      v-model="editDrawer"
      :title="editMode ? '编辑文案' : '新建文案'"
      size="40%"
      direction="rtl"
    >
      <el-form :model="form" label-width="90px" class="form-section">
        <el-form-item label="标题">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="正文">
          <RichTextEditor v-model="form.contents[0]" />
        </el-form-item>
        <el-form-item label="用途">
          <el-select v-model="form.use">
            <el-option label="社交媒体" value="社交媒体" />
            <el-option label="邮件" value="邮件" />
            <el-option label="博客" value="博客" />
          </el-select>
        </el-form-item>
        <el-form-item label="发布渠道">
          <el-select v-model="form.channels" multiple>
            <el-option label="Facebook" value="Facebook" />
            <el-option label="Email" value="Email" />
            <el-option label="Twitter" value="Twitter" />
          </el-select>
        </el-form-item>
        <el-form-item label="周期">
          <el-select v-model="form.cycle">
            <el-option label="一次" value="once" />
            <el-option label="每天" value="daily" />
            <el-option label="每周" value="weekly" />
          </el-select>
        </el-form-item>
        <el-form-item label="开始时间">
          <el-date-picker
            v-model="form.startTime"
            type="datetime"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="结束时间">
          <el-date-picker
            v-model="form.endTime"
            type="datetime"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDrawer = false">取消</el-button>
        <el-button @click="save">保存</el-button>
      </template>
    </el-drawer>

    <!-- 详情 Drawer -->
    <el-drawer
      v-model="detailDrawer"
      title="文案详情"
      size="40%"
      direction="rtl"
    >
      <h3>{{ current.name }}</h3>
      <el-descriptions :column="1" style="margin: 10px 0">
        <el-descriptions-item label="风格">{{
          current.style
        }}</el-descriptions-item>
        <el-descriptions-item label="用途">{{
          current.use
        }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <span :class="['status-badge', current.status]">{{
            current.status
          }}</span>
        </el-descriptions-item>
      </el-descriptions>
      <div
        style="max-height: 300px; overflow: auto"
        v-html="current.contents?.[0] || ''"
      ></div>
    </el-drawer>
  </div>
</template>
<script setup>
import { ref, reactive, computed, onMounted } from "vue";
import { ElMessage } from "element-plus";
import { useI18n } from "vue-i18n";
import FunnelChart from "@/components/charts/FunnelChart.vue";
import RichTextEditor from "@/components/RichTextEditor.vue";
import {
  getContentGenerationList,
  getContentGenerationDetail,
  createContentGeneration,
  updateContentGeneration,
  deleteContentGeneration,
} from "@/api/contentGeneration";

const campaigns = ref([]);
const statusFilter = ref("");
const channelFilter = ref("");
const search = ref("");

const { t } = useI18n();

const editDrawer = ref(false);
const detailDrawer = ref(false);
const editMode = ref(false);
const current = ref(null);

const metricsFunnel = computed(() => {
  if (!current.value || !current.value.metrics) return [];
  return [
    { step: t("campaign.sent"), value: current.value.metrics.sent },
    { step: t("campaign.opens"), value: current.value.metrics.opens },
    { step: t("campaign.clicks"), value: current.value.metrics.clicks },
  ];
});

const form = reactive({
  id: null,
  name: "",
  channels: [],
  startTime: "",
  endTime: "",
  contents: [""],
  cycle: "once",
  status: "pending",
});

onMounted(loadData);

async function loadData() {
  const res = await getContentGenerationList();
  const rows = res.data?.rows || [];

  campaigns.value = rows.map((row) => ({
    ...row,
    // 防止后端字段是字符串
    channels:
      typeof row.channels === "string"
        ? JSON.parse(row.channels)
        : row.channels,
    contents:
      typeof row.contents === "string"
        ? JSON.parse(row.contents)
        : row.contents,
  }));
}

const filtered = computed(() => {
  return campaigns.value.filter((c) => {
    const sOk = !statusFilter.value || c.status === statusFilter.value;
    const cOk =
      !channelFilter.value || c.channels.includes(channelFilter.value);
    const qOk = !search.value || c.name.includes(search.value);
    return sOk && cOk && qOk;
  });
});

function statusText(s) {
  const map = {
    pending: t("campaign.pending"),
    running: t("campaign.running"),
    success: t("campaign.success"),
    paused: t("campaign.pause"),
  };
  return map[s] || s;
}

function channelName(c) {
  const map = {
    email: "Email",
    social: t("sidebar.socialMedia"),
    wechat: "Wechat",
    custom: t("campaign.custom"),
  };
  return map[c] || c;
}

function openEdit(edit, row) {
  editMode.value = edit;
  if (edit && row) {
    Object.assign(form, JSON.parse(JSON.stringify(row)));
    if (!form.contents || !form.contents.length) form.contents = [""];
  } else {
    Object.assign(form, {
      id: null,
      name: "",
      channels: [],
      startTime: "",
      endTime: "",
      contents: [""],
      cycle: "once",
      status: "pending",
    });
  }
  editDrawer.value = true;
}

function addContent() {
  form.contents.push("");
}

async function save() {
  if (editMode.value) {
    await updateContentGeneration(form.id, form);
    ElMessage.success(t("common.saveSuccess"));
  } else {
    await createContentGeneration(form);
    ElMessage.success(t("common.saveSuccess"));
  }
  editDrawer.value = false;
  loadData();
}

async function remove(row) {
  await deleteContentGeneration(row.id);
  ElMessage.success(t("common.deleted"));
  loadData();
}

async function openDetail(row) {
  const res = await getContentGenerationDetail(row.id);
  current.value = res.data || row;
  if (!current.value.contents) current.value.contents = row.contents || [];
  detailDrawer.value = true;
}

function publish(row) {
  row.status = "running";
  ElMessage.success(t("campaign.publish"));
}

function togglePause(row) {
  row.status = row.status === "paused" ? "running" : "paused";
  ElMessage.success(
    row.status === "paused" ? t("campaign.pause") : t("campaign.resume")
  );
}
</script>
<style scoped>
.content-card {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}
</style>
