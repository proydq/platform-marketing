<template>
  <div class="page-wrapper">
    <div class="action-buttons">
      <el-select
        v-model="filterPlatform"
        placeholder="平台"
        style="width: 120px"
        @change="loadAccounts"
      >
        <el-option label="全部" value="" />
        <el-option v-for="p in platforms" :key="p" :label="p" :value="p" />
      </el-select>
      <el-input
        v-model="keyword"
        placeholder="搜索账号"
        clearable
        style="width: 200px"
        @input="loadAccounts"
      />
      <el-button type="primary" @click="openAdd">
        <span class="icon">➕</span>添加账号
      </el-button>
    </div>

    <el-row :gutter="20">
      <el-col
        v-for="acc in filteredAccounts"
        :key="acc.id"
        :xs="24"
        :sm="12"
        :md="8"
        style="margin-bottom: 20px"
      >
        <el-card>
          <template #header>
            <div
              style="
                display: flex;
                justify-content: space-between;
                align-items: center;
              "
            >
              <span>{{ acc.platform }}</span>
              <div>
                <el-tooltip content="编辑">
                  <el-button text size="small" @click="openEdit(acc)">
                    <el-icon><Edit /></el-icon>
                  </el-button>
                </el-tooltip>
                <el-tooltip content="解绑">
                  <el-button
                    text
                    size="small"
                    style="color: #f56c6c"
                    @click="unbind(acc)"
                  >
                    <el-icon><Delete /></el-icon>
                  </el-button>
                </el-tooltip>
                <el-tooltip content="推送日志">
                  <el-button text size="small" @click="openLogs(acc)">
                    <el-icon><Document /></el-icon>
                  </el-button>
                </el-tooltip>
              </div>
            </div>
          </template>
          <div style="margin-bottom: 10px">
            <span
              :class="[
                'status-badge',
                acc.status === '已绑定' ? 'status-success' : 'status-error',
              ]"
            >
              {{ acc.status }}
            </span>
          </div>
          <p>{{ acc.name }}</p>
          <p style="color: #999; font-size: 12px">绑定于 {{ acc.bindTime }}</p>
        </el-card>
      </el-col>
    </el-row>

    <el-drawer
      v-model="drawerVisible"
      :title="drawerMode === 'add' ? '添加账号' : '编辑账号'"
      size="480px"
      direction="rtl"
    >
      <el-form :model="form" label-width="90px" class="form-section">
        <el-form-item label="平台类型">
          <el-select v-model="form.platform">
            <el-option label="Facebook" value="Facebook" />
            <el-option label="LinkedIn" value="LinkedIn" />
            <el-option label="Twitter" value="Twitter" />
          </el-select>
        </el-form-item>
        <el-form-item label="账号名">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="AccessToken">
          <el-input v-model="form.accessToken" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.note" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="drawerVisible = false">取消</el-button>
        <el-button type="primary" @click="saveForm">保存</el-button>
      </template>
    </el-drawer>

    <el-dialog v-model="logDialogVisible" title="推送日志" width="600px">
      <el-table :data="logsForAccount" style="width: 100%">
        <el-table-column prop="time" label="时间" width="160" />
        <el-table-column prop="content" label="内容" />
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <span
              :class="[
                'status-badge',
                row.status === '成功' ? 'status-success' : 'status-error',
              ]"
            >
              {{ row.status }}
            </span>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Edit, Delete, Document } from "@element-plus/icons-vue";
import {
  getSocialAccounts,
  getSocialAccountById,
  createSocialAccount,
  updateSocialAccount,
  deleteSocialAccount,
  getPushLogs,
} from "@/api/socialAccount";

const accounts = ref([]);
const logs = ref([]);
const filterPlatform = ref("");
const keyword = ref("");
const drawerVisible = ref(false);
const drawerMode = ref("add");
const form = ref({
  id: null,
  platform: "",
  name: "",
  accessToken: "",
  note: "",
});
const logDialogVisible = ref(false);
const currentAccountId = ref(null);

const platforms = computed(() => {
  const set = new Set(accounts.value.map((a) => a.platform));
  return Array.from(set);
});

const filteredAccounts = computed(() => {
  let list = accounts.value;
  if (filterPlatform.value) {
    list = list.filter((a) => a.platform === filterPlatform.value);
  }
  if (keyword.value) {
    list = list.filter((a) => a.name.includes(keyword.value));
  }
  return list;
});

onMounted(() => {
  loadAccounts();
});

async function loadAccounts() {
  const res = await getSocialAccounts({
    platform: filterPlatform.value,
    keyword: keyword.value,
  });
  accounts.value = res.data?.rows || [];
}

function openAdd() {
  drawerMode.value = "add";
  form.value = {
    id: null,
    platform: "",
    name: "",
    accessToken: "",
    note: "",
  };
  drawerVisible.value = true;
}

function openEdit(acc) {
  drawerMode.value = "edit";
  form.value = { ...acc };
  drawerVisible.value = true;
}

async function saveForm() {
  const payload = {
    platform: form.value.platform,
    name: form.value.name,
    accessToken: form.value.accessToken,
    note: form.value.note,
  };

  if (drawerMode.value === "edit" && form.value.id) {
    await updateSocialAccount(form.value.id, payload);
    ElMessage.success("账号已更新");
  } else {
    await createSocialAccount(payload);
    ElMessage.success("账号已添加");
  }

  drawerVisible.value = false;
  await loadAccounts();
}

async function unbind(acc) {
  await ElMessageBox.confirm("确定解绑该账号吗?", "提示", { type: "warning" });
  await deleteSocialAccount(acc.id);
  ElMessage.success("已解绑");
  await loadAccounts();
}

async function openLogs(acc) {
  currentAccountId.value = acc.id;
  const res = await getPushLogs({ accountId: acc.id });
  logs.value = res.data?.rows || [];
  logDialogVisible.value = true;
}

const logsForAccount = computed(() =>
  logs.value.filter((l) => l.accountId === currentAccountId.value)
);
</script>
