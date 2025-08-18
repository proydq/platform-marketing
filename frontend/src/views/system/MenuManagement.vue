<template>
  <div class="page-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="page-title">
        <span class="page-icon">📋</span>
        菜单管理
      </div>
      <div class="page-subtitle">
        管理系统菜单配置，控制导航显示和权限分配
      </div>
      <div class="page-actions">
        <el-button type="primary" class="btn-primary" @click="openAddDialog">
          <el-icon><Plus /></el-icon>
          新增菜单
        </el-button>
        <el-button class="btn-secondary" @click="fetchData">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </div>
    </div>

    <!-- 搜索区域 -->
    <div class="data-table-wrapper">
      <div class="table-header">
        <div class="table-title">🔍 菜单搜索</div>
        <div class="table-subtitle">快速筛选和查找菜单项</div>
      </div>
      <div class="form-section">
        <div class="filter-group">
          <el-input
            v-model="keyword"
            placeholder="搜索菜单名称或路径"
            clearable
            style="width: 280px"
            @keyup.enter="fetchData"
            @clear="fetchData"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </div>
        <div class="action-buttons">
          <el-button class="btn-secondary" @click="fetchData">
            搜索
          </el-button>
        </div>
      </div>
    </div>

    <!-- 数据表格 -->
    <div class="data-table-wrapper">
      <div class="table-header">
        <div class="table-title">📋 菜单列表</div>
        <div class="table-subtitle">系统菜单配置和权限管理</div>
      </div>
      <el-table
        :data="menuList"
        v-loading="loading"
        style="width: 100%"
        :empty-text="'暂无数据'"
        class="modern-table"
      >
          <el-table-column prop="name" label="菜单名" />
          <el-table-column prop="path" label="路径" />
          <el-table-column prop="type" label="类型" />
          <el-table-column prop="status" label="状态">
            <template #default="{ row }">
              <el-switch
                v-model="row.status"
                inline-prompt
                active-text="启"
                inactive-text="禁"
                @change="toggleStatus(row)"
              />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="350" fixed="right">
            <template #default="{ row }">
              <el-button size="small" link @click="openEditDialog(row)">
                <el-icon><Edit /></el-icon>
                编辑
              </el-button>
              <el-button size="small" link @click="viewUsers(row)">
                <el-icon><View /></el-icon>
                查看用户
              </el-button>
              <el-button size="small" link @click="openBindRoleDialog(row)">
                <el-icon><UserFilled /></el-icon>
                绑定角色
              </el-button>
              <el-button size="small" type="danger" link @click="remove(row.id)">
                <el-icon><Delete /></el-icon>
                删除
              </el-button>
            </template>
          </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="size"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          background
          @size-change="fetchData"
          @current-change="fetchData"
        />
      </div>
    </div>

    <!-- 编辑弹窗 -->
    <el-dialog 
      v-model="dialogVisible" 
      :title="isEdit ? '编辑菜单' : '新增菜单'" 
      width="600px"
      :before-close="() => dialogVisible = false"
    >
        <el-form :model="form" label-width="100px" class="dialog-form">
          <el-form-item label="菜单名">
            <el-input v-model="form.name" />
          </el-form-item>
          <el-form-item label="路由path">
            <el-input v-model="form.path" />
          </el-form-item>
          <el-form-item label="权限码">
            <el-input v-model="form.permission" />
          </el-form-item>
          <el-form-item label="组件路径">
            <el-input v-model="form.component" />
          </el-form-item>
          <el-form-item label="父级菜单">
            <el-tree-select
              v-model="form.parentId"
              :data="treeData"
              :props="{ label: 'name', children: 'children' }"
              check-strictly
              clearable
              placeholder="请选择父级"
              style="width: 100%"
            />
          </el-form-item>
          <el-form-item label="菜单类型">
            <el-select v-model="form.type" style="width: 100%">
              <el-option label="菜单" value="menu" />
              <el-option label="按钮" value="button" />
            </el-select>
          </el-form-item>
          <el-form-item label="排序号">
            <el-input-number v-model="form.sort" :min="0" />
          </el-form-item>

          <!-- 新增部分：绑定用户 -->
          <el-form-item label="绑定用户" v-if="isEdit && boundUsers.length > 0">
            <el-tag
              v-for="user in boundUsers"
              :key="user.id"
              class="mr-1"
              size="small"
              type="info"
            >
              {{ user.realName || user.username }}
            </el-tag>
          </el-form-item>

          <el-form-item label="是否缓存">
            <el-switch v-model="form.cache" />
          </el-form-item>
          <el-form-item label="是否隐藏">
            <el-switch v-model="form.hidden" />
          </el-form-item>
          <el-form-item label="是否外链">
            <el-switch v-model="form.external" />
          </el-form-item>
          <el-form-item label="备注">
            <el-input v-model="form.remark" type="textarea" />
          </el-form-item>
        </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="save">保存</el-button>
      </template>
    </el-dialog>

    <!-- 绑定角色弹窗 -->
    <el-dialog v-model="bindRoleDialogVisible" title="绑定角色" width="500px">
      <el-checkbox-group v-model="selectedRoleIds">
        <el-checkbox v-for="role in allRoles" :key="role.id" :label="role.id">
          {{ role.name }}
        </el-checkbox>
      </el-checkbox-group>
      <template #footer>
        <el-button @click="bindRoleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveBindRoles">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import {
  Search,
  Refresh,
  Plus,
  Edit,
  Delete,
  View,
  UserFilled,
} from "@element-plus/icons-vue";
import {
  fetchMenus,
  fetchMenuTree,
  createMenu,
  updateMenu,
  deleteMenu,
  updateMenuStatus,
  fetchUsersByMenu,
  fetchAllRoles,
  fetchRolesByMenu,
  assignRolesToMenu,
} from "../../api/menu";
import "@/assets/css/permission-ui-enhanced.css";

