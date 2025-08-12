<template>
  <el-dialog 
    v-model="visible" 
    :title="isEdit ? $t('customer.edit') : $t('customer.addCustomer')" 
    width="600px"
    :destroy-on-close="true"
    @close="handleClose"
  >
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="100px"
      label-position="left"
    >
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item :label="$t('customer.name')" prop="name">
            <el-input
              v-model="formData.name"
              :placeholder="$t('customer.name')"
              clearable
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="$t('customer.email')" prop="email">
            <el-input
              v-model="formData.email"
              :placeholder="$t('customer.email')"
              clearable
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item :label="$t('customer.phone')" prop="phone">
            <el-input
              v-model="formData.phone"
              :placeholder="$t('customer.phone')"
              clearable
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="$t('customer.company')" prop="company">
            <el-input
              v-model="formData.company"
              :placeholder="$t('customer.company')"
              clearable
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item :label="$t('customer.source')" prop="source">
            <el-select
              v-model="formData.source"
              :placeholder="$t('customer.source')"
              clearable
              style="width: 100%"
            >
              <el-option
                v-for="source in sourceOptions"
                :key="source"
                :label="source"
                :value="source"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="$t('customer.status')" prop="status">
            <el-select
              v-model="formData.status"
              :placeholder="$t('customer.status')"
              style="width: 100%"
            >
              <el-option :label="$t('customer.statusActive')" value="active" />
              <el-option :label="$t('customer.statusInactive')" value="inactive" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item :label="$t('customer.remark')" prop="remark">
        <el-input
          v-model="formData.remark"
          type="textarea"
          :rows="3"
          :placeholder="$t('customer.remark')"
        />
      </el-form-item>
    </el-form>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">{{ $t('common.cancel') }}</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">
          {{ $t('common.confirm') }}
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, computed, watch, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useI18n } from 'vue-i18n'
import { useCustomerStore } from '@/stores/modules/customer'

const { t } = useI18n()
const customerStore = useCustomerStore()

// Props
const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  customer: {
    type: Object,
    default: null
  },
  isEdit: {
    type: Boolean,
    default: false
  }
})

// Emits
const emit = defineEmits(['update:modelValue', 'success'])

// 响应式数据
const formRef = ref()
const submitting = ref(false)

const visible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

const formData = reactive({
  id: '',
  name: '',
  email: '',
  phone: '',
  company: '',
  source: '',
  status: 'active',
  remark: ''
})

const formRules = {
  name: [
    { required: true, message: t('customer.nameRequired'), trigger: 'blur' },
    { min: 2, max: 50, message: t('customer.nameLength'), trigger: 'blur' }
  ],
  email: [
    { required: true, message: t('customer.emailRequired'), trigger: 'blur' },
    { type: 'email', message: t('customer.emailInvalid'), trigger: 'blur' }
  ],
  phone: [
    { pattern: /^[+]?[\d\s\-()]+$/, message: t('customer.phoneInvalid'), trigger: 'blur' }
  ],
  status: [
    { required: true, message: t('customer.statusRequired'), trigger: 'change' }
  ]
}

const sourceOptions = computed(() => customerStore.sourcesOptions)

// 监听器
watch(() => props.customer, (newCustomer) => {
  if (newCustomer && props.isEdit) {
    Object.assign(formData, {
      id: newCustomer.id || '',
      name: newCustomer.name || '',
      email: newCustomer.email || '',
      phone: newCustomer.phone || '',
      company: newCustomer.company || '',
      source: newCustomer.source || '',
      status: newCustomer.status || 'active',
      remark: newCustomer.remark || ''
    })
  }
}, { immediate: true })

watch(visible, (newVisible) => {
  if (newVisible && !props.isEdit) {
    resetForm()
  }
})

// 生命周期
onMounted(() => {
  // 确保来源选项已加载
  if (sourceOptions.value.length === 0) {
    customerStore.fetchSourcesOptions()
  }
})

// 方法
const resetForm = () => {
  Object.assign(formData, {
    id: '',
    name: '',
    email: '',
    phone: '',
    company: '',
    source: '',
    status: 'active',
    remark: ''
  })
  if (formRef.value) {
    formRef.value.clearValidate()
  }
}

const handleClose = () => {
  visible.value = false
  resetForm()
}

const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
  } catch (error) {
    return
  }

  submitting.value = true
  try {
    const submitData = { ...formData }
    delete submitData.id

    if (props.isEdit && formData.id) {
      await customerStore.updateCustomer(formData.id, submitData)
      ElMessage.success(t('common.updateSuccess'))
    } else {
      await customerStore.createCustomer(submitData)
      ElMessage.success(t('common.createSuccess'))
    }

    emit('success')
    handleClose()
  } catch (error) {
    const errorMsg = props.isEdit ? t('common.updateError') : t('common.createError')
    ElMessage.error(errorMsg)
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

:deep(.el-form-item__label) {
  font-weight: 500;
  color: #606266;
}

:deep(.el-textarea__inner) {
  resize: vertical;
}
</style>