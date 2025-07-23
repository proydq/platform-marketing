<template>
  <div class="page-wrapper">
    <el-tabs v-model="activeTab">
      <el-tab-pane label="模板管理" name="templates">
        <div class="action-buttons">
          <el-button type="primary" @click="openTemplateDialog(false)"
            ><span class="icon">➕</span>新建模板</el-button
          >
        </div>
        <el-card class="chart-container">
          <el-table :data="templates" style="width: 100%">
            <el-table-column prop="name" label="名称" min-width="120" />
            <el-table-column prop="desc" label="描述" />
            <el-table-column prop="createdAt" label="创建时间" width="160" />
            <el-table-column label="操作" width="180">
              <template #default="{ row }">
                <el-button type="text" @click="viewTemplate(row)"
                  >查看</el-button
                >
                <el-button type="text" @click="openTemplateDialog(true, row)"
                  >编辑</el-button
                >
                <el-button
                  type="text"
                  style="color: #f56c6c"
                  @click="removeTemplate(row)"
                  >删除</el-button
                >
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="群发配置" name="config">
        <el-card class="chart-container">
          <el-form
            :model="configForm"
            label-width="90px"
            style="max-width: 600px"
          >
            <el-form-item label="邮件标题">
              <el-input v-model="configForm.title" />
            </el-form-item>
            <el-form-item label="选择模板">
              <el-select v-model="configForm.templateId" placeholder="选择模板">
                <el-option
                  v-for="t in templates"
                  :key="t.id"
                  :label="t.name"
                  :value="t.id"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="收件人分组">
              <el-select
                v-model="configForm.groups"
                multiple
                placeholder="选择分组"
              >
                <el-option v-for="g in groups" :key="g" :label="g" :value="g" />
              </el-select>
            </el-form-item>
            <el-form-item label="正文">
              <RichTextEditor v-model="configForm.content" />
            </el-form-item>
            <el-form-item>
              <div class="action-buttons">
                <el-button type="primary" @click="saveConfig"
                  >保存配置</el-button
                >
                <el-button @click="testDialogVisible = true"
                  >发送测试</el-button
                >
                <el-button type="success" @click="sendNow">立即发送</el-button>
              </div>
            </el-form-item>
          </el-form>
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="发送记录" name="records">
        <el-card class="chart-container">
          <el-table :data="sendRecords" style="width: 100%">
            <el-table-column prop="title" label="标题" />
            <el-table-column prop="status" label="状态" width="120">
              <template #default="{ row }">
                <span :class="'status-badge status-' + row.status">{{
                  row.status
                }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="time" label="时间" width="160" />
            <el-table-column prop="count" label="发送人数" width="100" />
          </el-table>
        </el-card>
      </el-tab-pane>
    </el-tabs>

    <!-- 模板编辑弹窗 -->
    <el-dialog
      v-model="templateDialogVisible"
      :title="isEdit ? '编辑模板' : '新建模板'"
      width="600px"
    >
      <el-form :model="currentTemplate" label-width="80px">
        <el-form-item label="名称">
          <el-input v-model="currentTemplate.name" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="currentTemplate.desc" />
        </el-form-item>
        <el-form-item label="内容">
          <RichTextEditor v-model="currentTemplate.content" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="templateDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveTemplate">保存</el-button>
      </template>
    </el-dialog>

    <!-- 模板查看抽屉 -->
    <el-drawer v-model="drawerVisible" title="模板详情" size="40%">
      <h3>{{ currentTemplate.name }}</h3>
      <p style="margin-bottom: 10px; color: #666">{{ currentTemplate.desc }}</p>
      <div v-html="currentTemplate.content"></div>
    </el-drawer>

    <!-- 测试发送弹窗 -->
    <el-dialog v-model="testDialogVisible" title="发送测试" width="400px">
      <el-input v-model="testEmail" placeholder="输入测试邮箱" />
      <template #footer>
        <el-button @click="testDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="sendTest">发送</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { ElMessageBox, ElMessage } from "element-plus";
import RichTextEditor from "@/components/RichTextEditor.vue";
import templatesData from "@/mock/emailCampaigns.json";
import sendRecordsData from "@/mock/sendRecords.json";

const activeTab = ref("templates");
const templates = ref([]);
const sendRecords = ref([]);

const templateDialogVisible = ref(false);
const drawerVisible = ref(false);
const isEdit = ref(false);
const currentTemplate = ref({
  id: null,
  name: "",
  desc: "",
  content: "",
  createdAt: "",
});

const configForm = ref({
  title: "",
  templateId: "",
  groups: [],
  content: "",
});
const groups = ["全部客户", "潜在客户", "VIP"];

const testDialogVisible = ref(false);
const testEmail = ref("");

onMounted(() => {
  templates.value = templatesData;
  sendRecords.value = sendRecordsData;
});

function openTemplateDialog(edit, tpl) {
  isEdit.value = edit;
  if (edit && tpl) {
    currentTemplate.value = { ...tpl };
  } else {
    currentTemplate.value = {
      id: null,
      name: "",
      desc: "",
      content: "",
      createdAt: "",
    };
  }
  templateDialogVisible.value = true;
}

function saveTemplate() {
  if (isEdit.value && currentTemplate.value.id) {
    const idx = templates.value.findIndex(
      (t) => t.id === currentTemplate.value.id
    );
    templates.value.splice(idx, 1, { ...currentTemplate.value });
    ElMessage.success("模板已更新");
  } else {
    currentTemplate.value.id = templates.value.length
      ? Math.max(...templates.value.map((t) => t.id)) + 1
      : 1;
    currentTemplate.value.createdAt = new Date().toISOString().split("T")[0];
    templates.value.push({ ...currentTemplate.value });
    ElMessage.success("模板已创建");
  }
  templateDialogVisible.value = false;
}

function viewTemplate(row) {
  currentTemplate.value = { ...row };
  drawerVisible.value = true;
}

function removeTemplate(row) {
  ElMessageBox.confirm("确定删除该模板吗?", "提示", { type: "warning" })
    .then(() => {
      templates.value = templates.value.filter((t) => t.id !== row.id);
      ElMessage.success("已删除");
    })
    .catch(() => {});
}

function saveConfig() {
  ElMessage.success("配置已保存");
}

function sendTest() {
  testDialogVisible.value = false;
  ElMessage.success("测试邮件已发送到 " + testEmail.value);
}

function sendNow() {
  sendRecords.value.push({
    id: sendRecords.value.length
      ? Math.max(...sendRecords.value.map((r) => r.id)) + 1
      : 1,
    title: configForm.value.title,
    status: "running",
    time: new Date().toLocaleString(),
    count: 0,
  });
  ElMessage.success("发送任务已创建");
}
</script>
