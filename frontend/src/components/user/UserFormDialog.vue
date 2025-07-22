<template>
  <el-dialog v-model="visible" :title="mode === 'create' ? '新增用户' : '编辑用户'" width="500px">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
      <el-form-item label="用户名" prop="username">
        <el-input v-model="form.username" />
      </el-form-item>
      <el-form-item label="邮箱" prop="email">
        <el-input v-model="form.email" />
      </el-form-item>
      <el-form-item label="密码" prop="password" v-if="mode === 'create'">
        <el-input type="password" v-model="form.password" />
      </el-form-item>
      <el-form-item label="状态">
        <el-switch v-model="form.status" inline-prompt active-text="启" inactive-text="禁" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" :loading="loading" @click="submit">提交</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { createUser, updateUser } from '../../api/user'

const props = defineProps({
  mode: { type: String, default: 'create' }
})
const emit = defineEmits(['saved'])

const visible = ref(false)
const loading = ref(false)
const formRef = ref()
const form = reactive({
  username: '',
  email: '',
  password: '',
  status: true
})
const editId = ref(null)

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ],
  password: props.mode === 'create'
    ? [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, message: '密码不少于6位', trigger: 'blur' }
      ]
    : []
}

function open(data = null) {
  visible.value = true
  if (props.mode === 'edit' && data) {
    editId.value = data.id
    Object.assign(form, {
      username: data.username,
      email: data.email,
      status: data.status,
      password: ''
    })
  } else {
    editId.value = null
    Object.assign(form, { username: '', email: '', password: '', status: true })
  }
}

function submit() {
  formRef.value.validate(valid => {
    if (!valid) return
    loading.value = true
    const payload = {
      username: form.username,
      email: form.email,
      status: form.status
    }
    const req = props.mode === 'create'
      ? createUser({ ...payload, password: form.password })
      : updateUser(editId.value, payload)
    req
      .then(() => {
        ElMessage.success(props.mode === 'create' ? '新增成功' : '修改成功')
        visible.value = false
        emit('saved')
      })
      .finally(() => (loading.value = false))
  })
}

defineExpose({ open })
</script>

<style scoped>
</style>

