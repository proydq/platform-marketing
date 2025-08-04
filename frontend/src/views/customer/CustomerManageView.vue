<template>
  <div class="page-wrapper">
    <el-card class="card-container">
      <div class="action-buttons">
        <el-input
          v-model="keyword"
          placeholder="搜索客户"
          clearable
          style="width: 240px"
          @keyup.enter="fetchData"
        />
        <el-button type="primary" @click="openAdd">新增客户</el-button>
        <el-upload
          action="#"
          :show-file-list="false"
          accept=".csv,.xlsx"
          :before-upload="handleCustomerImport"
        >
          <el-button type="primary">导入客户</el-button>
        </el-upload>
      </div>

      <el-table
        :data="list"
        border
        size="small"
        v-loading="loading"
        style="width: 100%"
      >
        <el-table-column prop="name" label="名称" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="phone" label="电话" />
        <el-table-column prop="source" label="来源" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              active-value="active"
              inactive-value="inactive"
              inline-prompt
              active-text="启"
              inactive-text="禁"
              @change="changeStatus(row)"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220">
          <template #default="{ row }">
            <el-button
              v-if="hasPermission('customer:view')"
              size="small"
              type="primary"
              @click="openDetail(row)"
              >查看</el-button
            >
            <el-button size="small" @click="openEdit(row)">编辑</el-button>
            <el-button
              v-if="hasPermission('customer:delete')"
              size="small"
              type="danger"
              @click="remove(row)"
              >删除</el-button
            >
          </template>
        </el-table-column>
      </el-table>

      <div class="text-right mt-4">
        <el-pagination
          background
          v-model:current-page="page"
          v-model:page-size="size"
          :total="total"
          layout="total, prev, pager, next"
          @current-change="fetchData"
        />
      </div>
    </el-card>

    <!-- 客户编辑弹窗 -->
    <el-dialog v-model="dialogVisible" width="500px">
      <template #title>
        <strong>{{ isEdit ? "编辑客户" : "新增客户" }}</strong>
      </template>
      <el-form :model="form" label-width="80px">
        <el-form-item label="名称"
          ><el-input v-model="form.name"
        /></el-form-item>
        <el-form-item label="邮箱"
          ><el-input v-model="form.email"
        /></el-form-item>
        <el-form-item label="电话"
          ><el-input v-model="form.phone"
        /></el-form-item>
        <el-form-item label="来源"
          ><el-input v-model="form.source"
        /></el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status" style="width: 100%">
            <el-option label="启用" value="active" />
            <el-option label="禁用" value="inactive" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注"
          ><el-input v-model="form.remark" type="textarea"
        /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="save"
          >保存</el-button
        >
      </template>
    </el-dialog>

    <!-- 客户详情抽屉 -->
    <CustomerDetailDrawer ref="detailDrawer" />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { hasPermission } from "@/composables/permission";
import CustomerDetailDrawer from "@/components/customer/CustomerDetailDrawer.vue";
import {
  getCustomerList,
  createCustomer,
  updateCustomer,
  deleteCustomer,
  updateCustomerStatus,
} from "@/api/customer";

const list = ref([]);
const total = ref(0);
const page = ref(1);
const size = ref(10);
const keyword = ref("");
const loading = ref(false);

const dialogVisible = ref(false);
const isEdit = ref(false);
const saving = ref(false);
const detailDrawer = ref();

const form = reactive({
  id: "",
  name: "",
  email: "",
  phone: "",
  source: "",
  status: "active",
  remark: "",
});

onMounted(fetchData);

function fetchData() {
  loading.value = true;
  getCustomerList({
    page: page.value - 1,
    size: size.value,
    keyword: keyword.value,
  })
    .then((res) => {
      list.value = res.data.rows || [];
      total.value = res.data.total || 0;
    })
    .finally(() => (loading.value = false));
}

function openAdd() {
  isEdit.value = false;
  Object.assign(form, {
    id: "",
    name: "",
    email: "",
    phone: "",
    source: "",
    status: "active",
    remark: "",
  });
  dialogVisible.value = true;
}

function openEdit(row) {
  isEdit.value = true;
  Object.assign(form, row);
  dialogVisible.value = true;
}

function openDetail(row) {
  detailDrawer.value.open(row.id);
}

function save() {
  saving.value = true;
  const handler = isEdit.value
    ? updateCustomer.bind(null, form.id)
    : createCustomer;
  handler({ ...form })
    .then(() => {
      ElMessage.success("保存成功");
      dialogVisible.value = false;
      fetchData();
    })
    .finally(() => (saving.value = false));
}

function remove(row) {
  ElMessageBox.confirm("确定删除该客户吗？", "警告", { type: "warning" })
    .then(() => deleteCustomer(row.id))
    .then(() => {
      ElMessage.success("删除成功");
      fetchData();
    });
}

function changeStatus(row) {
  updateCustomerStatus({ id: row.id, status: row.status })
    .then(() => ElMessage.success("状态已更新"))
    .catch(() => ElMessage.error("更新失败"));
}

const handleCustomerImport = (file) => {
  console.log("📦 导入客户文件名：", file.name);
  return false; // 阻止自动上传，保留文件解析能力
};
</script>

<style scoped>
.page-wrapper {
  display: flex;
  flex-direction: column;
  height: 100vh;
  box-sizing: border-box;
  background-color: #f5f7fa;
}

.card-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  border-radius: 12px;
  overflow: hidden;
}

.el-card__body {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 20px;
}

.action-buttons {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  gap: 16px;
}

.el-table {
  flex: 1;
  overflow: auto;
}
</style>
