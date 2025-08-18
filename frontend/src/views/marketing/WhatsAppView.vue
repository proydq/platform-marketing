<template>
  <div class="whatsapp-container">
    <div class="page-header">
      <h1>{{ t('whatsapp.title') }}</h1>
      <p>{{ t('whatsapp.subtitle') }}</p>
    </div>

    <!-- 账号配置 -->
    <el-card class="account-config">
      <template #header>
        <div class="card-header">
          <span>{{ t('whatsapp.accountConfig') }}</span>
          <el-button type="primary" @click="showAccountDialog = true">
            {{ t('whatsapp.addAccount') }}
          </el-button>
        </div>
      </template>
      
      <el-table :data="whatsappAccounts" style="width: 100%">
        <el-table-column prop="name" :label="t('whatsapp.accountName')" />
        <el-table-column prop="phoneNumber" :label="t('whatsapp.phoneNumber')" />
        <el-table-column prop="businessName" :label="t('whatsapp.businessName')" />
        <el-table-column prop="status" :label="t('common.status')" width="120">
          <template #default="{ row }">
            <el-tag :type="row.status === 'active' ? 'success' : 'danger'">
              {{ t(`whatsapp.status.${row.status}`) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="lastSync" :label="t('whatsapp.lastSync')" width="160" />
        <el-table-column :label="t('common.actions')" width="200">
          <template #default="{ row }">
            <el-button type="text" @click="testConnection(row)">
              {{ t('whatsapp.testConnection') }}
            </el-button>
            <el-button type="text" @click="editAccount(row)">
              {{ t('common.edit') }}
            </el-button>
            <el-button type="text" style="color: #f56c6c" @click="deleteAccount(row)">
              {{ t('common.delete') }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 消息推送 -->
    <el-card class="message-push">
      <template #header>
        <div class="card-header">
          <span>{{ t('whatsapp.messagePush') }}</span>
        </div>
      </template>
      
      <el-form :model="messageForm" label-width="120px" class="message-form">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item :label="t('whatsapp.selectAccount')">
              <el-select v-model="messageForm.accountId" :placeholder="t('whatsapp.selectAccountPlaceholder')">
                <el-option 
                  v-for="account in activeAccounts" 
                  :key="account.id"
                  :label="account.name"
                  :value="account.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="t('whatsapp.messageType')">
              <el-select v-model="messageForm.type" :placeholder="t('whatsapp.selectMessageType')">
                <el-option :label="t('whatsapp.textMessage')" value="text" />
                <el-option :label="t('whatsapp.imageMessage')" value="image" />
                <el-option :label="t('whatsapp.documentMessage')" value="document" />
                <el-option :label="t('whatsapp.templateMessage')" value="template" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item :label="t('whatsapp.recipients')">
          <div class="recipients-section">
            <el-input 
              v-model="messageForm.recipients" 
              type="textarea"
              :rows="3"
              :placeholder="t('whatsapp.recipientsPlaceholder')"
            />
            <div class="upload-section">
              <el-upload
                :auto-upload="false"
                :on-change="handleRecipientsFile"
                accept=".csv,.xlsx,.xls"
                :show-file-list="false"
              >
                <el-button type="text">
                  {{ t('whatsapp.uploadRecipients') }}
                </el-button>
              </el-upload>
              <el-button type="text" @click="selectFromCustomers">
                {{ t('whatsapp.selectFromCustomers') }}
              </el-button>
            </div>
          </div>
        </el-form-item>
        
        <el-form-item v-if="messageForm.type === 'text'" :label="t('whatsapp.messageContent')">
          <el-input 
            v-model="messageForm.content" 
            type="textarea"
            :rows="4"
            :placeholder="t('whatsapp.messageContentPlaceholder')"
            maxlength="4096"
            show-word-limit
          />
        </el-form-item>
        
        <el-form-item v-if="messageForm.type === 'image'" :label="t('whatsapp.imageUpload')">
          <el-upload
            v-model:file-list="messageForm.images"
            :auto-upload="false"
            list-type="picture-card"
            accept="image/*"
            :limit="1"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
          <el-input 
            v-model="messageForm.caption" 
            :placeholder="t('whatsapp.imageCaption')"
            style="margin-top: 8px"
          />
        </el-form-item>
        
        <el-form-item v-if="messageForm.type === 'document'" :label="t('whatsapp.documentUpload')">
          <el-upload
            v-model:file-list="messageForm.documents"
            :auto-upload="false"
            :limit="1"
          >
            <el-button>{{ t('whatsapp.selectDocument') }}</el-button>
          </el-upload>
        </el-form-item>
        
        <el-form-item v-if="messageForm.type === 'template'" :label="t('whatsapp.selectTemplate')">
          <el-select v-model="messageForm.templateId" :placeholder="t('whatsapp.selectTemplatePlaceholder')">
            <el-option 
              v-for="template in messageTemplates" 
              :key="template.id"
              :label="template.name"
              :value="template.id"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item :label="t('whatsapp.sendOptions')">
          <el-checkbox v-model="messageForm.scheduleEnabled">
            {{ t('whatsapp.scheduleMessage') }}
          </el-checkbox>
          <el-date-picker
            v-if="messageForm.scheduleEnabled"
            v-model="messageForm.scheduledTime"
            type="datetime"
            :placeholder="t('whatsapp.selectScheduleTime')"
            style="margin-left: 12px"
          />
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="sendMessage" :loading="sending" size="large">
            {{ messageForm.scheduleEnabled ? t('whatsapp.scheduleNow') : t('whatsapp.sendNow') }}
          </el-button>
          <el-button @click="previewMessage">
            {{ t('whatsapp.preview') }}
          </el-button>
          <el-button @click="resetMessageForm">
            {{ t('common.reset') }}
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 发送记录 -->
    <el-card class="send-history">
      <template #header>
        <div class="card-header">
          <span>{{ t('whatsapp.sendHistory') }}</span>
          <div class="header-actions">
            <el-input 
              v-model="historySearch" 
              :placeholder="t('whatsapp.searchHistory')"
              style="width: 200px"
            />
            <el-select v-model="statusFilter" :placeholder="t('whatsapp.filterByStatus')" style="width: 120px">
              <el-option :label="t('common.all')" value="" />
              <el-option :label="t('whatsapp.status.sent')" value="sent" />
              <el-option :label="t('whatsapp.status.delivered')" value="delivered" />
              <el-option :label="t('whatsapp.status.read')" value="read" />
              <el-option :label="t('whatsapp.status.failed')" value="failed" />
            </el-select>
          </div>
        </div>
      </template>
      
      <el-table :data="filteredHistory" style="width: 100%">
        <el-table-column prop="id" :label="t('whatsapp.messageId')" width="100" />
        <el-table-column prop="recipient" :label="t('whatsapp.recipient')" width="150" />
        <el-table-column prop="type" :label="t('whatsapp.type')" width="100">
          <template #default="{ row }">
            <el-tag>{{ t(`whatsapp.${row.type}Message`) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="content" :label="t('whatsapp.content')" min-width="200" show-overflow-tooltip />
        <el-table-column prop="status" :label="t('common.status')" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ t(`whatsapp.status.${row.status}`) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sentAt" :label="t('whatsapp.sentTime')" width="160" />
        <el-table-column :label="t('common.actions')" width="150">
          <template #default="{ row }">
            <el-button type="text" @click="viewMessage(row)">
              {{ t('common.view') }}
            </el-button>
            <el-button v-if="row.status === 'failed'" type="text" @click="resendMessage(row)">
              {{ t('whatsapp.resend') }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 账号配置对话框 -->
    <el-dialog 
      v-model="showAccountDialog" 
      :title="editingAccount ? t('whatsapp.editAccount') : t('whatsapp.addAccount')"
      width="50%"
    >
      <el-form :model="accountForm" :rules="accountRules" ref="accountFormRef" label-width="120px">
        <el-form-item :label="t('whatsapp.accountName')" prop="name">
          <el-input v-model="accountForm.name" :placeholder="t('whatsapp.accountNamePlaceholder')" />
        </el-form-item>
        <el-form-item :label="t('whatsapp.phoneNumber')" prop="phoneNumber">
          <el-input v-model="accountForm.phoneNumber" :placeholder="t('whatsapp.phoneNumberPlaceholder')" />
        </el-form-item>
        <el-form-item :label="t('whatsapp.businessName')" prop="businessName">
          <el-input v-model="accountForm.businessName" :placeholder="t('whatsapp.businessNamePlaceholder')" />
        </el-form-item>
        <el-form-item :label="t('whatsapp.accessToken')" prop="accessToken">
          <el-input 
            v-model="accountForm.accessToken" 
            type="password"
            :placeholder="t('whatsapp.accessTokenPlaceholder')"
          />
        </el-form-item>
        <el-form-item :label="t('whatsapp.phoneNumberId')" prop="phoneNumberId">
          <el-input v-model="accountForm.phoneNumberId" :placeholder="t('whatsapp.phoneNumberIdPlaceholder')" />
        </el-form-item>
        <el-form-item :label="t('whatsapp.businessAccountId')" prop="businessAccountId">
          <el-input v-model="accountForm.businessAccountId" :placeholder="t('whatsapp.businessAccountIdPlaceholder')" />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showAccountDialog = false">{{ t('common.cancel') }}</el-button>
          <el-button type="primary" @click="saveAccount">{{ t('common.confirm') }}</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 客户选择对话框 -->
    <el-dialog 
      v-model="showCustomerDialog" 
      :title="t('whatsapp.selectCustomers')"
      width="70%"
    >
      <el-table 
        :data="customers" 
        @selection-change="handleCustomerSelection"
        style="width: 100%"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="name" :label="t('customer.name')" />
        <el-table-column prop="phone" :label="t('customer.phone')" />
        <el-table-column prop="company" :label="t('customer.company')" />
        <el-table-column prop="source" :label="t('customer.source')" />
      </el-table>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showCustomerDialog = false">{{ t('common.cancel') }}</el-button>
          <el-button type="primary" @click="confirmCustomerSelection">
            {{ t('whatsapp.confirmSelection') }}
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { useI18n } from 'vue-i18n'
import * as XLSX from 'xlsx'

const { t } = useI18n()

// 状态变量
const showAccountDialog = ref(false)
const showCustomerDialog = ref(false)
const editingAccount = ref(null)
const sending = ref(false)
const accountFormRef = ref()

// 筛选变量
const historySearch = ref('')
const statusFilter = ref('')

// WhatsApp账号数据
const whatsappAccounts = ref([
  {
    id: 1,
    name: '主营销账号',
    phoneNumber: '+1234567890',
    businessName: '我的企业',
    status: 'active',
    lastSync: '2024-01-15 10:30:00'
  },
  {
    id: 2,
    name: '客服账号',
    phoneNumber: '+1234567891',
    businessName: '客服中心',
    status: 'inactive',
    lastSync: '2024-01-14 15:20:00'
  }
])

// 消息模板
const messageTemplates = ref([
  { id: 1, name: '产品推广模板', content: '您好！我们的新产品已上市，欢迎了解...' },
  { id: 2, name: '客户关怀模板', content: '感谢您的支持，祝您生活愉快！' },
  { id: 3, name: '促销活动模板', content: '限时优惠！现在购买享受8折优惠...' }
])

// 发送历史
const sendHistory = ref([
  {
    id: 'MSG001',
    recipient: '+1234567890',
    type: 'text',
    content: '您好！我们的新产品已上市...',
    status: 'delivered',
    sentAt: '2024-01-15 10:30:00'
  },
  {
    id: 'MSG002',
    recipient: '+1234567891',
    type: 'image',
    content: '产品图片',
    status: 'read',
    sentAt: '2024-01-15 11:15:00'
  },
  {
    id: 'MSG003',
    recipient: '+1234567892',
    type: 'template',
    content: '促销活动通知',
    status: 'failed',
    sentAt: '2024-01-15 12:00:00'
  }
])

// 客户数据
const customers = ref([
  { id: 1, name: 'John Smith', phone: '+1234567890', company: 'ABC Corp', source: 'LinkedIn' },
  { id: 2, name: 'Sarah Johnson', phone: '+1234567891', company: 'Tech Solutions', source: 'Website' },
  { id: 3, name: 'Mike Wilson', phone: '+1234567892', company: 'Global Trade', source: 'Exhibition' }
])

const selectedCustomers = ref([])

// 表单数据
const accountForm = reactive({
  name: '',
  phoneNumber: '',
  businessName: '',
  accessToken: '',
  phoneNumberId: '',
  businessAccountId: ''
})

const messageForm = reactive({
  accountId: '',
  type: 'text',
  recipients: '',
  content: '',
  images: [],
  documents: [],
  caption: '',
  templateId: '',
  scheduleEnabled: false,
  scheduledTime: null
})

// 表单验证规则
const accountRules = reactive({
  name: [
    { required: true, message: t('whatsapp.accountNameRequired'), trigger: 'blur' }
  ],
  phoneNumber: [
    { required: true, message: t('whatsapp.phoneNumberRequired'), trigger: 'blur' }
  ],
  accessToken: [
    { required: true, message: t('whatsapp.accessTokenRequired'), trigger: 'blur' }
  ]
})

// 计算属性
const activeAccounts = computed(() => {
  return whatsappAccounts.value.filter(account => account.status === 'active')
})

const filteredHistory = computed(() => {
  return sendHistory.value.filter(record => {
    const searchMatch = !historySearch.value || 
      record.recipient.includes(historySearch.value) ||
      record.content.includes(historySearch.value)
    const statusMatch = !statusFilter.value || record.status === statusFilter.value
    return searchMatch && statusMatch
  })
})

// 方法
const testConnection = async (account) => {
  try {
    ElMessage.info(t('whatsapp.testing'))
    // 模拟测试连接
    await new Promise(resolve => setTimeout(resolve, 2000))
    ElMessage.success(t('whatsapp.connectionSuccess'))
  } catch (error) {
    ElMessage.error(t('whatsapp.connectionFailed'))
  }
}

const editAccount = (account) => {
  editingAccount.value = account
  Object.assign(accountForm, account)
  showAccountDialog.value = true
}

const deleteAccount = async (account) => {
  try {
    await ElMessageBox.confirm(
      t('whatsapp.deleteAccountConfirm'),
      t('common.warning'),
      {
        confirmButtonText: t('common.confirm'),
        cancelButtonText: t('common.cancel'),
        type: 'warning'
      }
    )
    
    const index = whatsappAccounts.value.findIndex(item => item.id === account.id)
    if (index > -1) {
      whatsappAccounts.value.splice(index, 1)
      ElMessage.success(t('common.deleteSuccess'))
    }
  } catch {
    // 用户取消删除
  }
}

const saveAccount = async () => {
  if (!accountFormRef.value) return
  
  try {
    await accountFormRef.value.validate()
    
    if (editingAccount.value) {
      // 编辑账号
      const index = whatsappAccounts.value.findIndex(item => item.id === editingAccount.value.id)
      if (index > -1) {
        whatsappAccounts.value[index] = { ...whatsappAccounts.value[index], ...accountForm }
      }
      ElMessage.success(t('common.updateSuccess'))
    } else {
      // 新增账号
      const newAccount = {
        id: Date.now(),
        ...accountForm,
        status: 'active',
        lastSync: new Date().toLocaleString()
      }
      whatsappAccounts.value.push(newAccount)
      ElMessage.success(t('common.saveSuccess'))
    }
    
    showAccountDialog.value = false
    resetAccountForm()
  } catch (error) {
    console.log('Validation failed:', error)
  }
}

const resetAccountForm = () => {
  Object.assign(accountForm, {
    name: '',
    phoneNumber: '',
    businessName: '',
    accessToken: '',
    phoneNumberId: '',
    businessAccountId: ''
  })
  editingAccount.value = null
}

const handleRecipientsFile = (file) => {
  const reader = new FileReader()
  reader.onload = (e) => {
    try {
      const data = e.target.result
      let phoneNumbers = []
      
      if (file.name.endsWith('.csv')) {
        const lines = data.split('\n')
        phoneNumbers = lines.map(line => line.trim()).filter(line => line)
      } else {
        const workbook = XLSX.read(data, { type: 'array' })
        const firstSheetName = workbook.SheetNames[0]
        const worksheet = workbook.Sheets[firstSheetName]
        const jsonData = XLSX.utils.sheet_to_json(worksheet, { header: 1 })
        phoneNumbers = jsonData.flat().filter(item => item)
      }
      
      messageForm.recipients = phoneNumbers.join('\n')
      ElMessage.success(t('whatsapp.recipientsImported'))
    } catch (error) {
      ElMessage.error(t('whatsapp.importFailed'))
    }
  }
  
  if (file.name.endsWith('.csv')) {
    reader.readAsText(file.raw)
  } else {
    reader.readAsArrayBuffer(file.raw)
  }
}

const selectFromCustomers = () => {
  showCustomerDialog.value = true
}

const handleCustomerSelection = (selection) => {
  selectedCustomers.value = selection
}

const confirmCustomerSelection = () => {
  const phoneNumbers = selectedCustomers.value
    .filter(customer => customer.phone)
    .map(customer => customer.phone)
  
  if (phoneNumbers.length > 0) {
    messageForm.recipients = phoneNumbers.join('\n')
    ElMessage.success(t('whatsapp.customersSelected', { count: phoneNumbers.length }))
  }
  
  showCustomerDialog.value = false
  selectedCustomers.value = []
}

const sendMessage = async () => {
  if (!messageForm.accountId || !messageForm.recipients) {
    ElMessage.warning(t('whatsapp.fillRequiredFields'))
    return
  }
  
  sending.value = true
  
  try {
    // 模拟发送延迟
    await new Promise(resolve => setTimeout(resolve, 2000))
    
    // 添加到发送历史
    const recipients = messageForm.recipients.split('\n').filter(r => r.trim())
    recipients.forEach(recipient => {
      const newRecord = {
        id: 'MSG' + Date.now(),
        recipient: recipient.trim(),
        type: messageForm.type,
        content: messageForm.content || messageForm.caption || t('whatsapp.mediaMessage'),
        status: 'sent',
        sentAt: new Date().toLocaleString()
      }
      sendHistory.value.unshift(newRecord)
    })
    
    ElMessage.success(
      messageForm.scheduleEnabled 
        ? t('whatsapp.scheduleSuccess') 
        : t('whatsapp.sendSuccess')
    )
    
    resetMessageForm()
  } catch (error) {
    ElMessage.error(t('whatsapp.sendFailed'))
  } finally {
    sending.value = false
  }
}

const previewMessage = () => {
  if (!messageForm.content && !messageForm.caption) {
    ElMessage.warning(t('whatsapp.noContentToPreview'))
    return
  }
  
  const content = messageForm.content || messageForm.caption || t('whatsapp.mediaMessage')
  ElMessageBox.alert(content, t('whatsapp.messagePreview'), {
    customClass: 'whatsapp-preview-dialog'
  })
}

const resetMessageForm = () => {
  Object.assign(messageForm, {
    accountId: '',
    type: 'text',
    recipients: '',
    content: '',
    images: [],
    documents: [],
    caption: '',
    templateId: '',
    scheduleEnabled: false,
    scheduledTime: null
  })
}

const viewMessage = (message) => {
  ElMessageBox.alert(message.content, t('whatsapp.messageDetail'), {
    customClass: 'whatsapp-detail-dialog'
  })
}

const resendMessage = async (message) => {
  try {
    await ElMessageBox.confirm(
      t('whatsapp.resendConfirm'),
      t('whatsapp.resend'),
      {
        confirmButtonText: t('common.confirm'),
        cancelButtonText: t('common.cancel'),
        type: 'info'
      }
    )
    
    // 模拟重新发送
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    // 更新状态
    message.status = 'sent'
    message.sentAt = new Date().toLocaleString()
    
    ElMessage.success(t('whatsapp.resendSuccess'))
  } catch {
    // 用户取消重发
  }
}

const getStatusType = (status) => {
  const typeMap = {
    sent: 'info',
    delivered: 'success',
    read: 'success',
    failed: 'danger'
  }
  return typeMap[status] || 'info'
}

onMounted(() => {
  // 初始化数据
})
</script>

<style scoped>
.whatsapp-container {
  padding: 20px;
}

.page-header {
  margin-bottom: 24px;
}

.page-header h1 {
  margin: 0 0 8px 0;
  color: #1f2937;
}

.page-header p {
  margin: 0;
  color: #6b7280;
}

.account-config, .message-push, .send-history {
  margin-bottom: 24px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
}

.header-actions {
  display: flex;
  gap: 12px;
  align-items: center;
}

.message-form {
  padding: 20px;
}

.recipients-section .upload-section {
  margin-top: 8px;
  display: flex;
  gap: 12px;
}

.dialog-footer {
  display: flex;
  gap: 12px;
}

:deep(.whatsapp-preview-dialog) {
  max-width: 400px;
}

:deep(.whatsapp-detail-dialog) {
  max-width: 500px;
}
</style>