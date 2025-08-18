<template>
  <div class="category-manager">
    <div class="toolbar">
      <el-button type="primary" @click="openDialog(false)">
        <el-icon><Plus /></el-icon>
        {{ t('product.addCategory') }}
      </el-button>
    </div>

    <div class="category-tree">
      <el-tree
        ref="treeRef"
        :data="categoryTree"
        :props="treeProps"
        node-key="id"
        :expand-on-click-node="false"
        :allow-drag="allowDrag"
        :allow-drop="allowDrop"
        @node-drop="handleDrop"
        draggable
      >
        <template #default="{ node, data }">
          <div class="tree-node">
            <div class="node-content">
              <span class="node-label">{{ data.name }}</span>
              <span class="node-count">({{ data.productCount || 0 }})</span>
            </div>
            <div class="node-actions">
              <el-button type="text" size="small" @click="openDialog(true, data)">
                <el-icon><Edit /></el-icon>
              </el-button>
              <el-button type="text" size="small" @click="addChild(data)">
                <el-icon><Plus /></el-icon>
              </el-button>
              <el-popconfirm
                :title="t('product.deleteCategoryConfirm')"
                @confirm="deleteCategory(data)"
              >
                <template #reference>
                  <el-button type="text" size="small" class="delete-btn">
                    <el-icon><Delete /></el-icon>
                  </el-button>
                </template>
              </el-popconfirm>
            </div>
          </div>
        </template>
      </el-tree>
    </div>

    <!-- 分类编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? t('product.editCategory') : t('product.addCategory')"
      width="500px"
    >
      <el-form :model="form" label-width="100px">
        <el-form-item :label="t('product.categoryName')" required>
          <el-input v-model="form.name" :placeholder="t('product.categoryNamePlaceholder')" />
        </el-form-item>
        
        <el-form-item :label="t('product.parentCategory')">
          <el-tree-select
            v-model="form.parentId"
            :data="categoryOptions"
            :props="{ label: 'name', value: 'id' }"
            :placeholder="t('product.selectParentCategory')"
            clearable
            check-strictly
          />
        </el-form-item>
        
        <el-form-item :label="t('product.categoryDescription')">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            :placeholder="t('product.categoryDescPlaceholder')"
          />
        </el-form-item>
        
        <el-form-item :label="t('product.categoryIcon')">
          <el-input v-model="form.icon" :placeholder="t('product.categoryIconPlaceholder')" />
        </el-form-item>
        
        <el-form-item :label="t('product.sortOrder')">
          <el-input-number v-model="form.sortOrder" :min="0" />
        </el-form-item>
        
        <el-form-item :label="t('product.categoryStatus')">
          <el-switch
            v-model="form.enabled"
            :active-text="t('common.enabled')"
            :inactive-text="t('common.disabled')"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="dialogVisible = false">{{ t('common.cancel') }}</el-button>
        <el-button type="primary" @click="saveCategory" :loading="saving">
          {{ t('common.save') }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { useI18n } from 'vue-i18n';
import { Plus, Edit, Delete } from '@element-plus/icons-vue';
import {
  getCategories,
  createCategory,
  updateCategory,
  deleteCategory as apiDeleteCategory
} from '@/api/product';

const emit = defineEmits(['updated']);
const { t } = useI18n();

const categories = ref([]);
const dialogVisible = ref(false);
const isEdit = ref(false);
const saving = ref(false);
const treeRef = ref();

const treeProps = {
  children: 'children',
  label: 'name'
};

const form = reactive({
  id: null,
  name: '',
  parentId: null,
  description: '',
  icon: '',
  sortOrder: 0,
  enabled: true
});

// 构建树形结构
const categoryTree = computed(() => {
  return buildTree(categories.value);
});

// 分类选项（用于父级选择）
const categoryOptions = computed(() => {
  return buildTreeOptions(categories.value, form.id);
});

onMounted(() => {
  loadCategories();
});

async function loadCategories() {
  try {
    const res = await getCategories();
    categories.value = res.data || [];
  } catch (error) {
    ElMessage.error(t('product.loadCategoriesFailed'));
  }
}

function buildTree(data, parentId = null) {
  const tree = [];
  for (const item of data) {
    if (item.parentId === parentId) {
      const children = buildTree(data, item.id);
      if (children.length > 0) {
        item.children = children;
      }
      tree.push(item);
    }
  }
  return tree.sort((a, b) => (a.sortOrder || 0) - (b.sortOrder || 0));
}

function buildTreeOptions(data, excludeId = null) {
  const options = [];
  for (const item of data) {
    if (item.id === excludeId) continue;
    
    const option = {
      id: item.id,
      name: item.name,
      children: []
    };
    
    const children = buildTreeOptions(data.filter(d => d.parentId === item.id), excludeId);
    if (children.length > 0) {
      option.children = children;
    }
    
    if (item.parentId === null) {
      options.push(option);
    }
  }
  return options;
}

function openDialog(edit, data = null) {
  isEdit.value = edit;
  if (edit && data) {
    Object.assign(form, data);
  } else {
    resetForm();
  }
  dialogVisible.value = true;
}

function addChild(parentData) {
  resetForm();
  form.parentId = parentData.id;
  dialogVisible.value = true;
}

function resetForm() {
  Object.assign(form, {
    id: null,
    name: '',
    parentId: null,
    description: '',
    icon: '',
    sortOrder: 0,
    enabled: true
  });
}

async function saveCategory() {
  if (!form.name) {
    ElMessage.error(t('product.categoryNameRequired'));
    return;
  }
  
  saving.value = true;
  try {
    if (isEdit.value) {
      await updateCategory(form.id, form);
      ElMessage.success(t('product.updateCategorySuccess'));
    } else {
      await createCategory(form);
      ElMessage.success(t('product.createCategorySuccess'));
    }
    
    dialogVisible.value = false;
    await loadCategories();
    emit('updated');
  } catch (error) {
    ElMessage.error(t('product.saveCategoryFailed'));
  } finally {
    saving.value = false;
  }
}

async function deleteCategory(data) {
  if (data.children && data.children.length > 0) {
    ElMessage.error(t('product.cannotDeleteCategoryWithChildren'));
    return;
  }
  
  try {
    await apiDeleteCategory(data.id);
    ElMessage.success(t('common.deleted'));
    await loadCategories();
    emit('updated');
  } catch (error) {
    ElMessage.error(t('common.deleteFailed'));
  }
}

function allowDrag(node) {
  return true;
}

function allowDrop(draggingNode, dropNode, type) {
  // 限制最大层级为3
  if (type === 'inner') {
    return getNodeLevel(dropNode) < 3;
  }
  return true;
}

function getNodeLevel(node) {
  let level = 1;
  let parent = node.parent;
  while (parent && parent.data) {
    level++;
    parent = parent.parent;
  }
  return level;
}

async function handleDrop(draggingNode, dropNode, dropType) {
  const dragData = draggingNode.data;
  let newParentId = null;
  
  if (dropType === 'inner') {
    newParentId = dropNode.data.id;
  } else if (dropNode.parent && dropNode.parent.data) {
    newParentId = dropNode.parent.data.id;
  }
  
  try {
    await updateCategory(dragData.id, {
      ...dragData,
      parentId: newParentId
    });
    
    await loadCategories();
    emit('updated');
  } catch (error) {
    ElMessage.error(t('product.updateCategoryFailed'));
    // 刷新树以恢复原始状态
    await loadCategories();
  }
}
</script>

<style scoped>
.category-manager {
  padding: 20px;
}

.toolbar {
  margin-bottom: 20px;
}

.category-tree {
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 16px;
}

.tree-node {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  padding: 4px 0;
}

.node-content {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
}

.node-label {
  font-size: 14px;
  color: #1f2937;
}

.node-count {
  font-size: 12px;
  color: #6b7280;
}

.node-actions {
  display: flex;
  gap: 4px;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.tree-node:hover .node-actions {
  opacity: 1;
}

.delete-btn {
  color: #f56c6c !important;
}

/* Element Plus 样式覆盖 */
:deep(.el-tree-node__content) {
  height: auto;
  padding: 8px 0;
}

:deep(.el-tree-node__content:hover) {
  background-color: #f8fafc;
}

:deep(.el-tree-node__expand-icon) {
  color: #6b7280;
}

:deep(.el-tree-node__expand-icon.expanded) {
  transform: rotate(90deg);
}
</style>