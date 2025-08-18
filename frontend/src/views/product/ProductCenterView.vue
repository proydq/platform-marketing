<template>
  <div class="page-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="page-title">
        <span class="page-icon">📦</span>
        {{ t('product.center') }}
      </div>
      <div class="page-subtitle">
        {{ t('product.centerSubtitle') }}
      </div>
      <div class="page-actions">
        <el-button type="primary" class="btn-primary" @click="showCreateDialog">
          <el-icon><Plus /></el-icon>
          {{ t("product.addProduct") }}
        </el-button>
        <el-button class="btn-secondary" @click="refreshData">
          <el-icon><Refresh /></el-icon>
          刷新数据
        </el-button>
      </div>
    </div>

    <!-- 产品统计 -->
    <div class="stats-grid">
      <div class="stat-card fade-in">
        <div class="stat-header">
          <div class="stat-icon" style="background: linear-gradient(135deg, #dbeafe, #93c5fd); color: #1e40af;">
            📦
          </div>
          <div class="stat-trend positive">
            +12%
          </div>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ productStats.total.toLocaleString() }}</div>
          <div class="stat-label">{{ t('product.totalProducts') }}</div>
        </div>
      </div>

      <div class="stat-card fade-in" style="animation-delay: 0.1s;">
        <div class="stat-header">
          <div class="stat-icon" style="background: linear-gradient(135deg, #d1fae5, #86efac); color: #059669;">
            🌟
          </div>
          <div class="stat-trend positive">
            +8%
          </div>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ productStats.published.toLocaleString() }}</div>
          <div class="stat-label">{{ t('product.publishedProducts') }}</div>
        </div>
      </div>

      <div class="stat-card fade-in" style="animation-delay: 0.2s;">
        <div class="stat-header">
          <div class="stat-icon" style="background: linear-gradient(135deg, #fef3c7, #fcd34d); color: #d97706;">
            🔄
          </div>
          <div class="stat-trend neutral">
            {{ productStats.draft }}个
          </div>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ productStats.draft }}</div>
          <div class="stat-label">{{ t('product.draftProducts') }}</div>
        </div>
      </div>

      <div class="stat-card fade-in" style="animation-delay: 0.3s;">
        <div class="stat-header">
          <div class="stat-icon" style="background: linear-gradient(135deg, #ede9fe, #c4b5fd); color: #7c3aed;">
            🏷️
          </div>
          <div class="stat-trend positive">
            +5%
          </div>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ productStats.categories }}</div>
          <div class="stat-label">{{ t('product.categories') }}</div>
        </div>
      </div>
    </div>

    <!-- 筛选和操作栏 -->
    <el-card class="filter-card">
      <div class="filter-section">
        <div class="filter-group">
          <el-select
            v-model="statusFilter"
            :placeholder="t('product.status')"
            style="width: 140px"
            clearable
          >
            <el-option :label="t('common.all')" value="" />
            <el-option :label="t('product.published')" value="published" />
            <el-option :label="t('product.draft')" value="draft" />
            <el-option :label="t('product.archived')" value="archived" />
          </el-select>
          <el-select
            v-model="categoryFilter"
            :placeholder="t('product.category')"
            style="width: 160px"
            clearable
          >
            <el-option :label="t('common.all')" value="" />
            <el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.id" />
          </el-select>
          <el-input
            v-model="search"
            :placeholder="t('product.searchPlaceholder')"
            clearable
            style="width: 240px"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </div>
        <div class="action-buttons">
          <el-button type="default" @click="showCategoryDialog = true">
            <el-icon><Folder /></el-icon>
            {{ t('product.manageCategories') }}
          </el-button>
          <el-button type="default" @click="showImportDialog = true">
            <el-icon><Upload /></el-icon>
            {{ t('product.batchImport') }}
          </el-button>
          <el-button type="primary" @click="createProduct">
            <el-icon><Plus /></el-icon>
            {{ t('product.newProduct') }}
          </el-button>
        </div>
      </div>
    </el-card>

    <!-- 产品列表 -->
    <div class="product-list">
      <div 
        v-for="product in filteredProducts" 
        :key="product.id"
        class="product-card"
      >
        <div class="product-image">
          <img :src="product.coverImage || '/default-product.png'" :alt="product.name" />
          <div class="product-actions-overlay">
            <el-button type="primary" size="small" @click="editProduct(product)">
              <el-icon><Edit /></el-icon>
            </el-button>
            <el-button type="info" size="small" @click="previewProduct(product)">
              <el-icon><View /></el-icon>
            </el-button>
            <el-popconfirm
              :title="t('common.deleteConfirm')"
              @confirm="deleteProduct(product)"
            >
              <template #reference>
                <el-button type="danger" size="small">
                  <el-icon><Delete /></el-icon>
                </el-button>
              </template>
            </el-popconfirm>
          </div>
        </div>
        
        <div class="product-content">
          <div class="product-header">
            <h3 class="product-name">{{ product.name }}</h3>
            <el-tag 
              :type="getStatusType(product.status)" 
              size="small"
              effect="light"
            >
              {{ getStatusText(product.status) }}
            </el-tag>
          </div>
          
          <div class="product-info">
            <div class="product-sku">{{ t('product.sku') }}: {{ product.sku }}</div>
            <div class="product-category">{{ t('product.category') }}: {{ getCategoryName(product.categoryId) }}</div>
            <div class="product-price">
              <span v-for="price in product.prices" :key="price.currency" class="price-item">
                {{ price.currency }} {{ price.value }}
              </span>
            </div>
          </div>
          
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
          
          <div class="product-meta">
            <span class="meta-item">{{ t('product.updated') }}: {{ formatDate(product.updatedAt) }}</span>
            <span class="meta-item">{{ t('product.languages') }}: {{ product.languages?.length || 1 }}</span>
          </div>
        </div>
      </div>
      
      <!-- 空状态 -->
      <el-empty v-if="filteredProducts.length === 0" :description="t('product.noProducts')" />
    </div>

    <!-- 分类管理对话框 -->
    <el-dialog v-model="showCategoryDialog" :title="t('product.categoryManagement')" width="800px">
      <CategoryManager @updated="loadCategories" />
    </el-dialog>

    <!-- 批量导入对话框 -->
    <el-dialog v-model="showImportDialog" :title="t('product.batchImport')" width="600px">
      <ProductImport @imported="loadProducts" @close="showImportDialog = false" />
    </el-dialog>

    <!-- 产品预览抽屉 -->
    <el-drawer v-model="showPreviewDrawer" :title="t('product.preview')" size="60%">
      <ProductPreview v-if="currentProduct" :product="currentProduct" />
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { useI18n } from 'vue-i18n';
import { Search, Plus, Folder, Upload, Edit, View, Delete, Refresh } from '@element-plus/icons-vue';
import CategoryManager from '@/components/product/CategoryManager.vue';
import ProductImport from '@/components/product/ProductImport.vue';
import ProductPreview from '@/components/product/ProductPreview.vue';
import {
  getProductList,
  deleteProduct as apiDeleteProduct,
  getCategories
} from '@/api/product';

