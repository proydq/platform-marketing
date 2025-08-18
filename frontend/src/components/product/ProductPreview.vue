<template>
  <div class="product-preview">
    <div class="preview-header">
      <div class="language-selector">
        <el-select v-model="currentLanguage" @change="changeLanguage">
          <el-option
            v-for="lang in availableLanguages"
            :key="lang.code"
            :label="lang.name"
            :value="lang.code"
          />
        </el-select>
      </div>
    </div>

    <div class="preview-content">
      <div class="product-gallery">
        <div class="main-image">
          <img
            :src="currentImage || '/default-product.png'"
            :alt="currentContent.name"
          />
        </div>
        <div
          v-if="product.images && product.images.length > 1"
          class="thumbnail-list"
        >
          <div
            v-for="(image, index) in product.images"
            :key="index"
            class="thumbnail"
            :class="{ active: currentImage === image }"
            @click="currentImage = image"
          >
            <img :src="image" :alt="`图片 ${index + 1}`" />
          </div>
        </div>
      </div>

      <div class="product-details">
        <div class="product-header">
          <h1 class="product-title">{{ currentContent.name }}</h1>
          <div class="product-meta">
            <el-tag
              :type="getStatusType(product.status)"
              size="large"
              effect="light"
            >
              {{ getStatusText(product.status) }}
            </el-tag>
            <span class="product-sku"
              >{{ t("product.sku") }}: {{ product.sku }}</span
            >
          </div>
        </div>

        <div class="product-pricing">
          <div
            v-for="price in product.prices"
            :key="price.currency"
            class="price-item"
          >
            <span class="currency">{{ price.currency }}</span>
            <span class="price">{{ formatPrice(price.value) }}</span>
          </div>
        </div>

        <div class="product-info">
          <div v-if="product.brand" class="info-item">
            <span class="info-label">{{ t("product.brand") }}:</span>
            <span class="info-value">{{ product.brand }}</span>
          </div>
          <div v-if="product.category" class="info-item">
            <span class="info-label">{{ t("product.category") }}:</span>
            <span class="info-value">{{ product.category }}</span>
          </div>
          <div v-if="product.tags && product.tags.length > 0" class="info-item">
            <span class="info-label">{{ t("product.tags") }}:</span>
            <div class="product-tags">
              <el-tag
                v-for="tag in product.tags"
                :key="tag"
                size="small"
                type="info"
                effect="plain"
              >
                {{ tag }}
              </el-tag>
            </div>
          </div>
        </div>

        <div v-if="currentContent.shortDescription" class="product-summary">
          <h3>{{ t("product.summary") }}</h3>
          <p>{{ currentContent.shortDescription }}</p>
        </div>

        <div v-if="currentContent.description" class="product-description">
          <h3>{{ t("product.description") }}</h3>
          <div
            class="description-content"
            v-html="currentContent.description"
          ></div>
        </div>

        <div
          v-if="product.specifications && product.specifications.length > 0"
          class="product-specifications"
        >
          <h3>{{ t("product.specifications") }}</h3>
          <el-table :data="product.specifications" size="small">
            <el-table-column
              prop="name"
              :label="t('product.specName')"
              width="150"
            />
            <el-table-column prop="value" :label="t('product.specValue')" />
          </el-table>
        </div>

        <div
          v-if="product.documents && product.documents.length > 0"
          class="product-documents"
        >
          <h3>{{ t("product.documents") }}</h3>
          <div class="document-list">
            <div
              v-for="doc in product.documents"
              :key="doc.id"
              class="document-item"
            >
              <el-link :href="doc.url" :download="doc.name" type="primary">
                <el-icon><Document /></el-icon>
                {{ doc.name }}
              </el-link>
              <span class="document-size">{{ formatFileSize(doc.size) }}</span>
            </div>
          </div>
        </div>

        <div
          v-if="product.videos && product.videos.length > 0"
          class="product-videos"
        >
          <h3>{{ t("product.videos") }}</h3>
          <div class="video-list">
            <video
              v-for="video in product.videos"
              :key="video.id"
              :src="video.url"
              controls
              class="product-video"
            ></video>
          </div>
        </div>
      </div>
    </div>

    <div class="preview-actions">
      <el-button @click="editProduct">
        <el-icon><Edit /></el-icon>
        {{ t("product.editProduct") }}
      </el-button>
      <el-button type="primary" @click="duplicateProduct">
        <el-icon><CopyDocument /></el-icon>
        {{ t("product.duplicateProduct") }}
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { useI18n } from "vue-i18n";
import { Document, Edit, CopyDocument } from "@element-plus/icons-vue";
import { duplicateProduct as apiDuplicateProduct } from "@/api/product";

const props = defineProps({
  product: {
    type: Object,
    required: true,
  },
});

const router = useRouter();
const { t } = useI18n();

const currentLanguage = ref("zh-CN");
const currentImage = ref("");

// 可用语言
const availableLanguages = ref([
  { code: "zh-CN", name: "中文" },
  { code: "en-US", name: "English" },
  { code: "es-ES", name: "Español" },
  { code: "fr-FR", name: "Français" },
  { code: "de-DE", name: "Deutsch" },
  { code: "ja-JP", name: "日本語" },
]);