const menuList = ref([]);
const total = ref(0);
const page = ref(1);
const size = ref(10);
const keyword = ref("");
const loading = ref(false);
const dialogVisible = ref(false);
const isEdit = ref(false);
const saving = ref(false);
const treeData = ref([]);
const boundUsers = ref([]);

const form = reactive({
  id: "",
  name: "",
  path: "",
  permission: "",
  component: "",
  parentId: null,
  type: "menu",
  sort: 0,
  cache: false,
  hidden: false,
  external: false,
  remark: "",
  status: true,
});

onMounted(() => {
  fetchData();
  loadTree();
});

function fetchData() {
  loading.value = true;
  fetchMenus({ page: page.value - 1, size: size.value, keyword: keyword.value })
    .then((res) => {
      menuList.value = res.data.rows || [];
      total.value = res.data.total || 0;
    })
    .finally(() => (loading.value = false));
}

function loadTree() {
  fetchMenuTree().then((res) => {
    treeData.value = res.data || [];
  });
}

function openAddDialog() {
  isEdit.value = false;
  Object.assign(form, {
    id: "",
    name: "",
    path: "",
    permission: "",
    component: "",
    parentId: null,
    type: "menu",
    sort: 0,
    cache: false,
    hidden: false,
    external: false,
    remark: "",
    status: true,
  });
  boundUsers.value = []; // 清空绑定用户
  dialogVisible.value = true;
}

async function openEditDialog(row) {
  isEdit.value = true;
  Object.assign(form, row);
  dialogVisible.value = true;

  // 加载绑定用户
  const res = await fetchUsersByMenu(row.id);
  boundUsers.value = res.data || [];
}

async function viewUsers(row) {
  const res = await fetchUsersByMenu(row.id);
  ElMessage.info(`${res.data?.length || 0} 个用户拥有此权限`);
}

function save() {
  saving.value = true;
  const payload = { ...form };
  const handler = isEdit.value ? updateMenu.bind(null, form.id) : createMenu;
  handler(payload)
    .then(() => {
      ElMessage.success("保存成功");
      dialogVisible.value = false;
      fetchData();
      loadTree();
    })
    .finally(() => (saving.value = false));
}

function remove(id) {
  ElMessageBox.confirm("确认删除该菜单吗？", "警告", { type: "warning" })
    .then(() => deleteMenu(id))
    .then(() => {
      ElMessage.success("删除成功");
      fetchData();
      loadTree();
    });
}

function toggleStatus(row) {
  updateMenuStatus(row.id, row.status)
    .then(() => ElMessage.success("状态更新成功"))
    .catch(() => ElMessage.error("更新失败"));
}
const bindRoleDialogVisible = ref(false);
const allRoles = ref([]);
const selectedRoleIds = ref([]);
const currentMenuId = ref("");

// 打开绑定弹窗
async function openBindRoleDialog(row) {
  currentMenuId.value = row.id;
  bindRoleDialogVisible.value = true;

  const res1 = await fetchAllRoles(); // 获取所有角色
  const res2 = await fetchRolesByMenu(row.id); // 获取已绑定的角色ID列表

  allRoles.value = res1.data || [];
  selectedRoleIds.value = res2.data.map((role) => role.id) || [];
}

// 保存绑定角色
async function saveBindRoles() {
  await assignRolesToMenu(currentMenuId.value, selectedRoleIds.value);
  ElMessage.success("绑定成功");
  bindRoleDialogVisible.value = false;
}
</script>

<style scoped>
/* 使用全局设计系统，仅保留必要的组件特定样式 */

.pagination-wrapper {
  margin-top: var(--spacing-5);
  display: flex;
  justify-content: center;
  padding: var(--spacing-4) 0;
}

.mr-1 {
  margin-right: 6px;
}

/* 对话框表单样式 */
.dialog-form {
  padding: var(--spacing-4);
}

.dialog-form .el-form-item {
  margin-bottom: var(--spacing-4);
}

/* 响应式设计 */
@media (max-width: 768px) {
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