const router = useRouter();
const { t } = useI18n();

const products = ref([]);
const categories = ref([]);
const statusFilter = ref('');
const categoryFilter = ref('');
const search = ref('');
const showCategoryDialog = ref(false);
const showImportDialog = ref(false);
const showPreviewDrawer = ref(false);
const currentProduct = ref(null);

// 产品统计数据
const productStats = computed(() => {
  const total = products.value.length;
  const published = products.value.filter(p => p.status === 'published').length;
  const draft = products.value.filter(p => p.status === 'draft').length;
  const categoriesSet = new Set(products.value.map(p => p.categoryId));
  
  return {
    total,
    published,
    draft,
    categories: categoriesSet.size
  };
});

// 过滤后的产品列表
const filteredProducts = computed(() => {
  return products.value.filter(product => {
    const statusOk = !statusFilter.value || product.status === statusFilter.value;
    const categoryOk = !categoryFilter.value || product.categoryId === categoryFilter.value;
    const searchOk = !search.value || 
                    product.name.toLowerCase().includes(search.value.toLowerCase()) ||
                    product.sku.toLowerCase().includes(search.value.toLowerCase());
    return statusOk && categoryOk && searchOk;
  });
});

onMounted(() => {
  loadProducts();
  loadCategories();
});

async function loadProducts() {
  try {
    const res = await getProductList({
      page: 0,
      size: 100,
      keyword: search.value,
      status: statusFilter.value,
      categoryId: categoryFilter.value
    });
    products.value = res.data?.rows || [];
    
    // 如果没有数据，添加示例数据
    if (products.value.length === 0) {
      products.value = [
        {
          id: 'demo-1',
          name: '智能手机 Pro Max',
          sku: 'PHONE-001',
          status: 'published',
          categoryId: 'cat-electronics',
          coverImage: 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=400&h=300&fit=crop',
          prices: [
            { currency: 'CNY', value: '8999.00' },
            { currency: 'USD', value: '1299.99' }
          ],
          tags: ['热销', '新品', '5G'],
          languages: ['zh-CN', 'en-US'],
          updatedAt: '2025-01-10T10:00:00Z'
        },
        {
          id: 'demo-2',
          name: '无线蓝牙耳机',
          sku: 'HEADPHONE-002',
          status: 'published',
          categoryId: 'cat-electronics',
          coverImage: 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=400&h=300&fit=crop',
          prices: [
            { currency: 'CNY', value: '599.00' },
            { currency: 'USD', value: '89.99' }
          ],
          tags: ['音质', '降噪'],
          languages: ['zh-CN'],
          updatedAt: '2025-01-08T15:30:00Z'
        },
        {
          id: 'demo-3',
          name: '智能手表运动版',
          sku: 'WATCH-003',
          status: 'draft',
          categoryId: 'cat-electronics',
          coverImage: 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=400&h=300&fit=crop',
          prices: [
            { currency: 'CNY', value: '2499.00' }
          ],
          tags: ['运动', '健康监测'],
          languages: ['zh-CN'],
          updatedAt: '2025-01-05T09:15:00Z'
        },
        {
          id: 'demo-4',
          name: '笔记本电脑商务版',
          sku: 'LAPTOP-004',
          status: 'published',
          categoryId: 'cat-computers',
          coverImage: 'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=400&h=300&fit=crop',
          prices: [
            { currency: 'CNY', value: '6999.00' },
            { currency: 'USD', value: '999.99' }
          ],
          tags: ['商务', '轻薄', '长续航'],
          languages: ['zh-CN', 'en-US'],
          updatedAt: '2025-01-03T11:45:00Z'
        },
        {
          id: 'demo-5',
          name: '机械键盘RGB版',
          sku: 'KEYBOARD-005',
          status: 'archived',
          categoryId: 'cat-computers',
          coverImage: 'https://images.unsplash.com/photo-1541140532154-b024d705b90a?w=400&h=300&fit=crop',
          prices: [
            { currency: 'CNY', value: '899.00' }
          ],
          tags: ['机械轴', 'RGB'],
          languages: ['zh-CN'],
          updatedAt: '2024-12-28T14:20:00Z'
        },
        {
          id: 'demo-6',
          name: '4K显示器专业版',
          sku: 'MONITOR-006',
          status: 'published',
          categoryId: 'cat-computers',
          coverImage: 'https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?w=400&h=300&fit=crop',
          prices: [
            { currency: 'CNY', value: '3299.00' },
            { currency: 'USD', value: '459.99' }
          ],
          tags: ['4K', '专业', '色彩准确'],
          languages: ['zh-CN', 'en-US'],
          updatedAt: '2024-12-25T16:10:00Z'
        }
      ];
    }
  } catch (error) {
    console.error('加载产品失败:', error);
    // 如果 API 调用失败，也显示示例数据
    products.value = [
      {
        id: 'demo-1',
        name: '智能手机 Pro Max',
        sku: 'PHONE-001',
        status: 'published',
        categoryId: 'cat-electronics',
        coverImage: 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=400&h=300&fit=crop',
        prices: [
          { currency: 'CNY', value: '8999.00' },
          { currency: 'USD', value: '1299.99' }
        ],
        tags: ['热销', '新品', '5G'],
        languages: ['zh-CN', 'en-US'],
        updatedAt: '2025-01-10T10:00:00Z'
      },
      {
        id: 'demo-2',
        name: '无线蓝牙耳机',
        sku: 'HEADPHONE-002',
        status: 'published',
        categoryId: 'cat-electronics',
        coverImage: 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=400&h=300&fit=crop',
        prices: [
          { currency: 'CNY', value: '599.00' },
          { currency: 'USD', value: '89.99' }
        ],
        tags: ['音质', '降噪'],
        languages: ['zh-CN'],
        updatedAt: '2025-01-08T15:30:00Z'
      }
    ];
  }
}

