<template>
  <div class="customer-tags-container">
    <div class="page-header">
      <h1>{{ t('customerTags.title') }}</h1>
      <p>{{ t('customerTags.subtitle') }}</p>
    </div>

    <div class="tags-management">
      <el-card>
        <template #header>
          <div class="card-header">
            <span>{{ t('customerTags.tagCategories') }}</span>
            <el-button type="primary" @click="showCategoryDialog = true">
              {{ t('customerTags.addCategory') }}
            </el-button>
          </div>
        </template>

        <div class="categories-section">
          <el-row :gutter="20">
            <el-col 
              v-for="category in categories" 
              :key="category.id" 
              :span="8"
              class="category-col"
            >
              <div class="category-card">
                <div class="category-header">
                  <h3>{{ category.name }}</h3>
                  <el-dropdown @command="handleCategoryAction">
                    <el-button type="text" size="small">
                      <el-icon><More /></el-icon>
                    </el-button>
                    <template #dropdown>
                      <el-dropdown-menu>
                        <el-dropdown-item :command="`edit-${category.id}`">
                          {{ t('common.edit') }}
                        </el-dropdown-item>
                        <el-dropdown-item :command="`delete-${category.id}`">
                          {{ t('common.delete') }}
                        </el-dropdown-item>
                      </el-dropdown-menu>
                    </template>
                  </el-dropdown>
                </div>
                <p class="category-desc">{{ category.description }}</p>
                
                <div class="tags-list">
                  <el-tag
                    v-for="tag in category.tags"
                    :key="tag.id"
                    :type="tag.type"
                    :color="tag.color"
                    class="tag-item"
                    closable
                    @close="removeTag(category.id, tag.id)"
                    @click="editTag(tag)"
                  >
                    {{ tag.name }}
                  </el-tag>
                  <el-button 
                    type="primary" 
                    size="small" 
                    plain
                    @click="addTag(category.id)"
                    class="add-tag-btn"
                  >
                    + {{ t('customerTags.addTag') }}
                  </el-button>
                </div>
              </div>
            </el-col>
          </el-row>
        </div>
      </el-card>

      <!-- 标签使用统计 -->
      <el-card class="stats-card">
        <template #header>
          <div class="card-header">
            <span>{{ t('customerTags.tagStats') }}</span>
            <el-button @click="refreshStats">
              {{ t('common.refresh') }}
            </el-button>
          </div>
        </template>

        <el-table :data="tagStats" style="width: 100%">
          <el-table-column prop="name" :label="t('customerTags.tagName')" />
          <el-table-column prop="category" :label="t('customerTags.category')" />
          <el-table-column prop="count" :label="t('customerTags.customerCount')" />
          <el-table-column prop="percentage" :label="t('customerTags.percentage')">
            <template #default="{ row }">
              {{ row.percentage }}%
            </template>
          </el-table-column>
          <el-table-column :label="t('common.actions')">
            <template #default="{ row }">
              <el-button type="text" @click="viewTaggedCustomers(row)">
                {{ t('customerTags.viewCustomers') }}
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>

    <!-- 添加/编辑分类对话框 -->
    <el-dialog 
      v-model="showCategoryDialog" 
      :title="editingCategory ? t('customerTags.editCategory') : t('customerTags.addCategory')"
      width="40%"
    >
      <el-form :model="categoryForm" :rules="categoryRules" ref="categoryFormRef" label-width="100px">
        <el-form-item :label="t('customerTags.categoryName')" prop="name">
          <el-input v-model="categoryForm.name" :placeholder="t('customerTags.categoryNamePlaceholder')" />
        </el-form-item>
        <el-form-item :label="t('customerTags.description')" prop="description">
          <el-input 
            v-model="categoryForm.description" 
            type="textarea"
            :rows="3"
            :placeholder="t('customerTags.descriptionPlaceholder')"
          />
        </el-form-item>
        <el-form-item :label="t('customerTags.categoryColor')">
          <el-color-picker v-model="categoryForm.color" />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showCategoryDialog = false">{{ t('common.cancel') }}</el-button>
          <el-button type="primary" @click="saveCategory">{{ t('common.confirm') }}</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 添加/编辑标签对话框 -->
    <el-dialog 
      v-model="showTagDialog" 
      :title="editingTag ? t('customerTags.editTag') : t('customerTags.addTag')"
      width="40%"
    >
      <el-form :model="tagForm" :rules="tagRules" ref="tagFormRef" label-width="100px">
        <el-form-item :label="t('customerTags.tagName')" prop="name">
          <el-input v-model="tagForm.name" :placeholder="t('customerTags.tagNamePlaceholder')" />
        </el-form-item>
        <el-form-item :label="t('customerTags.tagType')" prop="type">
          <el-select v-model="tagForm.type" :placeholder="t('customerTags.selectTagType')">
            <el-option label="默认" value="" />
            <el-option label="成功" value="success" />
            <el-option label="警告" value="warning" />
            <el-option label="危险" value="danger" />
            <el-option label="信息" value="info" />
          </el-select>
        </el-form-item>
        <el-form-item :label="t('customerTags.tagColor')">
          <el-color-picker v-model="tagForm.color" />
        </el-form-item>
        <el-form-item :label="t('customerTags.description')">
          <el-input 
            v-model="tagForm.description" 
            type="textarea"
            :rows="2"
            :placeholder="t('customerTags.tagDescPlaceholder')"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showTagDialog = false">{{ t('common.cancel') }}</el-button>
          <el-button type="primary" @click="saveTag">{{ t('common.confirm') }}</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 标签客户列表对话框 -->
    <el-dialog 
      v-model="showCustomersDialog" 
      :title="t('customerTags.taggedCustomers')"
      width="70%"
    >
      <el-table :data="taggedCustomers" style="width: 100%">
        <el-table-column prop="name" :label="t('customer.name')" />
        <el-table-column prop="email" :label="t('customer.email')" />
        <el-table-column prop="company" :label="t('customer.company')" />
        <el-table-column prop="source" :label="t('customer.source')" />
        <el-table-column prop="createdAt" :label="t('customer.createdAt')" />
      </el-table>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showCustomersDialog = false">{{ t('common.close') }}</el-button>
          <el-button type="primary" @click="exportTaggedCustomers">
            {{ t('customer.exportCustomer') }}
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { More } from '@element-plus/icons-vue'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()

