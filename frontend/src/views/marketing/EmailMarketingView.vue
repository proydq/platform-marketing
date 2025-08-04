<template>
  <div class="email-marketing">
    <el-button type="primary" @click="dialogVisible = true">
      {{ t("email.send") }}
    </el-button>

    <el-dialog
      v-model="dialogVisible"
      :title="t('email.send')"
      width="600px"
      align-center
    >
      <el-form ref="formRef" :model="form" label-width="100px" class="dialog-form">
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
        <el-table-column prop="sendTime" label="Send Time" width="180" />
        <el-table-column
          prop="recipients"
          :label="t('email.recipients')"
          width="120"
        >
          <template #default="{ row }">
            {{ Array.isArray(row.recipients) ? row.recipients.length : row.recipients }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="Status" width="120" />
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
import $api from "@/utils/request";

const { t } = useI18n();
const formRef = ref();
const dialogVisible = ref(false);
const form = ref({ subject: "", content: "", recipients: [] });
const records = ref([]);
const total = ref(0);
const page = ref(1);
const pageSize = 10;

async function fetchHistory() {
  try {
    const res = await $api.get("/email/history", {
      params: { page: page.value, size: pageSize },
    });
    const data = res?.data || res;
    records.value = data?.rows || data?.list || data || [];
    total.value = data?.total || records.value.length;
  } catch (e) {
    records.value = [];
    total.value = 0;
  }
}

onMounted(fetchHistory);

async function handleCsvUpload({ file, onError, onSuccess }) {
  const formData = new FormData();
  formData.append("file", file);
  try {
    const res = await $api.post("/email/uploadRecipients", formData, {
      headers: { "Content-Type": "multipart/form-data" },
    });
    form.value.recipients = res?.recipients || [];
    ElMessage.success(t("email.uploadCSV") + " success");
    onSuccess && onSuccess(res);
  } catch (err) {
    ElMessage.error(err.message || "Upload failed");
    onError && onError(err);
  }
}

async function sendEmail() {
  await formRef.value.validate(async (valid) => {
    if (!valid) return;
    await $api.post("/email/send", {
      subject: form.value.subject,
      content: form.value.content,
      recipients: form.value.recipients,
    });
    ElMessage.success(t("email.send") + " success");
    dialogVisible.value = false;
    fetchHistory();
  });
}

async function sendTestEmail() {
  await $api.post("/email/test", {
    subject: form.value.subject,
    content: form.value.content,
  });
  ElMessage.success(t("email.test") + " success");
}

function handlePageChange(p) {
  page.value = p;
  fetchHistory();
}
</script>
<style scoped>
.email-marketing {
  padding: 24px;
  max-width: 800px;
  margin: 0 auto;
}

.history-card {
  margin-top: 24px;
}

.dialog-form {
  max-width: 500px;
  margin: 0 auto;
}
</style>
