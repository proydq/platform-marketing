<template>
  <div class="crawl-task-form">
    <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
      <el-form-item :label="t('crawl.taskName')" prop="name">
        <el-input v-model="form.name" :placeholder="t('crawl.taskNamePlaceholder')" />
      </el-form-item>
      
      <el-form-item :label="t('crawl.sourceType')" prop="sourceType">
        <el-select v-model="form.sourceType" :placeholder="t('crawl.selectSourceType')" @change="handleSourceTypeChange">
          <el-option :label="t('crawl.website')" value="website" />
          <el-option :label="t('crawl.socialMedia')" value="social" />
          <el-option :label="t('crawl.directory')" value="directory" />
          <el-option :label="t('crawl.exhibition')" value="exhibition" />
        </el-select>
      </el-form-item>
      
      <el-form-item :label="t('crawl.targetUrl')" prop="url">
        <el-input v-model="form.url" :placeholder="t('crawl.urlPlaceholder')" />
      </el-form-item>
      
      <el-form-item :label="t('crawl.crawlDepth')" prop="depth">
        <el-input-number 
          v-model="form.depth" 
          :min="1" 
          :max="5" 
          :placeholder="t('crawl.depthPlaceholder')"
        />
      </el-form-item>
      
      <el-form-item :label="t('crawl.dataFields')" prop="fields">
        <el-checkbox-group v-model="form.fields">
          <el-checkbox label="companyName">{{ t('crawl.companyName') }}</el-checkbox>
          <el-checkbox label="contactPerson">{{ t('crawl.contactPerson') }}</el-checkbox>
          <el-checkbox label="email">{{ t('crawl.email') }}</el-checkbox>
          <el-checkbox label="phone">{{ t('crawl.phone') }}</el-checkbox>
          <el-checkbox label="address">{{ t('crawl.address') }}</el-checkbox>
          <el-checkbox label="industry">{{ t('crawl.industry') }}</el-checkbox>
          <el-checkbox label="website">{{ t('crawl.website') }}</el-checkbox>
          <el-checkbox label="description">{{ t('crawl.description') }}</el-checkbox>
        </el-checkbox-group>
      </el-form-item>
      
      <el-form-item :label="t('crawl.filters')" prop="filters">
        <el-input 
          v-model="form.filters" 
          type="textarea"
          :rows="3"
          :placeholder="t('crawl.filtersPlaceholder')"
        />
      </el-form-item>
      
      <el-form-item :label="t('crawl.schedule')" prop="schedule">
        <el-radio-group v-model="form.schedule">
          <el-radio label="immediate">{{ t('crawl.immediate') }}</el-radio>
          <el-radio label="scheduled">{{ t('crawl.scheduled') }}</el-radio>
          <el-radio label="recurring">{{ t('crawl.recurring') }}</el-radio>
        </el-radio-group>
      </el-form-item>
      
      <el-form-item v-if="form.schedule === 'scheduled'" :label="t('crawl.scheduledTime')">
        <el-date-picker
          v-model="form.scheduledTime"
          type="datetime"
          :placeholder="t('crawl.selectDateTime')"
          format="YYYY-MM-DD HH:mm:ss"
          value-format="YYYY-MM-DD HH:mm:ss"
        />
      </el-form-item>
      
      <el-form-item v-if="form.schedule === 'recurring'" :label="t('crawl.frequency')">
        <el-select v-model="form.frequency" :placeholder="t('crawl.selectFrequency')">
          <el-option :label="t('crawl.daily')" value="daily" />
          <el-option :label="t('crawl.weekly')" value="weekly" />
          <el-option :label="t('crawl.monthly')" value="monthly" />
        </el-select>
      </el-form-item>
      
      <el-form-item>
        <el-button type="primary" @click="submitForm" :loading="submitting">
          {{ t('crawl.createTask') }}
        </el-button>
        <el-button @click="resetForm">
          {{ t('crawl.reset') }}
        </el-button>
        <el-button @click="testCrawl" :loading="testing">
          {{ t('crawl.testCrawl') }}
        </el-button>
      </el-form-item>
    </el-form>
    
    <!-- 测试结果对话框 -->
    <el-dialog 
      v-model="testDialogVisible" 
      :title="t('crawl.testResults')"
      width="70%"
    >
      <div v-if="testResults.length > 0">
        <el-table :data="testResults" style="width: 100%">
          <el-table-column 
            v-for="field in form.fields" 
            :key="field"
            :prop="field"
            :label="t(`crawl.${field}`)"
            show-overflow-tooltip
          />
        </el-table>
        <div class="test-info">
          <p>{{ t('crawl.testResultInfo', { count: testResults.length }) }}</p>
        </div>
      </div>
      <div v-else class="no-test-data">
        <p>{{ t('crawl.noTestData') }}</p>
      </div>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="testDialogVisible = false">{{ t('common.close') }}</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, defineEmits } from 'vue'
