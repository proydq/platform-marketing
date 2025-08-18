<template>
  <div class="product-import">
    <el-steps :active="currentStep" finish-status="success" align-center>
      <el-step :title="t('product.uploadFile')" />
      <el-step :title="t('product.fieldMapping')" />
      <el-step :title="t('product.importProgress')" />
    </el-steps>

    <!-- 步骤1: 文件上传 -->
    <div v-if="currentStep === 0" class="step-content">
      <div class="upload-section">
        <h3>{{ t('product.selectImportFile') }}</h3>
        <p class="upload-tips">{{ t('product.importTips') }}</p>
        
        <el-upload
          ref="uploadRef"
          v-model:file-list="fileList"
          :auto-upload="false"
          :on-change="handleFileChange"
          :before-upload="beforeUpload"
          :accept="'.xlsx,.xls,.csv'"
          drag
          :limit="1"
        >
          <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
          <div class="el-upload__text">
            {{ t('product.dragOrClickUpload') }}
          </div>
          <template #tip>
            <div class="el-upload__tip">
              {{ t('product.supportedFormats') }}
            </div>
          </template>
        </el-upload>
        
        <div class="template-section">
          <h4>{{ t('product.downloadTemplate') }}</h4>
          <el-button type="primary" link @click="downloadTemplate">
            <el-icon><Download /></el-icon>
            {{ t('product.templateFile') }}
          </el-button>
        </div>
      </div>
      
      <div class="step-actions">
        <el-button @click="$emit('close')">{{ t('common.cancel') }}</el-button>
        <el-button type="primary" @click="nextStep" :disabled="!selectedFile">
          {{ t('common.next') }}
        </el-button>
      </div>
    </div>

    <!-- 步骤2: 字段映射 -->
    <div v-if="currentStep === 1" class="step-content">
      <div class="mapping-section">
        <h3>{{ t('product.fieldMapping') }}</h3>
        <p class="mapping-tips">{{ t('product.mappingTips') }}</p>
        
        <div class="preview-data">
          <h4>{{ t('product.dataPreview') }}</h4>
          <el-table :data="previewData" size="small" max-height="200">
            <el-table-column 
              v-for="(col, index) in previewColumns" 
              :key="index"
              :prop="col" 
              :label="col"
              width="120"
            />
          </el-table>
        </div>
        
        <div class="field-mapping">
          <h4>{{ t('product.mappingRules') }}</h4>
          <div class="mapping-list">
            <div 
              v-for="field in requiredFields" 
              :key="field.key"
              class="mapping-item"
            >
              <div class="field-info">
                <span class="field-name">{{ field.name }}</span>
                <span v-if="field.required" class="required-mark">*</span>
              </div>
              <el-select 
                v-model="fieldMapping[field.key]" 
                :placeholder="t('product.selectColumn')"
                clearable
              >
                <el-option 
                  v-for="col in previewColumns" 
                  :key="col"
                  :label="col" 
                  :value="col" 
                />
              </el-select>
            </div>
          </div>
        </div>
      </div>
      
      <div class="step-actions">
        <el-button @click="prevStep">{{ t('common.previous') }}</el-button>
        <el-button type="primary" @click="startImport" :disabled="!isMappingValid">
          {{ t('product.startImport') }}
        </el-button>
      </div>
    </div>

    <!-- 步骤3: 导入进度 -->
    <div v-if="currentStep === 2" class="step-content">
      <div class="progress-section">
        <h3>{{ t('product.importProgress') }}</h3>
        
        <div class="progress-info">
          <el-progress 
            :percentage="importProgress" 
            :status="importStatus"
            :stroke-width="8"
          />
          <div class="progress-text">
            {{ importMessage }}
          </div>
        </div>
        
        <div v-if="importResult" class="import-result">
          <el-result
            :icon="importResult.success ? 'success' : 'error'"
            :title="importResult.title"
            :sub-title="importResult.message"
          >
            <template #extra>
              <div class="result-stats">
                <div class="stat-item success">
                  <span class="stat-number">{{ importResult.successCount || 0 }}</span>
                  <span class="stat-label">{{ t('product.successCount') }}</span>
                </div>
                <div class="stat-item error">
                  <span class="stat-number">{{ importResult.errorCount || 0 }}</span>
                  <span class="stat-label">{{ t('product.errorCount') }}</span>
                </div>
                <div class="stat-item total">
                  <span class="stat-number">{{ importResult.totalCount || 0 }}</span>
                  <span class="stat-label">{{ t('product.totalCount') }}</span>
                </div>
              </div>
              
              <div v-if="importResult.errors && importResult.errors.length > 0" class="error-list">
                <h4>{{ t('product.errorDetails') }}</h4>
                <el-table :data="importResult.errors" size="small" max-height="200">
                  <el-table-column prop="row" :label="t('product.rowNumber')" width="80" />
                  <el-table-column prop="field" :label="t('product.fieldName')" width="120" />
                  <el-table-column prop="error" :label="t('product.errorMessage')" />
                </el-table>
              </div>
            </template>
          </el-result>
        </div>
      </div>
      
      <div class="step-actions">
        <el-button v-if="!importResult" @click="cancelImport">
          {{ t('common.cancel') }}
        </el-button>
        <el-button v-else @click="resetImport">
          {{ t('product.importAgain') }}
        </el-button>
        <el-button v-if="importResult" type="primary" @click="finishImport">
          {{ t('common.finish') }}
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch } from 'vue';
import { ElMessage } from 'element-plus';
import { useI18n } from 'vue-i18n';
import { UploadFilled, Download } from '@element-plus/icons-vue';
import { importProducts } from '@/api/product';
import * as XLSX from 'xlsx';

