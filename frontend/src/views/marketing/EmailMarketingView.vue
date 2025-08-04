<template>
  <div class="email-marketing">
    <el-card class="form-card">
      <el-form ref="formRef" :model="form" label-width="100px">
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
          <el-input
            v-model="form.content"
            type="textarea"
            rows="10"
          />
        </el-form-item>
        <el-form-item
          :label="t('email.recipients')"
          prop="recipients"
          :rules="[
            { type: 'array', required: true, message: t('email.requiredField') },
          ]"
        >
          <el-upload
            accept=".csv"
            :show-file-list="false"
            :http-request="handleCsvUpload"
          >
            <el-button>{{ t('email.uploadCSV') }}</el-button>
          </el-upload>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="sendEmail">{{
            t('email.send')
          }}</el-button>
          <el-button @click="sendTestEmail">{{ t('email.test') }}</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card class="history-card">
      <h3>{{ t('email.history') }}</h3>
      <EmailHistory />
    </el-card>
  </div>
</template>
<script setup>
import { ref } from "vue";
import { ElMessage } from "element-plus";
import { useI18n } from "vue-i18n";
import EmailHistory from "@/components/email/EmailHistory.vue";
import $api from "@/utils/request";

const { t } = useI18n();
const formRef = ref();
const form = ref({ subject: "", content: "", recipients: [] });

async function handleCsvUpload({ file, onError, onSuccess }) {
  const formData = new FormData();
  formData.append('file', file);
  try {
    const res = await $api.post('/email/uploadRecipients', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
    form.value.recipients = res?.recipients || [];
    ElMessage.success(t('email.uploadCSV') + ' success');
    onSuccess && onSuccess(res);
  } catch (err) {
    ElMessage.error(err.message || 'Upload failed');
    onError && onError(err);
  }
}

async function sendEmail() {
  await formRef.value.validate(async (valid) => {
    if (!valid) return;
    await $api.post('/email/send', {
      subject: form.value.subject,
      content: form.value.content,
      recipients: form.value.recipients,
    });
    ElMessage.success(t('email.send') + ' success');
  });
}

async function sendTestEmail() {
  await $api.post('/email/test', {
    subject: form.value.subject,
    content: form.value.content,
  });
  ElMessage.success(t('email.test') + ' success');
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
</style>