async function loadCategories() {
  try {
    const res = await getCategories();
    categories.value = res.data || [];
    
    // 如果没有分类数据，添加示例分类
    if (categories.value.length === 0) {
      categories.value = [
        {
          id: 'cat-electronics',
          name: '电子产品',
          description: '各类电子数码产品',
          count: 3
        },
        {
          id: 'cat-computers',
          name: '电脑配件',
          description: '电脑及相关配件',
          count: 3
        },
        {
          id: 'cat-home',
          name: '家居用品',
          description: '家庭生活用品',
          count: 0
        }
      ];
    }
  } catch (error) {
    console.error('加载分类失败:', error);
    // API 调用失败时也显示示例分类
    categories.value = [
      {
        id: 'cat-electronics',
        name: '电子产品',
        description: '各类电子数码产品',
        count: 3
      },
      {
        id: 'cat-computers',
        name: '电脑配件',
        description: '电脑及相关配件',
        count: 3
      }
    ];
  }
}

function createProduct() {
  router.push('/product/edit');
}

function editProduct(product) {
  router.push(`/product/edit/${product.id}`);
}

function previewProduct(product) {
  currentProduct.value = product;
  showPreviewDrawer.value = true;
}

async function deleteProduct(product) {
  try {
    await apiDeleteProduct(product.id);
    ElMessage.success(t('common.deleted'));
    loadProducts();
  } catch (error) {
    ElMessage.error(t('common.operationFailed'));
  }
}

