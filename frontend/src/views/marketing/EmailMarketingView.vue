<template>
  <div class="email-marketing">
    <el-form ref="formRef" :model="form" label-width="100px">
      <el-form-item :label="t('email.subject')" prop="subject" :rules="[{ required: true, message: t('email.requiredField') }]"><el-input v-model="form.subject" /></el-form-item>
      <el-form-item :label="t('email.content')" prop="content" :rules="[{ required: true, message: t('email.requiredField') }]"><EmailEditor v-model="form.content" /></el-form-item>
      <el-form-item :label="t('email.recipients')" prop="recipients" :rules="[{ type:'array', required: true, message: t('email.requiredField') }]"><RecipientUploader v-model="form.recipients" /></el-form-item>
      <el-form-item>
        <el-button type="primary" @click="send">{{ t('email.send') }}</el-button>
        <el-button @click="sendTest">{{ t('email.test') }}</el-button>
      </el-form-item>
    </el-form>
    <h3 style="margin-top:30px">{{ t('email.history') }}</h3>
    <EmailHistory />
  </div>
</template>
<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useI18n } from 'vue-i18n'
import EmailEditor from '@/components/email/EmailEditor.vue'
import RecipientUploader from '@/components/email/RecipientUploader.vue'
import EmailHistory from '@/components/email/EmailHistory.vue'
import { sendEmail, sendTestEmail } from '@/api/email'

const { t } = useI18n()
const formRef = ref()
const form = ref({ subject: '', content: '', recipients: [] })

async function send() {
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    await sendEmail({ title: form.value.subject, html: form.value.content, recipients: form.value.recipients })
    ElMessage.success(t('email.send') + ' success')
  })
}
async function sendTest() {
  await sendTestEmail({ title: form.value.subject, html: form.value.content })
  ElMessage.success(t('email.test') + ' success')
}
</script>
<style scoped>
.email-marketing {
  padding: 24px;
}
</style>
