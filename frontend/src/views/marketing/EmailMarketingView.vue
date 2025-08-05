<template>
  <el-button type="primary" @click="dialogVisible = true">
    {{ t("email.send") }}
  </el-button>
  <div class="email-marketing">
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
      <el-table :data="records" style="width: 100%">
        <el-table-column prop="subject" :label="t('email.subject')" />
        <el-table-column prop="content" :label="t('email.content')" />
        <el-table-column prop="sentAt" label="Send Time" />
        <el-table-column
          prop="toList"
          :label="t('email.recipients')"
          width="300"
        >
          <template #default="{ row }">
            {{ Array.isArray(row.toList) ? row.toList.join(", ") : row.toList }}
          </template>
        </el-table-column>
      </el-table>

      <div class="text-right mt-4">
        <el-pagination
          background
          v-model:current-page="page"
          :page-size="pageSize"
          :total="total"
          layout="total, prev, pager, next"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>
  </div>
</template>
<script setup>
import { ref, onMounted } from "vue";
import { ElMessage } from "element-plus";
import { useI18n } from "vue-i18n";
import {
  uploadRecipientsCSV,
  sendEmail as sendEmailAPI,
  sendTestEmail as sendTestEmailAPI,
  getEmailRecords,
} from "@/api/email";

const { t } = useI18n();
const formRef = ref();
const dialogVisible = ref(false);
const form = ref({ subject: "", content: "", recipients: [] });

const records = ref([]);
const total = ref(0);
const page = ref(1);
const pageSize = 10;

// 获取历史记录
async function fetchHistory() {
  try {
    const res = await getEmailRecords({ page: page.value, size: pageSize });
    const data = res?.data || res;
    records.value = data?.rows || data?.list || data || [];
    total.value = data?.total || records.value.length;
  } catch (e) {
    records.value = [];
    total.value = 0;
  }
}

onMounted(fetchHistory);

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
</script>

<style scoped>
.email-marketing {
  display: flex;
  flex-direction: column;
  height: 100vh;
  gap: 16px;
  box-sizing: border-box;
  background-color: #fff;
}

.history-card {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  border-radius: 12px;
}

.dialog-form {
  max-width: 500px;
  margin: 0 auto;
}
</style>
