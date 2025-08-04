<template>
  <div class="email-editor">
    <el-upload
      class="upload-btn"
      :show-file-list="false"
      :http-request="handleUpload"
    >
      <el-button type="primary">{{ t('email.insertImage') }}</el-button>
    </el-upload>
    <QuillEditor
      ref="editorRef"
      v-model:content="content"
      content-type="html"
    />
  </div>
</template>
<script setup>
import { ref, watch } from 'vue'
import { QuillEditor } from 'vue3-quill'
import 'vue3-quill/lib/style.css'
import { uploadImage } from '@/api/email'
import { ElMessage } from 'element-plus'
import { useI18n } from 'vue-i18n'

const props = defineProps({
  modelValue: { type: String, default: '' }
})
const emit = defineEmits(['update:modelValue'])
const { t } = useI18n()

const content = ref(props.modelValue)
watch(() => props.modelValue, v => (content.value = v))
watch(content, v => emit('update:modelValue', v))

const editorRef = ref()

async function handleUpload({ file }) {
  const form = new FormData()
  form.append('file', file)
  try {
    const res = await uploadImage(form)
    const url = res.data?.url || res.url
    const quill = editorRef.value?.getQuill()
    const range = quill.getSelection(true)
    quill.insertEmbed(range.index, 'image', url)
    quill.setSelection(range.index + 1)
  } catch (e) {
    ElMessage.error('upload failed')
  }
}
</script>
<style scoped>
.email-editor {
  width: 100%;
}
.upload-btn {
  margin-bottom: 10px;
}
</style>
