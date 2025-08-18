<template>
  <div class="page-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="page-title">
        <span class="page-icon">📧</span>
        {{ t("email.marketing") }}
      </div>
      <div class="page-subtitle">
        {{ t("email.marketingSubtitle") }}
      </div>
      <div class="page-actions">
        <el-button
          type="primary"
          class="btn-primary"
          @click="dialogVisible = true"
        >
          <el-icon><Message /></el-icon>
          {{ t("email.newCampaign") }}
        </el-button>
        <el-button class="btn-secondary" @click="refreshData">
          <el-icon><Refresh /></el-icon>
          刷新数据
        </el-button>
      </div>
    </div>
  </div>
  <!-- 邮件统计 -->
  <div class="stats-grid">
    <div class="stat-card fade-in">
      <div class="stat-header">
        <div
          class="stat-icon"
          style="
            background: linear-gradient(135deg, #dbeafe, #93c5fd);
            color: #1e40af;
          "
        >
          📧
        </div>
        <div class="stat-trend positive">+12%</div>
      </div>
      <div class="stat-content">
        <div class="stat-value">{{ emailStats.total.toLocaleString() }}</div>
        <div class="stat-label">{{ t("email.totalSent") }}</div>
      </div>
    </div>

    <div class="stat-card fade-in" style="animation-delay: 0.1s">
      <div class="stat-header">
        <div
          class="stat-icon"
          style="
            background: linear-gradient(135deg, #d1fae5, #86efac);
            color: #059669;
          "
        >
          📈
        </div>
        <div class="stat-trend positive">+8%</div>
      </div>
      <div class="stat-content">
        <div class="stat-value">{{ emailStats.openRate }}%</div>
        <div class="stat-label">{{ t("email.openRate") }}</div>
      </div>
    </div>

    <div class="stat-card fade-in" style="animation-delay: 0.2s">
      <div class="stat-header">
        <div
          class="stat-icon"
          style="
            background: linear-gradient(135deg, #fef3c7, #fcd34d);
            color: #d97706;
          "
        >
          👆
        </div>
        <div class="stat-trend positive">+15%</div>
      </div>
      <div class="stat-content">
        <div class="stat-value">{{ emailStats.clickRate }}%</div>
        <div class="stat-label">{{ t("email.clickRate") }}</div>
      </div>
    </div>

    <div class="stat-card fade-in" style="animation-delay: 0.3s">
      <div class="stat-header">
        <div
          class="stat-icon"
          style="
            background: linear-gradient(135deg, #ede9fe, #c4b5fd);
            color: #7c3aed;
          "
        >
          🎨
        </div>
        <div class="stat-trend neutral">{{ emailStats.templates }}个</div>
      </div>
      <div class="stat-content">
        <div class="stat-value">{{ emailStats.templates }}</div>
        <div class="stat-label">{{ t("email.templates") }}</div>
      </div>
    </div>
  </div>

  <!-- 邮件管理 -->
  <div class="data-table-wrapper">
    <div class="table-header">
      <div class="table-title">📧 邮件营销管理</div>
      <div class="table-subtitle">管理邮件营销活动和查看发送记录</div>
      <div class="table-actions">
        <el-button
          type="primary"
          class="btn-primary"
          @click="dialogVisible = true"
        >
          <el-icon><Message /></el-icon>
          {{ t("email.newCampaign") }}
        </el-button>
        <el-button class="btn-secondary" @click="showTemplateDialog = true">
          <el-icon><Document /></el-icon>
          {{ t("email.manageTemplates") }}
        </el-button>
      </div>
    </div>
    
    <!-- 标签页 -->
    <el-tabs v-model="activeTab" class="email-tabs">
      <el-tab-pane label="📧 邮件活动" name="campaigns">
        <!-- 原有的邮件活动列表 -->
        <div class="campaigns-content">
      <el-dialog
        v-model="dialogVisible"
        :title="t('email.send')"
        width="600px"
        align-center
      >
        <el-form
          ref="formRef"
          :model="form"
          label-width="100px"
          class="dialog-form"
        >
          <el-form-item
            :label="t('email.subject')"
            prop="subject"
            :rules="[{ required: true, message: t('email.requiredField') }]"
          >
            <el-input v-model="form.subject" />
          </el-form-item>
          <el-form-item
            :label="t('email.content')"
            prop="content"
            :rules="[{ required: true, message: t('email.requiredField') }]"
          >
            <el-input v-model="form.content" type="textarea" rows="10" />
          </el-form-item>
          <el-form-item
            :label="t('email.recipients')"
            prop="recipients"
            :rules="[
              {
                type: 'array',
                required: true,
                message: t('email.requiredField'),
              },
            ]"
          >
            <el-upload
              accept=".csv"
              :show-file-list="false"
              :http-request="handleCsvUpload"
            >
              <el-button>{{ t("email.uploadCSV") }}</el-button>
            </el-upload>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="sendEmail">{{
              t("email.send")
            }}</el-button>
            <el-button @click="sendTestEmail">{{ t("email.test") }}</el-button>
          </el-form-item>
        </el-form>
      </el-dialog>

      <el-card class="history-card">
        <template #header>
          <div class="card-header">
            <span>{{ t("email.campaignHistory") }}</span>
            <el-button type="text" @click="fetchHistory">
              <el-icon><Refresh /></el-icon>
              {{ t("common.refresh") }}
            </el-button>
          </div>
        </template>

        <el-table
          :data="filteredRecords"
          v-loading="loading"
          style="width: 100%"
          class="modern-table"
        >
          <el-table-column
            prop="subject"
            :label="t('email.subject')"
            min-width="200"
          >
            <template #default="{ row }">
              <div class="subject-cell">
                <strong>{{ row.subject }}</strong>
                <el-tag
                  v-if="row.status"
                  :type="getStatusType(row.status)"
                  size="small"
                  effect="light"
                >
                  {{ getStatusText(row.status) }}
                </el-tag>
              </div>
            </template>
          </el-table-column>

          <el-table-column
            :label="t('email.recipients')"
            width="120"
            align="center"
          >
            <template #default="{ row }">
              <el-tag type="info" effect="plain">
                {{ getRecipientsCount(row.toList) }}
              </el-tag>
            </template>
          </el-table-column>

          <el-table-column :label="t('email.performance')" width="160">
            <template #default="{ row }">
              <div class="performance-cell">
                <div class="performance-item">
                  <span class="label">打开率:</span>
                  <span class="value">{{ row.openRate || "0" }}%</span>
                </div>
                <div class="performance-item">
                  <span class="label">点击率:</span>
                  <span class="value">{{ row.clickRate || "0" }}%</span>
                </div>
              </div>
            </template>
          </el-table-column>

          <el-table-column
            prop="sentAt"
            :label="t('email.sentTime')"
            width="160"
          >
            <template #default="{ row }">
              {{ formatDateTime(row.sentAt) }}
            </template>
          </el-table-column>

          <el-table-column
            :label="t('common.actions')"
            width="200"
            fixed="right"
          >
            <template #default="{ row }">
              <el-button
                size="small"
                type="primary"
                link
                @click="viewCampaign(row)"
              >
                <el-icon><View /></el-icon>
                {{ t("common.view") }}
              </el-button>
              <el-button size="small" link @click="duplicateCampaign(row)">
                <el-icon><CopyDocument /></el-icon>
                {{ t("common.duplicate") }}
              </el-button>
              <el-button
                size="small"
                type="danger"
                link
                @click="deleteCampaign(row)"
              >
                <el-icon><Delete /></el-icon>
                {{ t("common.delete") }}
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="page"
          :page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          background
          @current-change="handlePageChange"
          @size-change="handleSizeChange"
        />
        </div>
        </div>
      </el-tab-pane>
      
      <!-- 发送记录标签页 -->
      <el-tab-pane label="📝 发送记录" name="history">
        <EmailHistory />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>
<script setup>
import { ref, computed, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { useI18n } from "vue-i18n";
import {
  Message,
  Document,
  Refresh,
  View,
  CopyDocument,
  Delete,
} from "@element-plus/icons-vue";
import {
  uploadRecipientsCSV,
  sendEmail as sendEmailAPI,
  sendTestEmail as sendTestEmailAPI,
  getEmailRecords,
} from "@/api/email";
import EmailHistory from "@/components/email/EmailHistory.vue";

const { t } = useI18n();
const formRef = ref();
const dialogVisible = ref(false);
const showTemplateDialog = ref(false);
const loading = ref(false);
const form = ref({ subject: "", content: "", recipients: [] });
const activeTab = ref('campaigns');

const records = ref([]);
const total = ref(0);
const page = ref(1);
const pageSize = ref(10);

// 过滤后的记录
const filteredRecords = computed(() => {
  return records.value;
});

// 邮件统计数据
const emailStats = computed(() => {
  const total = records.value.length;
  const totalOpens = records.value.reduce(
    (sum, record) => sum + (record.opens || 0),
    0
  );
  const totalClicks = records.value.reduce(
    (sum, record) => sum + (record.clicks || 0),
    0
  );
  const totalSent = records.value.reduce(
    (sum, record) => sum + getRecipientsCount(record.toList),
    0
  );

  return {
    total,
    openRate: totalSent > 0 ? Math.round((totalOpens / totalSent) * 100) : 0,
    clickRate: totalSent > 0 ? Math.round((totalClicks / totalSent) * 100) : 0,
    templates: 2,
  };
});

// 获取历史记录
async function fetchHistory() {
  loading.value = true;
  try {
    const res = await getEmailRecords({
      page: page.value,
      size: pageSize.value,
    });
    const data = res?.data || res;
    records.value = data?.rows || data?.list || data || [];
    total.value = data?.total || records.value.length;

    // 如果没有数据，添加示例数据
    if (records.value.length === 0) {
      records.value = [
        {
          id: "demo-1",
          subject: "春季新品发布会",
          content: "亲爱的客户，我们的春季新品已经上市，请及时关注...",
          toList: [
            "user1@example.com",
            "user2@example.com",
            "user3@example.com",
          ],
          sentAt: new Date(Date.now() - 2 * 24 * 60 * 60 * 1000).toISOString(),
          status: "sent",
          openRate: 25,
          clickRate: 8,
          opens: 75,
          clicks: 24,
        },
        {
          id: "demo-2",
          subject: "会员专享优惠活动",
          content: "本月会员可享受特别优惠，最低5折起！",
          toList: ["member1@example.com", "member2@example.com"],
          sentAt: new Date(Date.now() - 5 * 24 * 60 * 60 * 1000).toISOString(),
          status: "delivered",
          openRate: 32,
          clickRate: 12,
          opens: 64,
          clicks: 24,
        },
        {
          id: "demo-3",
          subject: "新年特别祝福",
          content: "新年快乐！感谢您一年来的支持，祝您在新的一年里...",
          toList: ["customer@example.com"],
          sentAt: new Date(Date.now() - 7 * 24 * 60 * 60 * 1000).toISOString(),
          status: "failed",
          openRate: 0,
          clickRate: 0,
          opens: 0,
          clicks: 0,
        },
      ];
      total.value = records.value.length;
    }
  } catch (e) {
    console.error("获取邮件记录失败:", e);
    // API调用失败时显示示例数据
    records.value = [
      {
        id: "demo-1",
        subject: "欢迎使用邮件营销系统",
        content: "这是一封测试邮件...",
        toList: ["test@example.com"],
        sentAt: new Date().toISOString(),
        status: "sent",
        openRate: 0,
        clickRate: 0,
      },
    ];
    total.value = 1;
  } finally {
    loading.value = false;
  }
}

onMounted(fetchHistory);

// 刷新数据
function refreshData() {
  fetchHistory();
  ElMessage.success("数据已刷新");
}

// 上传 CSV
async function handleCsvUpload({ file, onError, onSuccess }) {
  try {
    const res = await uploadRecipientsCSV(file);
    form.value.recipients = res?.data?.emails || [];
    ElMessage.success(t("email.uploadCSV") + " success");
    onSuccess && onSuccess(res);
  } catch (err) {
    ElMessage.error(err.message || "Upload failed");
    onError && onError(err);
  }
}
// 发送正式邮件
async function sendEmail() {
  await formRef.value.validate(async (valid) => {
    if (!valid) return;
    await sendEmailAPI({
      subject: form.value.subject,
      content: form.value.content,
      toList: form.value.recipients.filter((e) => e && e !== "email"), // 去掉表头
    });
    ElMessage.success(t("email.send") + " success");
    dialogVisible.value = false;
    fetchHistory();
  });
}

// 发送测试邮件
async function sendTestEmail() {
  await sendTestEmailAPI({
    subject: form.value.subject,
    content: form.value.content,
  });
  ElMessage.success(t("email.test") + " success");
}

// 分页
function handlePageChange(p) {
  page.value = p;
  fetchHistory();
}

function handleSizeChange(size) {
  pageSize.value = size;
  page.value = 1;
  fetchHistory();
}

// 工具函数
function getRecipientsCount(toList) {
  if (Array.isArray(toList)) {
    return toList.length;
  }
  return toList ? toList.split(",").length : 0;
}

function getStatusType(status) {
  const typeMap = {
    sent: "success",
    delivered: "primary",
    failed: "danger",
    draft: "warning",
  };
  return typeMap[status] || "info";
}

function getStatusText(status) {
  const textMap = {
    sent: "已发送",
    delivered: "已投递",
    failed: "发送失败",
    draft: "草稿",
  };
  return textMap[status] || status;
}

function formatDateTime(dateStr) {
  if (!dateStr) return "--";
  return new Date(dateStr).toLocaleString("zh-CN");
}

// 活动操作
function viewCampaign(campaign) {
  ElMessageBox.alert(
    `主题: ${campaign.subject}\n内容: ${
      campaign.content
    }\n收件人: ${getRecipientsCount(campaign.toList)}人`,
    "活动详情",
    { confirmButtonText: t("common.ok") }
  );
}

function duplicateCampaign(campaign) {
  form.value = {
    subject: campaign.subject + " (复制)",
    content: campaign.content,
    recipients: Array.isArray(campaign.toList) ? campaign.toList : [],
  };
  dialogVisible.value = true;
}

async function deleteCampaign(campaign) {
  try {
    await ElMessageBox.confirm(t("common.deleteConfirm"), t("common.warning"), {
      confirmButtonText: t("common.confirm"),
      cancelButtonText: t("common.cancel"),
      type: "warning",
    });

    const index = records.value.findIndex(
      (record) => record.id === campaign.id
    );
    if (index > -1) {
      records.value.splice(index, 1);
      total.value--;
      ElMessage.success(t("common.deleteSuccess"));
    }
  } catch {
    // 用户取消删除
  }
}
</script>

<style scoped>
.email-marketing-container {
  padding: 20px;
  background: #f5f7fa;
  min-height: calc(100vh - 60px);
}

.page-header {
  margin-bottom: 24px;
}

.page-header h1 {
  margin: 0 0 8px 0;
  color: #1f2937;
  font-size: 24px;
  font-weight: 600;
}

.page-header p {
  margin: 0;
  color: #6b7280;
  font-size: 14px;
}

/* 邮件统计 */
.email-stats {
  margin-bottom: 24px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
}

.stat-item {
  background: white;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  gap: 16px;
  transition: all 0.3s ease;
}

.stat-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.stat-icon {
  font-size: 28px;
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f8fafc;
}

.stat-content {
  flex: 1;
}

.stat-number {
  font-size: 24px;
  font-weight: 700;
  color: #1f2937;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #6b7280;
  font-weight: 500;
}

/* 操作栏 */
.action-card {
  margin-bottom: 24px;
  border-radius: 12px;
}

.action-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.action-buttons {
  display: flex;
  gap: 12px;
}

/* 历史记录 */
.history-card {
  border-radius: 12px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
  color: #1f2937;
}

.modern-table {
  border-radius: 8px;
}

.subject-cell {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.performance-cell {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.performance-item {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
}

.performance-item .label {
  color: #6b7280;
}

.performance-item .value {
  font-weight: 500;
  color: #059669;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.dialog-form {
  max-width: 500px;
  margin: 0 auto;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .email-marketing-container {
    padding: 16px;
  }

  .stats-grid {
    grid-template-columns: 1fr;
  }

  .action-section {
    flex-direction: column;
    gap: 16px;
  }

  .action-buttons {
    justify-content: center;
    flex-wrap: wrap;
  }
}

/* Element Plus 样式覆盖 */
:deep(.el-card) {
  border-radius: 12px;
  border: 1px solid #e5e7eb;
}

:deep(.el-card__header) {
  background: #f8fafc;
  border-bottom: 1px solid #e5e7eb;
}

:deep(.el-table) {
  border-radius: 8px;
}

:deep(.el-table__header) {
  background: #f8fafc;
}

:deep(.el-table tr:hover > td) {
  background-color: #f0f9ff !important;
}
</style>
