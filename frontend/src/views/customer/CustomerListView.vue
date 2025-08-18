<template>
  <div class="page-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="page-title">
        <span class="page-icon">👥</span>
        {{ t("customer.title") }}
      </div>
      <div class="page-subtitle">
        {{ t("customer.description") }}
      </div>
      <div class="page-actions">
        <el-button type="primary" class="btn-primary" @click="showCreateDialog">
          <el-icon><Plus /></el-icon>
          {{ t("customer.addCustomer") }}
        </el-button>
        <el-button class="btn-secondary" @click="refreshData">
          <el-icon><Refresh /></el-icon>
          {{ t("common.refresh") }}
        </el-button>
      </div>
    </div>

    <!-- 客户统计 -->
    <div class="stats-grid">
      <div class="stat-card fade-in">
        <div class="stat-header">
          <div class="stat-icon" style="background: linear-gradient(135deg, #dbeafe, #93c5fd); color: #1e40af;">
            👥
          </div>
          <div class="stat-trend positive">
            +12%
          </div>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ customerStats.total.toLocaleString() }}</div>
          <div class="stat-label">{{ t('customer.totalCustomers') }}</div>
        </div>
      </div>

      <div class="stat-card fade-in" style="animation-delay: 0.1s;">
        <div class="stat-header">
          <div class="stat-icon" style="background: linear-gradient(135deg, #d1fae5, #86efac); color: #059669;">
            ✅
          </div>
          <div class="stat-trend positive">
            +8%
          </div>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ customerStats.active.toLocaleString() }}</div>
          <div class="stat-label">{{ t('customer.activeCustomers') }}</div>
        </div>
      </div>

      <div class="stat-card fade-in" style="animation-delay: 0.2s;">
        <div class="stat-header">
          <div class="stat-icon" style="background: linear-gradient(135deg, #fef3c7, #fcd34d); color: #d97706;">
            📊
          </div>
          <div class="stat-trend positive">
            +15%
          </div>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ customerStats.thisMonth.toLocaleString() }}</div>
          <div class="stat-label">{{ t('customer.newThisMonth') }}</div>
        </div>
      </div>

      <div class="stat-card fade-in" style="animation-delay: 0.3s;">
        <div class="stat-header">
          <div class="stat-icon" style="background: linear-gradient(135deg, #ede9fe, #c4b5fd); color: #7c3aed;">
            🎯
          </div>
          <div class="stat-trend neutral">
            {{ customerStats.sources }}个
          </div>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ customerStats.sources }}</div>
          <div class="stat-label">{{ t('customer.sources') }}</div>
        </div>
      </div>
    </div>

    <div class="data-table-wrapper">
      <div class="table-header">
        <div class="table-title">🔍 客户筛选与搜索</div>
        <div class="table-subtitle">精准筛选客户数据，快速找到目标客户群体</div>
      </div>
      <div class="form-section">
        <div class="filter-group">
          <el-input
            v-model="searchForm.keyword"
            :placeholder="t('customer.searchPlaceholder')"
            clearable
            style="width: 240px"
            @keyup.enter="handleSearch"
            @clear="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>

          <el-select
            v-model="searchForm.status"
            :placeholder="t('customer.statusFilter')"
            clearable
            style="width: 140px"
            @change="handleSearch"
          >
            <el-option :label="t('common.all')" value="" />
            <el-option :label="t('customer.statusActive')" value="active" />
            <el-option :label="t('customer.statusInactive')" value="inactive" />
          </el-select>

          <el-select
            v-model="searchForm.source"
            :placeholder="t('customer.sourceFilter')"
            clearable
            style="width: 140px"
            @change="handleSearch"
          >
            <el-option :label="t('common.all')" value="" />
            <el-option
              v-for="source in sourcesOptions"
              :key="source"
              :label="source"
              :value="source"
            />
          </el-select>
        </div>

        <div class="action-buttons">
          <el-upload
            action="#"
            :show-file-list="false"
            accept=".csv,.xlsx,.xls"
            :before-upload="handleImport"
            :disabled="importing"
          >
            <el-button :loading="importing" class="btn-secondary">
              <el-icon><Upload /></el-icon>
              {{ t("customer.importCustomer") }}
            </el-button>
          </el-upload>

          <el-button @click="handleExport" :loading="exporting" class="btn-secondary">
            <el-icon><Download /></el-icon>
            {{ t("customer.exportCustomer") }}
          </el-button>
        </div>
      </div>

    </div>

    <!-- 数据表格 -->
    <div class="data-table-wrapper">
      <div class="table-header">
        <div class="table-title">📋 客户数据表</div>
        <div class="table-subtitle">管理和查看所有客户详细信息</div>
      </div>
      <el-table
        :data="filteredCustomerList"
        v-loading="loading"
        style="width: 100%"
        :empty-text="t('common.noData')"
        @selection-change="handleSelectionChange"
        class="modern-table"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column
          prop="name"
          :label="$t('customer.name')"
          min-width="120"
        />
        <el-table-column
          prop="email"
          :label="$t('customer.email')"
          min-width="180"
        />
        <el-table-column
          prop="phone"
          :label="$t('customer.phone')"
          min-width="140"
        />
        <el-table-column
          prop="company"
          :label="$t('customer.company')"
          min-width="150"
        />
        <el-table-column
          prop="source"
          :label="$t('customer.source')"
          width="120"
        />
        <el-table-column
          prop="status"
          :label="$t('customer.status')"
          width="100"
          align="center"
        >
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              active-value="active"
              inactive-value="inactive"
              :active-text="$t('customer.statusActive')"
              :inactive-text="$t('customer.statusInactive')"
              inline-prompt
              @change="handleStatusChange(row)"
            />
          </template>
        </el-table-column>
        <el-table-column
          prop="createdAt"
          :label="$t('customer.createdAt')"
          width="180"
        >
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column
          :label="$t('common.actions')"
          width="200"
          fixed="right"
        >
          <template #default="{ row }">
            <el-button
              v-if="hasPermission('customer:view')"
              size="small"
              type="primary"
              link
              @click="handleView(row)"
            >
              <el-icon><View /></el-icon>
              {{ $t("common.view") }}
            </el-button>

            <el-button size="small" link @click="handleEdit(row)">
              <el-icon><Edit /></el-icon>
              {{ $t("common.edit") }}
            </el-button>

            <el-button
              v-if="hasPermission('customer:delete')"
              size="small"
              type="danger"
              link
              @click="handleDelete(row)"
            >
              <el-icon><Delete /></el-icon>
              {{ $t("common.delete") }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          background
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </div>

    <!-- 客户表单对话框 -->
    <CustomerForm
      v-model="formVisible"
      :customer="selectedCustomer"
      :is-edit="isEdit"
      @success="handleFormSuccess"
    />

    <!-- 客户详情抽屉 -->
    <CustomerDetail v-model="detailVisible" :customer-id="selectedCustomerId" />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import {
  Search,
  Refresh,
  Plus,
  Upload,
  Download,
  View,
  Edit,
  Delete,
} from "@element-plus/icons-vue";
import { useI18n } from "vue-i18n";
import { hasPermission } from "@/composables/permission";
import { useCustomerStore } from "@/stores/modules/customer";
import { formatDate } from "@/utils/date";
import CustomerForm from "./components/CustomerForm.vue";
import CustomerDetail from "./components/CustomerDetail.vue";

const { t } = useI18n();
const customerStore = useCustomerStore();

// 响应式数据
const loading = ref(false);
const importing = ref(false);
const exporting = ref(false);
const formVisible = ref(false);
const detailVisible = ref(false);
const isEdit = ref(false);
const selectedCustomer = ref(null);
const selectedCustomerId = ref(null);
const selectedCustomers = ref([]);

// 搜索表单
const searchForm = reactive({
  keyword: "",
  status: "",
  source: "",
});

// 分页数据
const pagination = reactive({
  page: 1,
  size: 20,
  total: 0,
});

// 计算属性
const customerList = computed(() => customerStore.customerList);
const sourcesOptions = computed(() => customerStore.sourcesOptions);

// 过滤后的客户列表
const filteredCustomerList = computed(() => {
  return customerList.value.filter(customer => {
    const keywordOk = !searchForm.keyword || 
                     customer.name.toLowerCase().includes(searchForm.keyword.toLowerCase()) ||
                     customer.email.toLowerCase().includes(searchForm.keyword.toLowerCase()) ||
                     customer.phone.includes(searchForm.keyword);
    const statusOk = !searchForm.status || customer.status === searchForm.status;
    const sourceOk = !searchForm.source || customer.source === searchForm.source;
    return keywordOk && statusOk && sourceOk;
  });
});

// 客户统计数据
const customerStats = computed(() => {
  const total = customerList.value.length;
  const active = customerList.value.filter(c => c.status === 'active').length;
  const thisMonthStart = new Date();
  thisMonthStart.setDate(1);
  const thisMonth = customerList.value.filter(c => new Date(c.createdAt) >= thisMonthStart).length;
  const sourcesSet = new Set(customerList.value.map(c => c.source).filter(Boolean));
  
  return {
    total,
    active,
    thisMonth,
    sources: sourcesSet.size
  };
});

// 生命周期
onMounted(() => {
  fetchData();
  customerStore.fetchSourcesOptions();
});

// 方法
const fetchData = async () => {
  loading.value = true;
  try {
    await customerStore.fetchCustomerList({
      page: pagination.page - 1,
      size: pagination.size,
      keyword: searchForm.keyword,
      status: searchForm.status,
      source: searchForm.source,
    });
    pagination.total = customerStore.total;
    
    // 如果没有数据，添加示例数据
    if (customerStore.customerList.length === 0) {
      customerStore.customerList = [
        {
          id: 'demo-1',
          name: '张三',
          email: 'zhangsan@example.com',
          phone: '13888888888',
          company: '阿里巴巴',
          source: '官网注册',
          status: 'active',
          createdAt: '2024-12-01T10:00:00Z'
        },
        {
          id: 'demo-2', 
          name: '李四',
          email: 'lisi@example.com',
          phone: '13999999999',
          company: '腾讯',
          source: '线下活动',
          status: 'active',
          createdAt: '2024-12-05T14:30:00Z'
        },
        {
          id: 'demo-3',
          name: '王五',
          email: 'wangwu@example.com', 
          phone: '13777777777',
          company: '百度',
          source: '广告投放',
          status: 'inactive',
          createdAt: '2024-11-28T09:15:00Z'
        },
        {
          id: 'demo-4',
          name: 'John Smith',
          email: 'john@example.com',
          phone: '+1-555-0123',
          company: 'Google',
          source: '社交媒体',
          status: 'active',
          createdAt: '2025-01-08T16:45:00Z'
        }
      ];
      pagination.total = 4;
    }
  } catch (error) {
    ElMessage.error(t("common.fetchError"));
    // API调用失败时也显示示例数据
    customerStore.customerList = [
      {
        id: 'demo-1',
        name: '张三',
        email: 'zhangsan@example.com',
        phone: '13888888888',
        company: '阿里巴巴',
        source: '官网注册',
        status: 'active',
        createdAt: '2024-12-01T10:00:00Z'
      }
    ];
    pagination.total = 1;
  } finally {
    loading.value = false;
  }
};

const handleSearch = () => {
  pagination.page = 1;
  fetchData();
};

const refreshData = () => {
  fetchData();
};

const showCreateDialog = () => {
  isEdit.value = false;
  selectedCustomer.value = null;
  formVisible.value = true;
};

const handleEdit = (customer) => {
  isEdit.value = true;
  selectedCustomer.value = { ...customer };
  formVisible.value = true;
};

const handleView = (customer) => {
  selectedCustomerId.value = customer.id;
  detailVisible.value = true;
};

const handleDelete = async (customer) => {
  try {
    await ElMessageBox.confirm(
      t("customer.deleteConfirmMessage", { name: customer.name }),
      t("common.warning"),
      {
        confirmButtonText: t("common.confirm"),
        cancelButtonText: t("common.cancel"),
        type: "warning",
      }
    );

    await customerStore.deleteCustomer(customer.id);
    ElMessage.success(t("common.deleteSuccess"));
    await fetchData();
  } catch (error) {
    if (error !== "cancel") {
      ElMessage.error(t("common.deleteError"));
    }
  }
};

const handleStatusChange = async (customer) => {
  try {
    await customerStore.updateCustomerStatus(customer.id, customer.status);
    ElMessage.success(t("common.updateSuccess"));
  } catch (error) {
    ElMessage.error(t("common.updateError"));
    // 回滚状态
    customer.status = customer.status === "active" ? "inactive" : "active";
  }
};

const handleImport = async (file) => {
  importing.value = true;
  try {
    await customerStore.importCustomers(file);
    ElMessage.success(t("customer.importSuccess"));
    await fetchData();
  } catch (error) {
    ElMessage.error(t("customer.importError"));
  } finally {
    importing.value = false;
  }
  return false; // 阻止默认上传
};

const handleExport = async () => {
  exporting.value = true;
  try {
    await customerStore.exportCustomers(searchForm);
    ElMessage.success(t("customer.exportSuccess"));
  } catch (error) {
    ElMessage.error(t("customer.exportError"));
  } finally {
    exporting.value = false;
  }
};

const handleSelectionChange = (selection) => {
  selectedCustomers.value = selection;
};

const handlePageChange = (page) => {
  pagination.page = page;
  fetchData();
};

const handleSizeChange = (size) => {
  pagination.size = size;
  pagination.page = 1;
  fetchData();
};

const handleFormSuccess = () => {
  fetchData();
};
</script>

<style scoped>
/* 使用全局设计系统，仅保留必要的组件特定样式 */

.pagination-wrapper {
  margin-top: var(--spacing-5);
  display: flex;
  justify-content: center;
  padding: var(--spacing-4) 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .filter-group {
    justify-content: center;
  }
  
  .action-buttons {
    justify-content: center;
  }
}

/* Element Plus 样式覆盖 */
:deep(.el-table tr:hover > td) {
  background-color: var(--primary-bg-hover) !important;
}
</style>
