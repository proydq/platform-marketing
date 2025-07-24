<template>
  <div class="page-wrapper">
    <el-card class="card-container">
      <div class="action-bar">
        <el-space>
          <el-select
            v-model="filters.type"
            :placeholder="$t('notification.type')"
            style="width: 120px"
          >
            <el-option :label="$t('common.all')" value="" />
            <el-option label="Message" value="message" />
            <el-option label="Task" value="task" />
            <el-option label="System" value="system" />
          </el-select>
          <el-select
            v-model="filters.status"
            :placeholder="$t('notification.status')"
            style="width: 120px"
          >
            <el-option :label="$t('common.all')" value="" />
            <el-option :label="$t('notification.unread')" value="unread" />
            <el-option :label="$t('notification.read')" value="read" />
          </el-select>
        </el-space>

        <el-space>
          <el-button
            size="small"
            :disabled="!selected.length"
            @click="markSelectedRead"
            >{{ $t("notification.markRead") }}</el-button
          >
          <el-button
            size="small"
            type="danger"
            :disabled="!selected.length"
            @click="deleteSelected"
            >{{ $t("notification.delete") }}</el-button
          >
        </el-space>
      </div>

      <div class="notification-list">
        <transition-group name="fade-list" tag="div">
          <el-card
            v-for="item in list"
            :key="item.id"
            class="notify-card"
            body-style="padding: 0"
          >
            <el-row class="notify-row" align="middle" style="padding: 20px">
              <el-col :span="1">
                <el-checkbox
                  :model-value="selected.includes(item.id)"
                  @change="toggleSelect(item.id)"
                />
              </el-col>
              <el-col :span="1" class="icon-col">{{ iconFor(item) }}</el-col>
              <el-col :span="11">
                <div class="title-line">
                  <span class="title">{{ item.title }}</span>
                  <span
                    class="status-badge"
                    :class="
                      item.status === 'unread' ? 'status-unread' : 'status-read'
                    "
                  >
                    {{
                      item.status === "unread"
                        ? $t("notification.unread")
                        : $t("notification.read")
                    }}
                  </span>
                </div>
                <div class="desc text-gray">{{ item.content }}</div>
              </el-col>
              <el-col :span="6" class="time-col">
                {{ formatTime(item.time) }}
              </el-col>
              <el-col :span="5" class="action-col">
                <el-button text size="small" @click.stop="view(item)">
                  {{ $t("notification.view") }}
                </el-button>
                <el-button text size="small" @click.stop="markRead(item)">
                  {{ $t("notification.markRead") }}
                </el-button>
                <el-button
                  text
                  size="small"
                  style="color: #f56c6c"
                  @click.stop="remove(item)"
                >
                  {{ $t("notification.delete") }}
                </el-button>
              </el-col>
            </el-row>
          </el-card>
        </transition-group>
      </div>

      <el-drawer
        v-model="drawer"
        :title="$t('notification.detail')"
        size="30%"
        direction="rtl"
      >
        <h3>{{ current.title }}</h3>
        <p class="text-gray">{{ formatTime(current.time) }}</p>
        <p style="margin: 10px 0">{{ current.content }}</p>
        <a
          v-if="current.link"
          :href="current.link"
          target="_blank"
          style="color: #409eff"
        >
          {{ $t("notification.view") }}
        </a>
      </el-drawer>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { ElMessage } from "element-plus";
import { useI18n } from "vue-i18n";
import {
  getNotificationList,
  markNotificationRead,
  deleteNotification,
  getNotificationDetail,
  batchMarkRead,
  batchDelete,
} from "@/api/notification";

const list = ref([]);
const filters = ref({ type: "", status: "" });
const selected = ref([]);
const drawer = ref(false);
const current = ref({});

const { t } = useI18n();

onMounted(loadData);

async function loadData() {
  const res = await getNotificationList(filters.value);
  list.value = res.data?.rows || [];
}

function formatTime(t) {
  if (!t || isNaN(Date.parse(t))) return "-";
  const time = new Date(t);
  const diff = Date.now() - time.getTime();
  const mins = Math.floor(diff / 60000);
  if (mins < 60) return `${mins}${t("notification.minutesAgo")}`;
  return time.toLocaleString();
}

const iconMap = { message: "🔔", task: "📋", system: "⚠️" };
function iconFor(item) {
  return iconMap[item.type] || "🔔";
}

function toggleSelect(id) {
  const i = selected.value.indexOf(id);
  if (i === -1) selected.value.push(id);
  else selected.value.splice(i, 1);
}

async function view(item) {
  const res = await getNotificationDetail(item.id);
  current.value = res.data || item;
  drawer.value = true;
  if (item.status === "unread") markRead(item);
}

async function markRead(item) {
  await markNotificationRead(item.id);
  item.status = "read";
  ElMessage.success(t("notification.markRead"));
  selected.value = selected.value.filter((i) => i !== item.id);
}

async function remove(item) {
  await deleteNotification(item.id);
  list.value = list.value.filter((n) => n.id !== item.id);
  selected.value = selected.value.filter((i) => i !== item.id);
  ElMessage.success(t("notification.delete"));
}

async function markSelectedRead() {
  await batchMarkRead(selected.value);
  list.value.forEach((n) => {
    if (selected.value.includes(n.id)) n.status = "read";
  });
  selected.value = [];
  ElMessage.success(t("notification.markRead"));
}

async function deleteSelected() {
  await batchDelete(selected.value);
  list.value = list.value.filter((n) => !selected.value.includes(n.id));
  selected.value = [];
  ElMessage.success(t("notification.delete"));
}
</script>

<style scoped>
.page-wrapper {
  display: flex;
  flex-direction: column;
  height: 100vh;
  padding: 16px;
  background-color: #f5f7fa;
  box-sizing: border-box;
}

.card-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  border-radius: 12px;
}

.el-card__body {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 20px;
}

.action-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.notification-list {
  flex: 1;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.notify-card {
  border-radius: 8px;
  overflow: hidden;
}

.notify-row {
  padding: 10px;
}

.title-line {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-weight: 600;
}

.desc {
  color: #666;
  margin-top: 4px;
}

.status-badge {
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 12px;
  background-color: #f2f2f2;
  color: #666;
}

.status-unread {
  background-color: #409eff20;
  color: #409eff;
}

.status-read {
  background-color: #e5e5e5;
  color: #999;
}

.time-col {
  color: #999;
  font-size: 13px;
}

.icon-col {
  font-size: 18px;
  text-align: center;
}
</style>