const emit = defineEmits(['imported', 'close']);
const { t } = useI18n();

const currentStep = ref(0);
const fileList = ref([]);
const selectedFile = ref(null);
const previewData = ref([]);
const previewColumns = ref([]);
const fieldMapping = reactive({});
const importProgress = ref(0);
const importStatus = ref('');
const importMessage = ref('');
const importResult = ref(null);

// 必填字段定义
const requiredFields = ref([
  { key: 'name', name: t('product.name'), required: true },
  { key: 'sku', name: t('product.sku'), required: true },
  { key: 'category', name: t('product.category'), required: false },
  { key: 'brand', name: t('product.brand'), required: false },
  { key: 'price', name: t('product.price'), required: false },
  { key: 'description', name: t('product.description'), required: false },
  { key: 'tags', name: t('product.tags'), required: false }
]);

// 检查映射是否有效
const isMappingValid = computed(() => {
  const requiredMapped = requiredFields.value
    .filter(field => field.required)
    .every(field => fieldMapping[field.key]);
  return requiredMapped;
});

function handleFileChange(file) {
  selectedFile.value = file.raw;
  parseFilePreview(file.raw);
}

function beforeUpload(file) {
  const isValidType = file.type === 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' ||
                     file.type === 'application/vnd.ms-excel' ||
                     file.type === 'text/csv';
  
  if (!isValidType) {
    ElMessage.error(t('product.invalidFileType'));
    return false;
  }
  
  const isValidSize = file.size / 1024 / 1024 < 10; // 限制10MB
  if (!isValidSize) {
    ElMessage.error(t('product.fileTooLarge'));
    return false;
  }
  
  return false; // 阻止自动上传
}

function parseFilePreview(file) {
  const reader = new FileReader();
  reader.onload = (e) => {
    try {
      const data = new Uint8Array(e.target.result);
      const workbook = XLSX.read(data, { type: 'array' });
      const firstSheetName = workbook.SheetNames[0];
      const worksheet = workbook.Sheets[firstSheetName];
      const jsonData = XLSX.utils.sheet_to_json(worksheet, { header: 1 });
      
      if (jsonData.length > 0) {
        previewColumns.value = jsonData[0]; // 第一行作为列名
        previewData.value = jsonData.slice(1, 6).map(row => {
          const obj = {};
          previewColumns.value.forEach((col, index) => {
            obj[col] = row[index] || '';
          });
          return obj;
        });
        
        // 自动映射相似字段
        autoMapFields();
      }
    } catch (error) {
      ElMessage.error(t('product.parseFileFailed'));
    }
  };
  reader.readAsArrayBuffer(file);
}

function autoMapFields() {
  const columnMap = {
    'name': ['产品名称', '名称', 'name', 'product_name', '产品'],
    'sku': ['SKU', 'sku', '产品编码', '编码', 'code'],
    'category': ['分类', 'category', '产品分类', 'product_category'],
    'brand': ['品牌', 'brand', '厂商'],
    'price': ['价格', 'price', '单价', '售价'],
    'description': ['描述', 'description', '产品描述', '详情'],
    'tags': ['标签', 'tags', '标记', 'tag']
  };
  
  Object.keys(columnMap).forEach(field => {
    const matchedColumn = previewColumns.value.find(col => 
      columnMap[field].some(keyword => 
        col.toLowerCase().includes(keyword.toLowerCase())
      )
    );
    if (matchedColumn) {
      fieldMapping[field] = matchedColumn;
    }
  });
}

function downloadTemplate() {
  // 创建模板数据
  const templateData = [
    ['产品名称', 'SKU', '产品分类', '品牌', '价格', '产品描述', '标签'],
    ['示例产品1', 'SKU001', '电子产品', '示例品牌', '99.99', '这是一个示例产品描述', '新品,热销'],
    ['示例产品2', 'SKU002', '机械设备', '示例品牌', '199.99', '这是另一个示例产品描述', '推荐']
  ];
  
  const ws = XLSX.utils.aoa_to_sheet(templateData);
  const wb = XLSX.utils.book_new();
  XLSX.utils.book_append_sheet(wb, ws, '产品导入模板');
  XLSX.writeFile(wb, '产品导入模板.xlsx');
}

