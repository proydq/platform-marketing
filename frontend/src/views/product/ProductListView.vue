<template>
  <div class="product-list-container">
    <div class="header-actions">
      <h2>{{ $t('product.productManagement') }}</h2>
      <el-button type="primary" @click="openDialog()" size="large">
        <el-icon><Plus /></el-icon>
        {{ $t('product.addProduct') }}
      </el-button>
    </div>

    <el-form :inline="true" :model="filters" class="filter-form">
      <el-form-item :label="$t('product.name')">
        <el-input v-model="filters.name" :placeholder="$t('product.searchByName')" clearable />
      </el-form-item>
      <el-form-item :label="$t('product.category')">
        <el-select v-model="filters.category" :placeholder="$t('product.selectCategory')" clearable>
          <el-option value="" :label="$t('common.all')" />
          <el-option v-for="category in categories" :key="category.id" :label="category.name" :value="category.name" />
        </el-select>
      </el-form-item>
      <el-form-item :label="$t('product.tags')">
        <el-input v-model="filters.tag" :placeholder="$t('product.searchByTag')" clearable />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="loadData" :loading="loading">
          <el-icon><Search /></el-icon>
          {{ $t('common.search') }}
        </el-button>
        <el-button @click="resetFilters">
          <el-icon><Refresh /></el-icon>
          {{ $t('common.reset') }}
        </el-button>
      </el-form-item>
    </el-form>

    <el-table :data="products" v-loading="loading" empty-text="暂无产品数据">
      <el-table-column prop="name" :label="$t('product.name')" min-width="150" show-overflow-tooltip />
      <el-table-column prop="category" :label="$t('product.category')" width="120" />
      <el-table-column :label="$t('product.images')" width="80">
        <template #default="{ row }">
          <el-image 
            v-if="row.imageList && row.imageList.length > 0"
            :src="row.imageList[0]"
            fit="cover"
            class="product-image"
            :preview-src-list="row.imageList"
          />
          <span v-else class="no-image">{{ $t('product.noImage') }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('product.price')" width="150">
        <template #default="{ row }">
          <div v-if="row.priceUSD" class="price-info">
            <span class="currency">USD</span>
            <span class="amount">${{ row.priceUSD }}</span>
          </div>
          <div v-if="row.priceCNY" class="price-info">
            <span class="currency">CNY</span>
            <span class="amount">¥{{ row.priceCNY }}</span>
          </div>
          <div v-if="row.priceEUR" class="price-info">
            <span class="currency">EUR</span>
            <span class="amount">€{{ row.priceEUR }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column :label="$t('product.tags')" min-width="150">
        <template #default="{ row }">
          <el-tag v-for="t in row.tags" :key="t" size="small" style="margin-right:4px">{{ t }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column :label="$t('product.spec')" width="100">
        <template #default="{ row }">
          <el-tag v-if="row.specPdf" type="success" size="small">{{ $t('product.hasSpec') }}</el-tag>
          <span v-else class="no-spec">{{ $t('product.noSpec') }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('common.actions')" width="150" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="openDialog(row)">
            <el-icon><Edit /></el-icon>
            {{ $t('common.edit') }}
          </el-button>
          <el-popconfirm 
            :title="$t('product.confirmDelete')"
            @confirm="handleDelete(row)"
            :confirm-button-text="$t('common.confirm')"
            :cancel-button-text="$t('common.cancel')"
          >
            <template #reference>
              <el-button type="danger" size="small">
                <el-icon><Delete /></el-icon>
                {{ $t('common.delete') }}
              </el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination-container">
      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        background
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <el-dialog 
      v-model="dialogVisible" 
      :title="dialogTitle" 
      width="800px"
      :close-on-click-modal="false"
    >
      <el-form 
        :model="form" 
        :rules="formRules"
        ref="formRef"
        label-width="120px"
        class="product-form"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item :label="$t('product.name')" prop="name" required>
              <el-input 
                v-model="form.name" 
                :placeholder="$t('product.enterProductName')"
                clearable
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="$t('product.category')" prop="category">
              <el-select 
                v-model="form.category" 
                :placeholder="$t('product.selectCategory')"
                filterable
                allow-create
                clearable
                style="width: 100%"
              >
                <el-option v-for="category in categories" :key="category.id" :label="category.name" :value="category.name" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item :label="$t('product.description')">
          <el-input 
            v-model="form.description" 
            type="textarea" 
            :rows="3"
            :placeholder="$t('product.enterDescription')"
          />
        </el-form-item>

        <el-form-item :label="$t('product.price')">
          <el-row :gutter="10">
            <el-col :span="8">
              <el-input v-model="form.priceUSD" :placeholder="$t('product.priceUSD')">
                <template #prepend>USD $</template>
              </el-input>
            </el-col>
            <el-col :span="8">
              <el-input v-model="form.priceCNY" :placeholder="$t('product.priceCNY')">
                <template #prepend>CNY ¥</template>
              </el-input>
            </el-col>
            <el-col :span="8">
              <el-input v-model="form.priceEUR" :placeholder="$t('product.priceEUR')">
                <template #prepend>EUR €</template>
              </el-input>
            </el-col>
          </el-row>
        </el-form-item>

        <el-form-item :label="$t('product.tags')">
          <el-select 
            v-model="form.tags" 
            multiple 
            filterable 
            allow-create
            :placeholder="$t('product.selectTags')"
            style="width: 100%"
          >
            <el-option v-for="t in allTags" :key="t" :label="t" :value="t" />
          </el-select>
        </el-form-item>

        <el-form-item :label="$t('product.images')">
          <el-upload
            action="/v1/products/upload"
            list-type="picture-card"
            :on-success="handleImageSuccess"
            :on-remove="handleImageRemove"
            :file-list="imageList"
            :limit="5"
            accept="image/*"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
          <div class="upload-tip">{{ $t('product.imageTip') }}</div>
        </el-form-item>

        <el-form-item :label="$t('product.specPdf')">
          <el-upload
            action="/v1/products/upload"
            :on-success="handlePdfSuccess"
            :file-list="pdfList"
            :limit="1"
            accept="application/pdf"
            class="pdf-upload"
          >
            <el-button type="primary">
              <el-icon><Upload /></el-icon>
              {{ $t('product.uploadPdf') }}
            </el-button>
          </el-upload>
          <div class="upload-tip">{{ $t('product.pdfTip') }}</div>
        </el-form-item>

        <el-form-item :label="$t('product.videoUrl')">
          <el-input 
            v-model="form.videoUrl" 
            :placeholder="$t('product.enterVideoUrl')"
            clearable
          >
            <template #prepend>
              <el-icon><VideoPlay /></el-icon>
            </template>
          </el-input>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="dialogVisible=false" size="large">
          {{ $t('common.cancel') }}
        </el-button>
        <el-button type="primary" @click="submitForm" :loading="submitting" size="large">
          {{ $t('common.confirm') }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue';
import { ElMessage, ElLoading } from 'element-plus';
import { 
  Plus, Search, Refresh, Edit, Delete, Upload, VideoPlay 
} from '@element-plus/icons-vue';
import { 
  getProductList, 
  createProduct, 
  updateProduct, 
  deleteProduct,
  getCategories 
} from '@/api/product';

// 响应式数据
const loading = ref(false);
const submitting = ref(false);
const filters = reactive({ name: '', category: '', tag: '' });
const products = ref([]);
const categories = ref([]);
const dialogVisible = ref(false);
const dialogTitle = ref('');
const formRef = ref(null);

// 分页数据
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
});

// 表单数据
const form = reactive({
  id: null,
  name: '',
  category: '',
  description: '',
  priceUSD: '',
  priceCNY: '',
  priceEUR: '',
  tags: [],
  imageList: [],
  specPdf: '',
  videoUrl: ''
});

// 文件列表
const imageList = ref([]);
const pdfList = ref([]);
const allTags = ref(['New', 'Hot', 'Sale', 'Limited', 'Popular', 'Featured']);

// 表单验证规则
const formRules = {
  name: [
    { required: true, message: '请输入产品名称', trigger: 'blur' },
    { min: 2, max: 100, message: '产品名称长度在 2 到 100 个字符', trigger: 'blur' }
  ]
};

// 加载产品列表数据
async function loadData() {
  try {
    loading.value = true;
    const params = {
      ...filters,
      page: pagination.page - 1, // 后端从0开始
      size: pagination.size
    };
    const response = await getProductList(params);
    
    if (response.data) {
      // 处理分页数据结构
      if (response.data.rows !== undefined) {
        products.value = response.data.rows || [];
        pagination.total = response.data.total || 0;
      } else if (Array.isArray(response.data)) {
        products.value = response.data;
        pagination.total = response.data.length;
      } else {
        products.value = [];
        pagination.total = 0;
      }
    } else {
      products.value = [];
      pagination.total = 0;
    }
  } catch (error) {
    console.error('加载产品列表失败:', error);
    ElMessage.error('加载产品列表失败');
    products.value = [];
    pagination.total = 0;
  } finally {
    loading.value = false;
  }
}

// 分页大小改变
function handleSizeChange() {
  pagination.page = 1;
  loadData();
}

// 当前页改变
function handleCurrentChange() {
  loadData();
}

// 加载产品分类
async function loadCategories() {
  try {
    const response = await getCategories();
    categories.value = response.data || [];
  } catch (error) {
    console.error('加载分类失败:', error);
  }
}

// 重置搜索条件
function resetFilters() {
  Object.assign(filters, { name: '', category: '', tag: '' });
  loadData();
}

// 打开新增/编辑对话框
function openDialog(row = null) {
  dialogTitle.value = row?.id ? '编辑产品' : '新增产品';
  
  if (row) {
    // 编辑模式
    Object.assign(form, {
      ...row,
      tags: row.tags || [],
      imageList: row.imageList || []
    });
    
    // 设置文件列表
    imageList.value = (row.imageList || []).map(url => ({
      name: url.split('/').pop(),
      url: url
    }));
    
    pdfList.value = row.specPdf ? [{
      name: 'spec.pdf',
      url: row.specPdf
    }] : [];
  } else {
    // 新增模式
    Object.assign(form, {
      id: null,
      name: '',
      category: '',
      description: '',
      priceUSD: '',
      priceCNY: '',
      priceEUR: '',
      tags: [],
      imageList: [],
      specPdf: '',
      videoUrl: ''
    });
    
    imageList.value = [];
    pdfList.value = [];
  }
  
  dialogVisible.value = true;
  
  // 清除表单验证
  nextTick(() => {
    formRef.value?.clearValidate();
  });
}

// 处理图片上传成功
function handleImageSuccess(response, file) {
  const url = response.data?.url || response.url;
  if (url) {
    form.imageList.push(url);
    ElMessage.success('图片上传成功');
  } else {
    ElMessage.error('图片上传失败');
  }
}

// 处理图片移除
function handleImageRemove(file) {
  const index = form.imageList.findIndex(url => url === file.url);
  if (index > -1) {
    form.imageList.splice(index, 1);
  }
}

// 处理PDF上传成功
function handlePdfSuccess(response, file) {
  const url = response.data?.url || response.url;
  if (url) {
    form.specPdf = url;
    pdfList.value = [{
      name: file.name,
      url: url
    }];
    ElMessage.success('PDF上传成功');
  } else {
    ElMessage.error('PDF上传失败');
  }
}

// 提交表单
async function submitForm() {
  try {
    await formRef.value?.validate();
    
    submitting.value = true;
    
    const submitData = {
      ...form,
      // 确保数字字段正确转换
      priceUSD: form.priceUSD ? Number(form.priceUSD) : null,
      priceCNY: form.priceCNY ? Number(form.priceCNY) : null,
      priceEUR: form.priceEUR ? Number(form.priceEUR) : null
    };
    
    if (form.id) {
      await updateProduct(form.id, submitData);
      ElMessage.success('产品更新成功');
    } else {
      await createProduct(submitData);
      ElMessage.success('产品创建成功');
    }
    
    dialogVisible.value = false;
    loadData();
    
  } catch (error) {
    console.error('提交失败:', error);
    ElMessage.error('提交失败: ' + (error.message || '未知错误'));
  } finally {
    submitting.value = false;
  }
}

// 删除产品
async function handleDelete(row) {
  try {
    await deleteProduct(row.id);
    ElMessage.success('产品删除成功');
    loadData();
  } catch (error) {
    console.error('删除失败:', error);
    ElMessage.error('删除失败: ' + (error.message || '未知错误'));
  }
}

// 页面初始化
onMounted(() => {
  loadData();
  loadCategories();
});
</script>

<style scoped>
.product-list-container {
  padding: 20px;
}

.header-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #ebeef5;
}

.header-actions h2 {
  margin: 0;
  color: #303133;
  font-size: 24px;
  font-weight: 600;
}

.filter-form {
  background: #f8f9fa;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.product-image {
  width: 50px;
  height: 50px;
  border-radius: 4px;
}

.no-image {
  color: #999;
  font-size: 12px;
}

.price-info {
  margin-bottom: 4px;
  font-size: 13px;
}

.currency {
  color: #909399;
  margin-right: 4px;
}

.amount {
  font-weight: 500;
  color: #303133;
}

.no-spec {
  color: #999;
  font-size: 12px;
}

.product-form {
  max-height: 60vh;
  overflow-y: auto;
}

.upload-tip {
  color: #909399;
  font-size: 12px;
  margin-top: 8px;
  line-height: 1.4;
}

.pdf-upload {
  display: flex;
  align-items: center;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
  padding: 20px 0;
}

/* Element Plus 样式覆盖 */
:deep(.el-upload-list--picture-card .el-upload-list__item) {
  width: 80px;
  height: 80px;
}

:deep(.el-upload--picture-card) {
  width: 80px;
  height: 80px;
}

:deep(.el-dialog__body) {
  padding: 20px 20px 0;
}

:deep(.el-form-item__label) {
  font-weight: 500;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .product-list-container {
    padding: 10px;
  }
  
  .header-actions {
    flex-direction: column;
    gap: 15px;
    align-items: stretch;
  }
  
  .filter-form :deep(.el-form-item) {
    display: block;
    margin-right: 0;
    margin-bottom: 15px;
  }
  
  .filter-form :deep(.el-form-item__content) {
    margin-left: 0 !important;
  }
}
</style>
