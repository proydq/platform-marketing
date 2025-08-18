<template>
  <div class="role-management">
    <div class="action-bar">
      <el-button type="primary" class="btn-primary" @click="openDialog(false)">
        <el-icon><Plus /></el-icon>
        新建角色
      </el-button>
    </div>

    <el-table
      :data="roles"
      v-loading="loading"
      style="width: 100%"
      :empty-text="'暂无角色数据'"
      class="modern-table"
    >
      <el-table-column prop="name" label="角色名称" />
      <el-table-column prop="description" label="角色描述" />
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button size="small" link @click="openDialog(true, row)">
            <el-icon><Edit /></el-icon>
            编辑
          </el-button>
          <el-button size="small" type="danger" link @click="remove(row.id)">
            <el-icon><Delete /></el-icon>
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>

  <el-drawer
    class="page-dialog"
    v-model="drawerVisible"
    direction="rtl"
    size="400px"
  >
    <template #title>
      <strong>{{ isEdit ? "编辑角色" : "新增角色" }}</strong>
    </template>
    <el-form class="dialog-form" :model="form" label-width="80px">
      <el-form-item label="名称">
        <el-input v-model="form.name" />
      </el-form-item>
      <el-form-item label="描述">
        <el-input v-model="form.description" />
      </el-form-item>
    </el-form>

    <el-tree
      ref="treeRef"
      :data="treeData"
      node-key="id"
      show-checkbox
      :props="{ label: 'name', children: 'children' }"
      :default-checked-keys="checkedKeys"
      class="permission-tree"
    />

    <template #footer>
      <el-button @click="drawerVisible = false">取消</el-button>
      <el-button type="primary" :loading="saving" @click="save">保存</el-button>
    </template>
  </el-drawer>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import { ElMessage } from "element-plus";
import { Plus, Edit, Delete } from "@element-plus/icons-vue";
import {
  fetchRoles,
  createRole,
  updateRole,
  deleteRole,
  fetchRolePermissions,
  bindPermissions,
} from "../../api/role";
import { fetchPermissionTree } from "../../api/permission";
import "@/assets/css/permission-ui-enhanced.css";

const roles = ref([]);
const loading = ref(false);
const saving = ref(false);
const treeData = ref([]);
const checkedKeys = ref([]);
const drawerVisible = ref(false);
const isEdit = ref(false);
const form = reactive({ id: "", name: "", description: "" });
const treeRef = ref();

onMounted(() => {
  loadRoles();
  loadPermissionTree();
});

function loadRoles() {
  loading.value = true;
  fetchRoles()
    .then((res) => {
      roles.value = res.data || [];
    })
    .finally(() => (loading.value = false));
}

function loadPermissionTree() {
  fetchPermissionTree().then((res) => {
    const data = res.data || [];
    treeData.value = data.map((m) => ({ ...m, disabled: true }));
  });
}

function openDialog(edit = false, data = null) {
  isEdit.value = edit;
  if (edit && data) {
    Object.assign(form, data);
    fetchRolePermissions(data.id).then((res) => {
      checkedKeys.value = res.data || [];
    });
  } else {
    Object.assign(form, { id: "", name: "", description: "" });
    checkedKeys.value = [];
  }
  drawerVisible.value = true;
}

async function save() {
  saving.value = true;
  const handler = isEdit.value ? updateRole : createRole;
  const payload = { ...form };

  try {
    // 第一步：创建/更新角色
    const res = await handler(payload);
    const roleId = res.data?.id || form.id;

    // 第二步：绑定权限（重要！）
    const permissionIds = treeRef.value?.getCheckedKeys() || [];

    await bindPermissions(roleId, permissionIds);

    ElMessage.success("保存成功");
    drawerVisible.value = false;
    loadRoles();
  } catch (e) {
    ElMessage.error("保存失败");
  } finally {
    saving.value = false;
  }
}

function remove(id) {
  deleteRole(id).then(() => {
    ElMessage.success("删除成功");
    loadRoles();
  });
}
</script>

<style scoped>
.role-management {
  padding: var(--spacing-4);
}

.action-bar {
  margin-bottom: var(--spacing-6);
  display: flex;
  justify-content: flex-start;
}

.permission-tree {
  margin-top: var(--spacing-4);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-lg);
  padding: var(--spacing-4);
  max-height: 300px;
  overflow: auto;
  background: var(--bg-secondary);
}

/* Element Plus 样式覆盖 */
:deep(.el-table tr:hover > td) {
  background-color: var(--primary-bg-hover) !important;
}

:deep(.el-drawer__header) {
  padding: var(--spacing-6);
  border-bottom: 1px solid var(--border-secondary);
}

:deep(.el-drawer__body) {
  padding: var(--spacing-6);
}
</style>