function nextStep() {
  if (currentStep.value < 2) {
    currentStep.value++;
  }
}

function prevStep() {
  if (currentStep.value > 0) {
    currentStep.value--;
  }
}

async function startImport() {
  currentStep.value = 2;
  importProgress.value = 0;
  importStatus.value = '';
  importMessage.value = t('product.preparingImport');
  importResult.value = null;
  
  try {
    // 模拟导入进度
    const progressInterval = setInterval(() => {
      if (importProgress.value < 90) {
        importProgress.value += Math.random() * 10;
        importMessage.value = t('product.importing', { 
          current: Math.floor(importProgress.value), 
          total: 100 
        });
      }
    }, 200);
    
    const result = await importProducts(selectedFile.value, fieldMapping);
    
    clearInterval(progressInterval);
    importProgress.value = 100;
    importStatus.value = 'success';
    importMessage.value = t('product.importCompleted');
    
    importResult.value = {
      success: true,
      title: t('product.importSuccess'),
      message: t('product.importSuccessMessage'),
      successCount: result.successCount || 0,
      errorCount: result.errorCount || 0,
      totalCount: result.totalCount || 0,
      errors: result.errors || []
    };
    
    emit('imported');
  } catch (error) {
    importProgress.value = 100;
    importStatus.value = 'exception';
    importMessage.value = t('product.importFailed');
    
    importResult.value = {
      success: false,
      title: t('product.importFailed'),
      message: error.message || t('product.importErrorMessage'),
      successCount: 0,
      errorCount: 0,
      totalCount: 0,
      errors: []
    };
  }
}

function cancelImport() {
  emit('close');
}

function resetImport() {
  currentStep.value = 0;
  fileList.value = [];
  selectedFile.value = null;
  previewData.value = [];
  previewColumns.value = [];
  Object.keys(fieldMapping).forEach(key => {
    delete fieldMapping[key];
  });
  importProgress.value = 0;
  importStatus.value = '';
  importMessage.value = '';
  importResult.value = null;
}

function finishImport() {
  emit('close');
}
</script>

<style scoped>
.product-import {
  padding: 20px;
}

.step-content {
  margin: 40px 0;
  min-height: 400px;
}

.upload-section {
  text-align: center;
}

.upload-section h3 {
  margin-bottom: 16px;
  color: #1f2937;
}

.upload-tips {
  color: #6b7280;
  margin-bottom: 24px;
}

.template-section {
  margin-top: 40px;
  padding-top: 24px;
  border-top: 1px solid #e5e7eb;
}

.template-section h4 {
  margin-bottom: 12px;
  color: #374151;
}

.mapping-section h3,
.progress-section h3 {
  margin-bottom: 16px;
  color: #1f2937;
}

.mapping-tips {
  color: #6b7280;
  margin-bottom: 24px;
}

.preview-data {
  margin-bottom: 32px;
}

.preview-data h4,
.field-mapping h4 {
  margin-bottom: 16px;
  color: #374151;
  font-size: 16px;
}

.mapping-list {
  display: grid;
  gap: 16px;
}

.mapping-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px;
  background: #f8fafc;
  border-radius: 8px;
  border: 1px solid #e5e7eb;
}

.field-info {
  display: flex;
  align-items: center;
  gap: 4px;
  min-width: 120px;
}

.field-name {
  font-weight: 500;
  color: #374151;
}

.required-mark {
  color: #f56c6c;
  font-weight: bold;
}

.progress-section {
  text-align: center;
}

.progress-info {
  margin: 40px 0;
}

.progress-text {
  margin-top: 16px;
  color: #6b7280;
}

.import-result {
  margin-top: 40px;
}

.result-stats {
  display: flex;
  justify-content: center;
  gap: 40px;
  margin: 20px 0;
}

.stat-item {
  text-align: center;
}

.stat-number {
  display: block;
  font-size: 24px;
  font-weight: 700;
  margin-bottom: 4px;
}

.stat-item.success .stat-number {
  color: #10b981;
}

.stat-item.error .stat-number {
  color: #ef4444;
}

.stat-item.total .stat-number {
  color: #6b7280;
}

.stat-label {
  font-size: 14px;
  color: #6b7280;
}

.error-list {
  margin-top: 24px;
  text-align: left;
}

.error-list h4 {
  margin-bottom: 12px;
  color: #374151;
}

.step-actions {
  display: flex;
  justify-content: center;
  gap: 12px;
  margin-top: 40px;
  padding-top: 24px;
  border-top: 1px solid #e5e7eb;
}

/* Element Plus 样式覆盖 */
:deep(.el-upload-dragger) {
  width: 400px;
  height: 200px;
}

:deep(.el-steps) {
  margin-bottom: 40px;
}
</style>