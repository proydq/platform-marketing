<template>
  <div class="customer-list-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2>{{ $t("customer.title") }}</h2>
      <p>{{ $t("customer.description") }}</p>
    </div>

    <el-card class="content-card">
      <!-- 操作栏 -->
      <div class="action-bar">
        <!-- 左侧：搜索和筛选 -->
        <div class="search-section">
          <el-input
            v-model="searchForm.keyword"
            :placeholder="$t('customer.searchPlaceholder')"
            clearable
            style="width: 280px"
            @keyup.enter="handleSearch"
            @clear="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>

          <el-select
            v-model="searchForm.status"
            :placeholder="$t('customer.statusFilter')"
            clearable
            style="width: 150px; margin-left: 12px"
            @change="handleSearch"
          >
            <el-option :label="$t('customer.statusActive')" value="active" />
            <el-option
              :label="$t('customer.statusInactive')"
              value="inactive"
            />
          </el-select>

          <el-select
            v-model="searchForm.source"
            :placeholder="$t('customer.sourceFilter')"
            clearable
            style="width: 150px; margin-left: 12px"
            @change="handleSearch"
          >
            <el-option
              v-for="source in sourcesOptions"
              :key="source"
              :label="source"
              :value="source"
            />
          </el-select>
        </div>

        <!-- 右侧：操作按钮 -->
        <div class="action-buttons">
          <el-button @click="refreshData">
            <el-icon><Refresh /></el-icon>
            {{ $t("common.refresh") }}
          </el-button>

          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            {{ $t("customer.addCustomer") }}
          </el-button>

          <el-upload
            action="#"
            :show-file-list="false"
            accept=".csv,.xlsx,.xls"
            :before-upload="handleImport"
            :disabled="importing"
          >
            <el-button :loading="importing">
              <el-icon><Upload /></el-icon>
              {{ $t("customer.importCustomer") }}
            </el-button>
          </el-upload>

          <el-button @click="handleExport" :loading="exporting">
            <el-icon><Download /></el-icon>
            {{ $t("customer.exportCustomer") }}
          </el-button>
        </div>
      </div>

      <!-- 数据表格 -->
      <el-table
        :data="customerList"
        v-loading="loading"
        border
        stripe
        style="width: 100%"
        :empty-text="$t('common.noData')"
        @selection-change="handleSelectionChange"
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
    </el-card>

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
  } catch (error) {
    ElMessage.error(t("common.fetchError"));
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

const handleAdd = () => {
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
.customer-list-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.page-header p {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.content-card {
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.action-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 16px;
}

.search-section {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
}

.action-buttons {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .customer-list-container {
    padding: 12px;
  }

  .action-bar {
    flex-direction: column;
    align-items: stretch;
  }

  .search-section,
  .action-buttons {
    justify-content: center;
  }
}
</style>
