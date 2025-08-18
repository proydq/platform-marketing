<template>
  <div class="product-edit-container">
    <div class="page-header">
      <div class="header-left">
        <el-button type="text" @click="goBack" class="back-btn">
          <el-icon><ArrowLeft /></el-icon>
          {{ t('common.back') }}
        </el-button>
        <h1>{{ isEdit ? t('product.editProduct') : t('product.newProduct') }}</h1>
      </div>
      <div class="header-actions">
        <el-button @click="goBack">{{ t('common.cancel') }}</el-button>
        <el-button @click="saveDraft" :loading="saving">{{ t('product.saveDraft') }}</el-button>
        <el-button type="primary" @click="saveAndPublish" :loading="saving">
          {{ t('product.saveAndPublish') }}
        </el-button>
      </div>
    </div>

    <div class="edit-content">
      <el-row :gutter="24">
        <!-- 左侧编辑区 -->
        <el-col :xl="16" :lg="14" :md="24">
          <div class="edit-sections">
            <!-- 基础信息 -->
            <el-card class="section-card">
              <template #header>
                <div class="section-header">
                  <span class="section-title">{{ t('product.basicInfo') }}</span>
                </div>
              </template>
              
              <el-form :model="form" label-width="120px" class="product-form">
                <el-form-item :label="t('product.name')" required>
                  <el-input v-model="form.name" :placeholder="t('product.namePlaceholder')" />
                </el-form-item>
                
                <el-form-item :label="t('product.sku')" required>
                  <el-input v-model="form.sku" :placeholder="t('product.skuPlaceholder')" />
                </el-form-item>
                
                <el-form-item :label="t('product.category')" required>
                  <el-select v-model="form.categoryId" :placeholder="t('product.selectCategory')" style="width: 100%">
                    <el-option 
                      v-for="cat in categories" 
                      :key="cat.id" 
                      :label="cat.name" 
                      :value="cat.id" 
                    />
                  </el-select>
                </el-form-item>
                
                <el-form-item :label="t('product.brand')">
                  <el-input v-model="form.brand" :placeholder="t('product.brandPlaceholder')" />
                </el-form-item>
                
                <el-form-item :label="t('product.tags')">
                  <el-select
                    v-model="form.tags"
                    multiple
                    filterable
                    allow-create
                    default-first-option
                    :placeholder="t('product.tagsPlaceholder')"
                    style="width: 100%"
                  >
                    <el-option v-for="tag in commonTags" :key="tag" :label="tag" :value="tag" />
                  </el-select>
                </el-form-item>
                
                <el-form-item :label="t('product.shortDescription')">
                  <el-input 
                    v-model="form.shortDescription" 
                    type="textarea" 
                    :rows="3"
                    :placeholder="t('product.shortDescPlaceholder')" 
                  />
                </el-form-item>
                
                <el-form-item :label="t('product.description')">
                  <RichTextEditor v-model="form.description" />
                </el-form-item>
              </el-form>
            </el-card>

            <!-- 多媒体管理 -->
            <el-card class="section-card">
              <template #header>
                <div class="section-header">
                  <span class="section-title">{{ t('product.multimedia') }}</span>
                </div>
              </template>
              
              <div class="multimedia-section">
                <div class="image-upload-area">
                  <h4>{{ t('product.productImages') }}</h4>
                  <ImageUploader 
                    v-model="form.images" 
                    :max-count="10"
                    draggable
                    @change="handleImagesChange"
                  />
                </div>
                
                <div class="video-upload-area" v-if="form.videos">
                  <h4>{{ t('product.productVideos') }}</h4>
                  <VideoUploader v-model="form.videos" :max-count="3" />
                </div>
                
                <div class="document-upload-area">
                  <h4>{{ t('product.documents') }}</h4>
                  <DocumentUploader v-model="form.documents" :max-count="5" />
                </div>
              </div>
            </el-card>

            <!-- 多语言内容 -->
            <el-card class="section-card">
              <template #header>
                <div class="section-header">
                  <span class="section-title">{{ t('product.multiLanguage') }}</span>
                  <el-button type="primary" size="small" @click="showLanguageDialog = true">
                    <el-icon><Plus /></el-icon>
                    {{ t('product.addLanguage') }}
                  </el-button>
                </div>
              </template>
              
              <el-tabs v-model="currentLanguage" type="card">
                <el-tab-pane 
                  v-for="lang in form.languages" 
                  :key="lang.code"
                  :label="getLanguageName(lang.code)" 
                  :name="lang.code"
                >
                  <div class="language-content">
                    <el-form label-width="120px">
                      <el-form-item :label="t('product.name')">
                        <el-input v-model="lang.name" />
                      </el-form-item>
                      <el-form-item :label="t('product.shortDescription')">
                        <el-input v-model="lang.shortDescription" type="textarea" :rows="3" />
                      </el-form-item>
                      <el-form-item :label="t('product.description')">
                        <RichTextEditor v-model="lang.description" />
                      </el-form-item>
                    </el-form>
                  </div>
                </el-tab-pane>
              </el-tabs>
            </el-card>
          </div>
        </el-col>

        <!-- 右侧配置区 -->
        <el-col :xl="8" :lg="10" :md="24">
          <div class="config-sections">
            <!-- 发布设置 -->
            <el-card class="config-card">
              <template #header>
                <span class="config-title">{{ t('product.publishSettings') }}</span>
              </template>
              
              <div class="config-content">
                <div class="status-setting">
                  <label>{{ t('product.status') }}</label>
                  <el-select v-model="form.status" style="width: 100%">
                    <el-option :label="t('product.draft')" value="draft" />
                    <el-option :label="t('product.published')" value="published" />
                    <el-option :label="t('product.archived')" value="archived" />
                  </el-select>
                </div>
                
                <div class="featured-setting">
                  <el-checkbox v-model="form.featured">{{ t('product.featured') }}</el-checkbox>
                </div>
                
                <div class="seo-setting">
                  <label>{{ t('product.seoKeywords') }}</label>
                  <el-input v-model="form.seoKeywords" type="textarea" :rows="2" />
                </div>
              </div>
            </el-card>

            <!-- 价格管理 -->
            <el-card class="config-card">
              <template #header>
                <div class="config-header">
                  <span class="config-title">{{ t('product.pricing') }}</span>
                  <el-button type="primary" size="small" @click="addPricing">
                    <el-icon><Plus /></el-icon>
                  </el-button>
                </div>
              </template>
              
              <div class="pricing-list">
                <div 
                  v-for="(price, index) in form.prices" 
                  :key="index"
                  class="pricing-item"
                >
                  <div class="pricing-row">
                    <el-select v-model="price.currency" style="width: 80px">
                      <el-option label="USD" value="USD" />
                      <el-option label="EUR" value="EUR" />
                      <el-option label="GBP" value="GBP" />
                      <el-option label="CNY" value="CNY" />
                      <el-option label="JPY" value="JPY" />
                    </el-select>
                    <el-input-number 
                      v-model="price.value" 
                      :min="0" 
                      :precision="2"
                      style="flex: 1"
                    />
                    <el-button 
                      type="danger" 
                      size="small" 
                      text
                      @click="removePricing(index)"
                    >
                      <el-icon><Delete /></el-icon>
                    </el-button>
                  </div>
                </div>
              </div>
            </el-card>

            <!-- 产品规格 -->
            <el-card class="config-card">
              <template #header>
                <div class="config-header">
                  <span class="config-title">{{ t('product.specifications') }}</span>
                  <el-button type="primary" size="small" @click="addSpecification">
                    <el-icon><Plus /></el-icon>
                  </el-button>
                </div>
              </template>
              
              <div class="specs-list">
                <div 
                  v-for="(spec, index) in form.specifications" 
                  :key="index"
                  class="spec-item"
                >
                  <el-input 
                    v-model="spec.name" 
                    :placeholder="t('product.specName')"
                    size="small"
                    style="margin-bottom: 8px"
                  />
                  <div class="spec-value-row">
                    <el-input 
                      v-model="spec.value" 
                      :placeholder="t('product.specValue')"
                      size="small"
                      style="flex: 1"
                    />
                    <el-button 
                      type="danger" 
                      size="small" 
                      text
                      @click="removeSpecification(index)"
                    >
                      <el-icon><Delete /></el-icon>
                    </el-button>
                  </div>
                </div>
              </div>
            </el-card>

            <!-- 预览 -->
            <el-card class="config-card">
              <template #header>
                <span class="config-title">{{ t('product.preview') }}</span>
              </template>
              
              <div class="preview-content">
                <div class="preview-image">
                  <img :src="form.images?.[0] || '/default-product.png'" alt="预览图" />
                </div>
                <h4 class="preview-name">{{ form.name || t('product.namePlaceholder') }}</h4>
                <div class="preview-price">
                  <span v-for="price in form.prices" :key="price.currency" class="price-tag">
                    {{ price.currency }} {{ price.value }}
                  </span>
                </div>
              </div>
            </el-card>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 添加语言对话框 -->
    <el-dialog v-model="showLanguageDialog" :title="t('product.addLanguage')" width="400px">
      <el-form>
        <el-form-item :label="t('product.selectLanguage')">
          <el-select v-model="selectedLanguage" style="width: 100%">
            <el-option 
              v-for="lang in availableLanguages" 
              :key="lang.code"
              :label="lang.name" 
              :value="lang.code" 
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showLanguageDialog = false">{{ t('common.cancel') }}</el-button>
        <el-button type="primary" @click="addLanguage">{{ t('common.confirm') }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { useI18n } from 'vue-i18n';
import { ArrowLeft, Plus, Delete } from '@element-plus/icons-vue';
import RichTextEditor from '@/components/RichTextEditor.vue';
import ImageUploader from '@/components/upload/ImageUploader.vue';
import VideoUploader from '@/components/upload/VideoUploader.vue';
import DocumentUploader from '@/components/upload/DocumentUploader.vue';
import {
  getProduct,
  createProduct,
  updateProduct,
  getCategories
} from '@/api/product';

const route = useRoute();
const router = useRouter();
const { t } = useI18n();

const isEdit = computed(() => !!route.params.id);
const saving = ref(false);
const categories = ref([]);
const currentLanguage = ref('zh-CN');
const showLanguageDialog = ref(false);
const selectedLanguage = ref('');

// 常用标签
const commonTags = ref(['新品', '热销', '促销', '推荐', '出口', '定制']);

// 可用语言
const availableLanguages = ref([
  { code: 'zh-CN', name: '中文' },
  { code: 'en-US', name: 'English' },
  { code: 'es-ES', name: 'Español' },
  { code: 'fr-FR', name: 'Français' },
  { code: 'de-DE', name: 'Deutsch' },
  { code: 'ja-JP', name: '日本語' },
  { code: 'ko-KR', name: '한국어' }
]);

// 表单数据
const form = reactive({
  name: '',
  sku: '',
  categoryId: '',
  brand: '',
  tags: [],
  shortDescription: '',
  description: '',
  status: 'draft',
  featured: false,
  seoKeywords: '',
  images: [],
  videos: [],
  documents: [],
  prices: [{ currency: 'USD', value: 0 }],
  specifications: [],
  languages: [
    {
      code: 'zh-CN',
      name: '',
      shortDescription: '',
      description: ''
    }
  ]
});

onMounted(async () => {
  await loadCategories();
  if (isEdit.value) {
    await loadProduct();
  }
});

async function loadCategories() {
  try {
    const res = await getCategories();
    categories.value = res.data || [];
  } catch (error) {
    console.error('加载分类失败:', error);
  }
}

async function loadProduct() {
  try {
    const res = await getProduct(route.params.id);
    const product = res.data;
    Object.assign(form, product);
    if (form.languages?.length > 0) {
      currentLanguage.value = form.languages[0].code;
    }
  } catch (error) {
    ElMessage.error(t('product.loadFailed'));
    router.push('/product');
  }
}

async function saveDraft() {
  form.status = 'draft';
  await saveProduct();
}

async function saveAndPublish() {
  form.status = 'published';
  await saveProduct();
}

async function saveProduct() {
  if (!validateForm()) return;
  
  saving.value = true;
  try {
    if (isEdit.value) {
      await updateProduct(route.params.id, form);
      ElMessage.success(t('product.updateSuccess'));
    } else {
      await createProduct(form);
      ElMessage.success(t('product.createSuccess'));
    }
    router.push('/product');
  } catch (error) {
    ElMessage.error(t('product.saveFailed'));
  } finally {
    saving.value = false;
  }
}

function validateForm() {
  if (!form.name) {
    ElMessage.error(t('product.nameRequired'));
    return false;
  }
  if (!form.sku) {
    ElMessage.error(t('product.skuRequired'));
    return false;
  }
  if (!form.categoryId) {
    ElMessage.error(t('product.categoryRequired'));
    return false;
  }
  return true;
}

function goBack() {
  router.push('/product');
}

function addPricing() {
  form.prices.push({ currency: 'USD', value: 0 });
}

function removePricing(index) {
  if (form.prices.length > 1) {
    form.prices.splice(index, 1);
  }
}

function addSpecification() {
  form.specifications.push({ name: '', value: '' });
}

function removeSpecification(index) {
  form.specifications.splice(index, 1);
}

function addLanguage() {
  if (!selectedLanguage.value) return;
  
  const exists = form.languages.find(l => l.code === selectedLanguage.value);
  if (exists) {
    ElMessage.warning(t('product.languageExists'));
    return;
  }
  
  form.languages.push({
    code: selectedLanguage.value,
    name: '',
    shortDescription: '',
    description: ''
  });
  
  currentLanguage.value = selectedLanguage.value;
  showLanguageDialog.value = false;
  selectedLanguage.value = '';
}

function getLanguageName(code) {
  const lang = availableLanguages.value.find(l => l.code === code);
  return lang?.name || code;
}

function handleImagesChange(images) {
  form.images = images;
}
</script>

<style scoped>
.product-edit-container {
  padding: 20px;
  background: #f5f7fa;
  min-height: calc(100vh - 60px);
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  background: white;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.back-btn {
  color: #6b7280;
  padding: 0;
}

.page-header h1 {
  margin: 0;
  color: #1f2937;
  font-size: 24px;
  font-weight: 600;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.edit-content {
  margin-bottom: 20px;
}

.edit-sections {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.config-sections {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.section-card, .config-card {
  border-radius: 12px;
  border: 1px solid #e5e7eb;
}

.section-header, .config-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.section-title, .config-title {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
}

.product-form {
  padding: 0;
}

.multimedia-section {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.multimedia-section h4 {
  margin: 0 0 12px 0;
  color: #374151;
  font-size: 14px;
  font-weight: 600;
}

.language-content {
  padding: 20px 0;
}

.config-content {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.config-content label {
  display: block;
  margin-bottom: 8px;
  color: #374151;
  font-size: 14px;
  font-weight: 500;
}

.status-setting, .featured-setting, .seo-setting {
  margin-bottom: 16px;
}

.pricing-list, .specs-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.pricing-item, .spec-item {
  padding: 12px;
  background: #f8fafc;
  border-radius: 8px;
  border: 1px solid #e5e7eb;
}

.pricing-row, .spec-value-row {
  display: flex;
  gap: 8px;
  align-items: center;
}

.preview-content {
  text-align: center;
}

.preview-image {
  width: 100%;
  height: 120px;
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 12px;
  background: #f3f4f6;
}

.preview-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.preview-name {
  margin: 0 0 8px 0;
  color: #1f2937;
  font-size: 16px;
  font-weight: 600;
}

.preview-price {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.price-tag {
  background: #f0f9ff;
  color: #0369a1;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 600;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .product-edit-container {
    padding: 16px;
  }
  
  .page-header {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }
  
  .header-actions {
    justify-content: center;
  }
}

/* Element Plus 样式覆盖 */
:deep(.el-card__header) {
  background: #f8fafc;
  border-bottom: 1px solid #e5e7eb;
}

:deep(.el-tabs__item) {
  color: #6b7280;
}

:deep(.el-tabs__item.is-active) {
  color: #409EFF;
}
</style>