const showCategoryDialog = ref(false)
const showTagDialog = ref(false)
const showCustomersDialog = ref(false)
const editingCategory = ref(null)
const editingTag = ref(null)
const categoryFormRef = ref()
const tagFormRef = ref()

const categories = ref([
  {
    id: 1,
    name: '客户类型',
    description: '根据客户业务类型分类',
    color: '#409EFF',
    tags: [
      { id: 1, name: '制造商', type: 'success', color: '#67C23A' },
      { id: 2, name: '贸易商', type: 'warning', color: '#E6A23C' },
      { id: 3, name: '零售商', type: 'info', color: '#909399' }
    ]
  },
  {
    id: 2,
    name: '客户规模',
    description: '根据公司规模大小分类',
    color: '#67C23A',
    tags: [
      { id: 4, name: '大型企业', type: 'danger', color: '#F56C6C' },
      { id: 5, name: '中型企业', type: 'warning', color: '#E6A23C' },
      { id: 6, name: '小型企业', type: 'info', color: '#909399' }
    ]
  },
  {
    id: 3,
    name: '合作意向',
    description: '客户合作意向程度',
    color: '#E6A23C',
    tags: [
      { id: 7, name: '高意向', type: 'success', color: '#67C23A' },
      { id: 8, name: '中意向', type: 'warning', color: '#E6A23C' },
      { id: 9, name: '低意向', type: 'info', color: '#909399' }
    ]
  }
])

const tagStats = ref([
  { name: '制造商', category: '客户类型', count: 45, percentage: 30 },
  { name: '贸易商', category: '客户类型', count: 60, percentage: 40 },
  { name: '零售商', category: '客户类型', count: 45, percentage: 30 },
  { name: '大型企业', category: '客户规模', count: 30, percentage: 20 },
  { name: '中型企业', category: '客户规模', count: 75, percentage: 50 },
  { name: '小型企业', category: '客户规模', count: 45, percentage: 30 }
])

const taggedCustomers = ref([])

const categoryForm = reactive({
  name: '',
  description: '',
  color: '#409EFF'
})

const tagForm = reactive({
  name: '',
  type: '',
  color: '',
  description: '',
  categoryId: null
})

const categoryRules = reactive({
  name: [
    { required: true, message: t('customerTags.categoryNameRequired'), trigger: 'blur' }
  ]
})

const tagRules = reactive({
  name: [
    { required: true, message: t('customerTags.tagNameRequired'), trigger: 'blur' }
  ]
})

onMounted(() => {
  // 初始化数据
})

const handleCategoryAction = (command) => {
  const [action, id] = command.split('-')
  const categoryId = parseInt(id)
  
  if (action === 'edit') {
    editCategory(categoryId)
  } else if (action === 'delete') {
    deleteCategory(categoryId)
  }
}

const editCategory = (categoryId) => {
  const category = categories.value.find(c => c.id === categoryId)
  if (category) {
    editingCategory.value = category
    categoryForm.name = category.name
    categoryForm.description = category.description
    categoryForm.color = category.color
    showCategoryDialog.value = true
  }
}

const deleteCategory = async (categoryId) => {
  try {
    await ElMessageBox.confirm(
      t('customerTags.deleteCategoryConfirm'),
      t('common.warning'),
      {
        confirmButtonText: t('common.confirm'),
        cancelButtonText: t('common.cancel'),
        type: 'warning'
      }
    )
    
    categories.value = categories.value.filter(c => c.id !== categoryId)
    ElMessage.success(t('common.deleteSuccess'))
  } catch {
    // 用户取消删除
  }
}

