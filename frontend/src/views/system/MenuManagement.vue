<template>
  <div class="page-wrapper">
    <el-card class="page-card">
      <!-- 顶部工具栏 -->
      <div class="toolbar mb-4 flex justify-between items-center gap-2">
        <el-input
          v-model="keyword"
          placeholder="搜索菜单"
          clearable
          style="width: 240px"
        />
        <el-button type="primary" @click="openAddDialog">新增菜单</el-button>
      </div>

      <!-- 表格区域 -->
      <div class="table-wrapper">
        <el-table
          :data="menuList"
          border
          size="small"
          v-loading="loading"
          style="width: 100%"
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
          <el-table-column label="操作" width="350">
            <template #default="{ row }">
              <el-button size="small" @click="openEditDialog(row)"
                >编辑</el-button
              >
              <el-button size="small" type="danger" @click="remove(row.id)"
                >删除</el-button
              >
              <el-button size="small" @click="viewUsers(row)"
                >查看用户</el-button
              >
              <el-button size="small" @click="openBindRoleDialog(row)"
                >绑定角色</el-button
              >
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 底部分页器 -->
      <div class="footer-pagination">
        <el-pagination
          background
          v-model:current-page="page"
          v-model:page-size="size"
          :total="total"
          layout="total, prev, pager, next"
          @current-change="fetchData"
        />
      </div>

      <!-- 编辑弹窗 -->
      <el-dialog class="page-dialog" v-model="dialogVisible" width="600px">
        <template #title>
          <strong>{{ isEdit ? "编辑菜单" : "新增菜单" }}</strong>
        </template>
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
          <el-button type="primary" :loading="saving" @click="save"
            >保存</el-button
          >
        </template>
      </el-dialog>
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
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
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
.page-card {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}
.table-wrapper {
  flex: 1;
  overflow: auto;
}
.mr-1 {
  margin-right: 6px;
}
</style>