// 当前语言的内容
const currentContent = computed(() => {
  if (props.product.languages) {
    const langContent = props.product.languages.find(
      (l) => l.code === currentLanguage.value
    );
    if (langContent) {
      return langContent;
    }
  }

  // 返回默认内容
  return {
    name: props.product.name || "",
    shortDescription: props.product.shortDescription || "",
    description: props.product.description || "",
  };
});

onMounted(() => {
  // 设置默认图片
  if (props.product.images && props.product.images.length > 0) {
    currentImage.value = props.product.images[0];
  }

  // 设置默认语言
  if (props.product.languages && props.product.languages.length > 0) {
    currentLanguage.value = props.product.languages[0].code;
  }
});

function changeLanguage(langCode) {
  currentLanguage.value = langCode;
}

function getStatusType(status) {
  const typeMap = {
    published: "success",
    draft: "warning",
    archived: "info",
  };
  return typeMap[status] || "info";
}

function getStatusText(status) {
  const textMap = {
    published: t("product.published"),
    draft: t("product.draft"),
    archived: t("product.archived"),
  };
  return textMap[status] || status;
}

function formatPrice(price) {
  return new Intl.NumberFormat("en-US", {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  }).format(price);
}

function formatFileSize(bytes) {
  if (!bytes) return "0 B";
  const k = 1024;
  const sizes = ["B", "KB", "MB", "GB"];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + " " + sizes[i];
}

function editProduct() {
  router.push(`/product/edit/${props.product.id}`);
}

async function duplicateProduct() {
  try {
    await apiDuplicateProduct(props.product.id);
    ElMessage.success(t("product.duplicateSuccess"));
  } catch (error) {
    ElMessage.error(t("product.duplicateFailed"));
  }
}
</script>

<style scoped>
.product-preview {
  padding: 24px;
  max-width: 1200px;
  margin: 0 auto;
}

.preview-header {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 24px;
}

.language-selector {
  width: 150px;
}

.preview-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 40px;
  margin-bottom: 40px;
}

.product-gallery {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.main-image {
  width: 100%;
  height: 400px;
  border-radius: 12px;
  overflow: hidden;
  background: #f3f4f6;
  border: 1px solid #e5e7eb;
}

.main-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.thumbnail-list {
  display: flex;
  gap: 8px;
  overflow-x: auto;
}

.thumbnail {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  border: 2px solid transparent;
  transition: all 0.3s ease;
}

.thumbnail.active {
  border-color: #409eff;
}

.thumbnail img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-details {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.product-header {
  border-bottom: 1px solid #e5e7eb;
  padding-bottom: 16px;
}

.product-title {
  margin: 0 0 12px 0;
  font-size: 28px;
  font-weight: 700;
  color: #1f2937;
  line-height: 1.2;
}

.product-meta {
  display: flex;
  align-items: center;
  gap: 16px;
}

.product-sku {
  color: #6b7280;
  font-size: 14px;
}

.product-pricing {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

.price-item {
  display: flex;
  align-items: baseline;
  gap: 4px;
  background: #f0f9ff;
  padding: 8px 16px;
  border-radius: 8px;
  border: 1px solid #e0f2fe;
}

.currency {
  font-size: 14px;
  color: #0369a1;
  font-weight: 500;
}

.price {
  font-size: 24px;
  font-weight: 700;
  color: #0c4a6e;
}

.product-info {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.info-item {
  display: flex;
  align-items: flex-start;
  gap: 8px;
}

.info-label {
  font-weight: 500;
  color: #374151;
  min-width: 80px;
}

.info-value {
  color: #6b7280;
}

.product-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.product-summary h3,
.product-description h3,
.product-specifications h3,
.product-documents h3,
.product-videos h3 {
  margin: 0 0 16px 0;
  font-size: 18px;
  font-weight: 600;
  color: #1f2937;
}

.product-summary p {
  margin: 0;
  color: #374151;
  line-height: 1.6;
}

.description-content {
  color: #374151;
  line-height: 1.6;
}

.description-content :deep(p) {
  margin-bottom: 16px;
}

.description-content :deep(ul),
.description-content :deep(ol) {
  margin-bottom: 16px;
  padding-left: 24px;
}

.document-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.document-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px;
  background: #f8fafc;
  border-radius: 8px;
  border: 1px solid #e5e7eb;
}

.document-size {
  font-size: 12px;
  color: #6b7280;
}

.video-list {
  display: grid;
  gap: 16px;
}

.product-video {
  width: 100%;
  max-height: 300px;
  border-radius: 8px;
}

.preview-actions {
  display: flex;
  justify-content: center;
  gap: 16px;
  padding-top: 24px;
  border-top: 1px solid #e5e7eb;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .preview-content {
    grid-template-columns: 1fr;
    gap: 24px;
  }

  .product-title {
    font-size: 24px;
  }

  .price {
    font-size: 20px;
  }

  .info-item {
    flex-direction: column;
    gap: 4px;
  }

  .info-label {
    min-width: auto;
  }
}

/* Element Plus 样式覆盖 */
:deep(.el-table) {
  border-radius: 8px;
  overflow: hidden;
}

:deep(.el-table th) {
  background-color: #f8fafc;
  color: #374151;
  font-weight: 600;
}
</style>