const saveCategory = async () => {
  if (!categoryFormRef.value) return
  
  try {
    await categoryFormRef.value.validate()
    
    if (editingCategory.value) {
      // 编辑分类
      const index = categories.value.findIndex(c => c.id === editingCategory.value.id)
      if (index > -1) {
        categories.value[index] = {
          ...categories.value[index],
          name: categoryForm.name,
          description: categoryForm.description,
          color: categoryForm.color
        }
      }
      ElMessage.success(t('common.updateSuccess'))
    } else {
      // 新增分类
      const newCategory = {
        id: Date.now(),
        name: categoryForm.name,
        description: categoryForm.description,
        color: categoryForm.color,
        tags: []
      }
      categories.value.push(newCategory)
      ElMessage.success(t('common.saveSuccess'))
    }
    
    showCategoryDialog.value = false
    resetCategoryForm()
  } catch (error) {
    console.log('Validation failed:', error)
  }
}

const resetCategoryForm = () => {
  categoryForm.name = ''
  categoryForm.description = ''
  categoryForm.color = '#409EFF'
  editingCategory.value = null
}

const addTag = (categoryId) => {
  tagForm.categoryId = categoryId
  showTagDialog.value = true
}

const editTag = (tag) => {
  editingTag.value = tag
  tagForm.name = tag.name
  tagForm.type = tag.type
  tagForm.color = tag.color
  tagForm.description = tag.description || ''
  showTagDialog.value = true
}

const saveTag = async () => {
  if (!tagFormRef.value) return
  
  try {
    await tagFormRef.value.validate()
    
    if (editingTag.value) {
      // 编辑标签
      const category = categories.value.find(c => 
        c.tags.some(t => t.id === editingTag.value.id)
      )
      if (category) {
        const tagIndex = category.tags.findIndex(t => t.id === editingTag.value.id)
        if (tagIndex > -1) {
          category.tags[tagIndex] = {
            ...category.tags[tagIndex],
            name: tagForm.name,
            type: tagForm.type,
            color: tagForm.color,
            description: tagForm.description
          }
        }
      }
      ElMessage.success(t('common.updateSuccess'))
    } else {
      // 新增标签
      const category = categories.value.find(c => c.id === tagForm.categoryId)
      if (category) {
        const newTag = {
          id: Date.now(),
          name: tagForm.name,
          type: tagForm.type,
          color: tagForm.color,
          description: tagForm.description
        }
        category.tags.push(newTag)
        ElMessage.success(t('common.saveSuccess'))
      }
    }
    
    showTagDialog.value = false
    resetTagForm()
  } catch (error) {
    console.log('Validation failed:', error)
  }
}

const resetTagForm = () => {
  tagForm.name = ''
  tagForm.type = ''
  tagForm.color = ''
  tagForm.description = ''
  tagForm.categoryId = null
  editingTag.value = null
}

const removeTag = async (categoryId, tagId) => {
  try {
    await ElMessageBox.confirm(
      t('customerTags.deleteTagConfirm'),
      t('common.warning'),
      {
        confirmButtonText: t('common.confirm'),
        cancelButtonText: t('common.cancel'),
        type: 'warning'
      }
    )
    
    const category = categories.value.find(c => c.id === categoryId)
    if (category) {
      category.tags = category.tags.filter(t => t.id !== tagId)
      ElMessage.success(t('common.deleteSuccess'))
    }
  } catch {
    // 用户取消删除
  }
}

const refreshStats = () => {
  ElMessage.success(t('customerTags.statsRefreshed'))
}

const viewTaggedCustomers = (tagInfo) => {
  // 模拟获取标签客户数据
  taggedCustomers.value = [
    {
      name: 'John Smith',
      email: 'john@example.com',
      company: 'ABC Corp',
      source: 'LinkedIn',
      createdAt: '2024-01-15'
    },
    {
      name: 'Sarah Johnson',
      email: 'sarah@techco.com',
      company: 'Tech Solutions',
      source: 'Website',
      createdAt: '2024-01-20'
    }
  ]
  showCustomersDialog.value = true
}

const exportTaggedCustomers = () => {
  ElMessage.success(t('customer.exportSuccess'))
  showCustomersDialog.value = false
}
</script>

<style scoped>
.customer-tags-container {
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

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
}

.stats-card {
  margin-top: 24px;
}

.category-col {
  margin-bottom: 20px;
}

.category-card {
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 16px;
  height: 100%;
  background: #fafafa;
}

.category-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.category-header h3 {
  margin: 0;
  color: #374151;
  font-size: 16px;
}

.category-desc {
  margin: 0 0 16px 0;
  color: #6b7280;
  font-size: 14px;
}

.tags-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tag-item {
  cursor: pointer;
  transition: all 0.2s;
}

.tag-item:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.add-tag-btn {
  font-size: 12px;
  height: 24px;
  padding: 0 8px;
}

.dialog-footer {
  display: flex;
  gap: 12px;
}
</style>