import { ElMessage } from 'element-plus'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()
const emit = defineEmits(['submit'])

const formRef = ref()
const submitting = ref(false)
const testing = ref(false)
const testDialogVisible = ref(false)
const testResults = ref([])

const form = reactive({
  name: '',
  sourceType: '',
  url: '',
  depth: 1,
  fields: ['companyName', 'email'],
  filters: '',
  schedule: 'immediate',
  scheduledTime: null,
  frequency: ''
})

const rules = reactive({
  name: [
    { required: true, message: t('crawl.nameRequired'), trigger: 'blur' }
  ],
  sourceType: [
    { required: true, message: t('crawl.sourceTypeRequired'), trigger: 'change' }
  ],
  url: [
    { required: true, message: t('crawl.urlRequired'), trigger: 'blur' },
    { type: 'url', message: t('crawl.urlFormat'), trigger: 'blur' }
  ],
  fields: [
    { type: 'array', min: 1, message: t('crawl.fieldsRequired'), trigger: 'change' }
  ]
})

const handleSourceTypeChange = (value) => {
  // 根据数据源类型预设字段
  if (value === 'social') {
    form.fields = ['companyName', 'contactPerson', 'email', 'website']
  } else if (value === 'directory') {
    form.fields = ['companyName', 'email', 'phone', 'address', 'industry']
  } else if (value === 'exhibition') {
    form.fields = ['companyName', 'contactPerson', 'email', 'phone', 'website']
  } else {
    form.fields = ['companyName', 'email']
  }
}

const submitForm = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    submitting.value = true
    
    // 模拟提交延迟
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    emit('submit', { ...form })
    ElMessage.success(t('crawl.taskCreated'))
    resetForm()
    
  } catch (error) {
    console.log('Validation failed:', error)
  } finally {
    submitting.value = false
  }
}

const resetForm = () => {
  if (!formRef.value) return
  formRef.value.resetFields()
  form.scheduledTime = null
  form.frequency = ''
}

const testCrawl = async () => {
  if (!form.url) {
    ElMessage.warning(t('crawl.enterUrlFirst'))
    return
  }
  
  testing.value = true
  
  try {
    // 模拟爬虫测试
    await new Promise(resolve => setTimeout(resolve, 2000))
    
    // 生成模拟测试数据
    testResults.value = [
      {
        companyName: 'ABC Trading Co.',
        contactPerson: 'John Smith',
        email: 'john@abctrading.com',
        phone: '+1-555-0123',
        address: '123 Business St, NY',
        industry: 'Import/Export',
        website: 'www.abctrading.com',
        description: 'Leading trading company specializing in electronics'
      },
      {
        companyName: 'Global Tech Solutions',
        contactPerson: 'Sarah Johnson',
        email: 'sarah@globaltech.com',
        phone: '+1-555-0456',
        address: '456 Tech Ave, CA',
        industry: 'Technology',
        website: 'www.globaltech.com',
        description: 'Innovative technology solutions provider'
      },
      {
        companyName: 'International Logistics',
        contactPerson: 'Mike Wilson',
        email: 'mike@intllogistics.com',
        phone: '+1-555-0789',
        address: '789 Logistics Blvd, TX',
        industry: 'Logistics',
        website: 'www.intllogistics.com',
        description: 'Comprehensive logistics and shipping services'
      }
    ]
    
    testDialogVisible.value = true
    ElMessage.success(t('crawl.testCompleted'))
    
  } catch (error) {
    ElMessage.error(t('crawl.testFailed'))
    console.error('Test crawl error:', error)
  } finally {
    testing.value = false
  }
}
</script>

<style scoped>
.crawl-task-form {
  max-width: 800px;
}

.test-info {
  margin-top: 16px;
  padding: 12px;
  background: #f0f9ff;
  border-radius: 4px;
  color: #0369a1;
}

.no-test-data {
  text-align: center;
  padding: 40px;
  color: #6b7280;
}

.dialog-footer {
  display: flex;
  gap: 12px;
}
</style>