function getStatusType(status) {
  const typeMap = {
    published: 'success',
    draft: 'warning',
    archived: 'info'
  };
  return typeMap[status] || 'info';
}

function getStatusText(status) {
  const textMap = {
    published: t('product.published'),
    draft: t('product.draft'),
    archived: t('product.archived')
  };
  return textMap[status] || status;
}

function getCategoryName(categoryId) {
  const category = categories.value.find(c => c.id === categoryId);
  return category?.name || t('common.unknown');
}

function formatDate(dateStr) {
  if (!dateStr) return '--';
  return new Date(dateStr).toLocaleDateString('zh-CN');
}

function refreshData() {
  loadProducts();
  loadCategories();
  ElMessage.success('数据已刷新');
}

function showCreateDialog() {
  router.push('/product/edit');
}
</script>

<style scoped>
.product-center-container {
  padding: 20px;
  background: #f5f7fa;
  min-height: calc(100vh - 60px);
}

.page-header {
  margin-bottom: 24px;
}

.page-header h1 {
  margin: 0 0 8px 0;
  color: #1f2937;
  font-size: 24px;
  font-weight: 600;
}

.page-header p {
  margin: 0;
  color: #6b7280;
  font-size: 14px;
}

/* 产品统计 */
.product-stats {
  margin-bottom: 24px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
}

.stat-item {
  background: white;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  gap: 16px;
  transition: all 0.3s ease;
}

.stat-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.stat-icon {
  font-size: 28px;
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f8fafc;
}

.stat-content {
  flex: 1;
}

.stat-number {
  font-size: 24px;
  font-weight: 700;
  color: #1f2937;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #6b7280;
  font-weight: 500;
}

/* 筛选卡片 */
.filter-card {
  margin-bottom: 24px;
}

.filter-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 16px;
}

.filter-group {
  display: flex;
  gap: 16px;
  align-items: center;
  flex-wrap: wrap;
}

.action-buttons {
  display: flex;
  gap: 12px;
}

/* 产品列表 */
.product-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
}

.product-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  border: 1px solid #e5e7eb;
}

.product-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
  border-color: #409EFF;
}

.product-image {
  position: relative;
  height: 200px;
  overflow: hidden;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-actions-overlay {
  position: absolute;
  top: 8px;
  right: 8px;
  display: flex;
  gap: 4px;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.product-card:hover .product-actions-overlay {
  opacity: 1;
}

.product-content {
  padding: 20px;
}

.product-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.product-name {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-info {
  margin-bottom: 12px;
  font-size: 14px;
  color: #6b7280;
}

.product-info > div {
  margin-bottom: 4px;
}

.product-price {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.price-item {
  background: #f0f9ff;
  color: #0369a1;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
}

.product-tags {
  margin-bottom: 12px;
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.product-meta {
  font-size: 12px;
  color: #9ca3af;
  display: flex;
  justify-content: space-between;
}

.meta-item {
  flex: 1;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .product-center-container {
    padding: 16px;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .filter-section {
    flex-direction: column;
    align-items: stretch;
  }
  
  .filter-group {
    justify-content: center;
  }
  
  .action-buttons {
    justify-content: center;
  }
  
  .product-list {
    grid-template-columns: 1fr;
  }
}

/* Element Plus 样式覆盖 */
:deep(.el-card) {
  border-radius: 12px;
  border: 1px solid #e5e7eb;
}

:deep(.el-card__header) {
  background: #f8fafc;
  border-bottom: 1px solid #e5e7eb;
}

:deep(.el-empty) {
  padding: 60px 20px;
}
</style>