<template>
  <el-dialog
    v-model="visible"
    :title="
      isEdit
        ? $t('customer.acquisition.editTask')
        : $t('customer.acquisition.createTask')
    "
    width="800px"
    :destroy-on-close="true"
    @close="handleClose"
  >
    <el-form
      ref="taskFormRef"
      :model="formData"
      :rules="formRules"
      label-width="120px"
      label-position="left"
    >
      <el-tabs v-model="activeTab" type="border-card">
        <!-- 基本信息 -->
        <el-tab-pane :label="$t('customer.acquisition.basicInfo')" name="basic">
          <el-form-item
            :label="$t('customer.acquisition.taskName')"
            prop="name"
          >
            <el-input
              v-model="formData.name"
              :placeholder="$t('customer.acquisition.taskNamePlaceholder')"
              clearable
            />
          </el-form-item>

          <el-form-item
            :label="$t('customer.acquisition.description')"
            prop="description"
          >
            <el-input
              v-model="formData.description"
              type="textarea"
              :rows="3"
              :placeholder="$t('customer.acquisition.descriptionPlaceholder')"
            />
          </el-form-item>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item
                :label="$t('customer.acquisition.type')"
                prop="type"
              >
                <el-select v-model="formData.type" style="width: 100%">
                  <el-option
                    :label="$t('customer.acquisition.typeCustomer')"
                    value="customer"
                  />
                  <el-option
                    :label="$t('customer.acquisition.typeComment')"
                    value="comment"
                  />
                  <el-option
                    :label="$t('customer.acquisition.typeArticle')"
                    value="article"
                  />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item
                :label="$t('customer.acquisition.priority')"
                prop="priority"
              >
                <el-select v-model="formData.priority" style="width: 100%">
                  <el-option
                    :label="$t('customer.acquisition.priorityLow')"
                    value="low"
                  />
                  <el-option
                    :label="$t('customer.acquisition.priorityMedium')"
                    value="medium"
                  />
                  <el-option
                    :label="$t('customer.acquisition.priorityHigh')"
                    value="high"
                  />
                  <el-option
                    :label="$t('customer.acquisition.priorityUrgent')"
                    value="urgent"
                  />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>

          <el-form-item
            :label="$t('customer.acquisition.platform')"
            prop="platforms"
          >
            <el-checkbox-group
              v-model="formData.platforms"
              class="platform-checkboxes"
            >
              <el-checkbox
                v-for="platform in platformOptions"
                :key="platform.value"
                :label="platform.value"
              >
                <span class="platform-label">
                  {{ platform.icon }} {{ platform.label }}
                </span>
              </el-checkbox>
            </el-checkbox-group>
          </el-form-item>
        </el-tab-pane>

        <!-- 采集配置 -->
        <el-tab-pane
          :label="$t('customer.acquisition.collectionConfig')"
          name="collection"
        >
          <el-form-item
            :label="$t('customer.acquisition.keywords')"
            prop="keywords"
          >
            <el-input
              v-model="formData.keywords"
              type="textarea"
              :rows="3"
              :placeholder="$t('customer.acquisition.keywordsPlaceholder')"
            />
            <div class="form-tip">
              {{ $t("customer.acquisition.keywordsTip") }}
            </div>
          </el-form-item>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item
                :label="$t('customer.acquisition.region')"
                prop="region"
              >
                <el-select v-model="formData.region" style="width: 100%">
                  <el-option
                    v-for="region in regionOptions"
                    :key="region.value"
                    :label="region.label"
                    :value="region.value"
                  />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item
                :label="$t('customer.acquisition.targetAmount')"
                prop="amount"
              >
                <el-input-number
                  v-model="formData.amount"
                  :min="1"
                  :max="50000"
                  :step="10"
                  style="width: 100%"
                />
              </el-form-item>
            </el-col>
          </el-row>

          <el-form-item
            :label="$t('customer.acquisition.fields')"
            prop="fields"
          >
            <el-checkbox-group
              v-model="formData.fields"
              class="fields-checkboxes"
            >
              <el-checkbox value="name">{{ $t("customer.name") }}</el-checkbox>
              <el-checkbox value="email">{{
                $t("customer.email")
              }}</el-checkbox>
              <el-checkbox value="phone">{{
                $t("customer.phone")
              }}</el-checkbox>
              <el-checkbox value="company">{{
                $t("customer.company")
              }}</el-checkbox>
              <el-checkbox value="title">{{
                $t("customer.acquisition.jobTitle")
              }}</el-checkbox>
              <el-checkbox value="location">{{
                $t("customer.acquisition.location")
              }}</el-checkbox>
              <el-checkbox value="social">{{
                $t("customer.acquisition.socialProfile")
              }}</el-checkbox>
              <el-checkbox value="website">{{
                $t("customer.acquisition.website")
              }}</el-checkbox>
            </el-checkbox-group>
          </el-form-item>

          <el-form-item
            :label="$t('customer.acquisition.filters')"
            prop="filters"
          >
            <el-input
              v-model="formData.filters"
              type="textarea"
              :rows="3"
              :placeholder="$t('customer.acquisition.filtersPlaceholder')"
            />
            <div class="form-tip">
              {{ $t("customer.acquisition.filtersTip") }}
            </div>
          </el-form-item>
        </el-tab-pane>

        <!-- 调度配置 -->
        <el-tab-pane
          :label="$t('customer.acquisition.scheduleConfig')"
          name="schedule"
        >
          <el-form-item :label="$t('customer.acquisition.cycle')" prop="cycle">
            <el-radio-group v-model="formData.cycle">
              <el-radio value="once">
                <div class="cycle-option">
                  <div class="cycle-title">
                    {{ $t("customer.acquisition.cycleOnce") }}
                  </div>
                  <div class="cycle-desc">
                    {{ $t("customer.acquisition.cycleOnceDesc") }}
                  </div>
                </div>
              </el-radio>
              <el-radio value="daily">
                <div class="cycle-option">
                  <div class="cycle-title">
                    {{ $t("customer.acquisition.cycleDaily") }}
                  </div>
                  <div class="cycle-desc">
                    {{ $t("customer.acquisition.cycleDailyDesc") }}
                  </div>
                </div>
              </el-radio>
              <el-radio value="weekly">
                <div class="cycle-option">
                  <div class="cycle-title">
                    {{ $t("customer.acquisition.cycleWeekly") }}
                  </div>
                  <div class="cycle-desc">
                    {{ $t("customer.acquisition.cycleWeeklyDesc") }}
                  </div>
                </div>
              </el-radio>
              <el-radio value="monthly">
                <div class="cycle-option">
                  <div class="cycle-title">
                    {{ $t("customer.acquisition.cycleMonthly") }}
                  </div>
                  <div class="cycle-desc">
                    {{ $t("customer.acquisition.cycleMonthlyDesc") }}
                  </div>
                </div>
              </el-radio>
            </el-radio-group>
          </el-form-item>

          <el-form-item
            v-if="formData.cycle !== 'once'"
            :label="$t('customer.acquisition.startTime')"
            prop="startTime"
          >
            <el-date-picker
              v-model="formData.startTime"
              type="datetime"
              :placeholder="$t('customer.acquisition.selectStartTime')"
              value-format="YYYY-MM-DD HH:mm:ss"
              style="width: 100%"
            />
          </el-form-item>

          <el-form-item
            v-if="formData.cycle !== 'once'"
            :label="$t('customer.acquisition.endTime')"
            prop="endTime"
          >
            <el-date-picker
              v-model="formData.endTime"
              type="datetime"
              :placeholder="$t('customer.acquisition.selectEndTime')"
              value-format="YYYY-MM-DD HH:mm:ss"
              style="width: 100%"
            />
          </el-form-item>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item
                :label="$t('customer.acquisition.maxRetries')"
                prop="maxRetries"
              >
                <el-input-number
                  v-model="formData.maxRetries"
                  :min="0"
                  :max="10"
                  style="width: 100%"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item
                :label="$t('customer.acquisition.timeout')"
                prop="timeout"
              >
                <el-input-number
                  v-model="formData.timeout"
                  :min="10"
                  :max="300"
                  style="width: 100%"
                />
                <div class="form-tip">
                  {{ $t("customer.acquisition.timeoutUnit") }}
                </div>
              </el-form-item>
            </el-col>
          </el-row>
        </el-tab-pane>

        <!-- 通知配置 -->
        <el-tab-pane
          :label="$t('customer.acquisition.notificationConfig')"
          name="notification"
        >
          <el-form-item
            :label="$t('customer.acquisition.notifications')"
            prop="notifications"
          >
            <el-checkbox-group v-model="formData.notifications">
              <el-checkbox value="start">{{
                $t("customer.acquisition.notifyStart")
              }}</el-checkbox>
              <el-checkbox value="complete">{{
                $t("customer.acquisition.notifyComplete")
              }}</el-checkbox>
              <el-checkbox value="error">{{
                $t("customer.acquisition.notifyError")
              }}</el-checkbox>
              <el-checkbox value="milestone">{{
                $t("customer.acquisition.notifyMilestone")
              }}</el-checkbox>
            </el-checkbox-group>
          </el-form-item>

          <el-form-item
            :label="$t('customer.acquisition.notifyEmail')"
            prop="notifyEmail"
          >
            <el-input
              v-model="formData.notifyEmail"
              :placeholder="$t('customer.acquisition.notifyEmailPlaceholder')"
              clearable
            />
          </el-form-item>

          <el-form-item
            :label="$t('customer.acquisition.webhookUrl')"
            prop="webhookUrl"
          >
            <el-input
              v-model="formData.webhookUrl"
              :placeholder="$t('customer.acquisition.webhookUrlPlaceholder')"
              clearable
            />
            <div class="form-tip">
              {{ $t("customer.acquisition.webhookTip") }}
            </div>
          </el-form-item>
        </el-tab-pane>
      </el-tabs>
    </el-form>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">{{ $t("common.cancel") }}</el-button>
        <el-button @click="handleSaveAndStart" :loading="submitting">
          <el-icon><VideoPlay /></el-icon>
          {{ $t("customer.acquisition.saveAndStart") }}
        </el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">
          {{ $t("common.save") }}
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, computed, watch } from "vue";
import { ElMessage } from "element-plus";
import { VideoPlay } from "@element-plus/icons-vue";
import { useI18n } from "vue-i18n";

const { t } = useI18n();

// Props
const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false,
  },
  task: {
    type: Object,
    default: null,
  },
  isEdit: {
    type: Boolean,
    default: false,
  },
});

// Emits
const emit = defineEmits(["update:modelValue", "success"]);

// 响应式数据
const taskFormRef = ref();
const submitting = ref(false);
const activeTab = ref("basic");

const visible = computed({
  get: () => props.modelValue,
  set: (value) => emit("update:modelValue", value),
});

const formData = reactive({
  name: "",
  description: "",
  type: "customer",
  priority: "medium",
  platforms: ["linkedin"],
  keywords: "",
  region: "global",
  amount: 100,
  fields: ["name", "email", "company"],
  filters: "",
  cycle: "once",
  startTime: "",
  endTime: "",
  maxRetries: 3,
  timeout: 60,
  notifications: ["complete", "error"],
  notifyEmail: "",
  webhookUrl: "",
});

// 表单验证规则
const formRules = {
  name: [
    {
      required: true,
      message: t("customer.acquisition.nameRequired"),
      trigger: "blur",
    },
    {
      min: 2,
      max: 100,
      message: t("customer.acquisition.nameLength"),
      trigger: "blur",
    },
  ],
  platforms: [
    {
      type: "array",
      min: 1,
      message: t("customer.acquisition.platformRequired"),
      trigger: "change",
    },
  ],
  keywords: [
    {
      required: true,
      message: t("customer.acquisition.keywordsRequired"),
      trigger: "blur",
    },
  ],
  region: [
    {
      required: true,
      message: t("customer.acquisition.regionRequired"),
      trigger: "change",
    },
  ],
  amount: [
    {
      required: true,
      message: t("customer.acquisition.amountRequired"),
      trigger: "blur",
    },
  ],
  fields: [
    {
      type: "array",
      min: 1,
      message: t("customer.acquisition.fieldsRequired"),
      trigger: "change",
    },
  ],
  notifyEmail: [
    {
      type: "email",
      message: t("customer.acquisition.emailInvalid"),
      trigger: "blur",
    },
  ],
  webhookUrl: [
    {
      type: "url",
      message: t("customer.acquisition.urlInvalid"),
      trigger: "blur",
    },
  ],
};

// 平台选项
const platformOptions = [
  { value: "linkedin", label: "LinkedIn", icon: "🔗" },
  { value: "facebook", label: "Facebook", icon: "📘" },
  { value: "google_maps", label: "Google Maps", icon: "🗺️" },
  { value: "yellow_pages", label: "Yellow Pages", icon: "📞" },
  { value: "twitter", label: "Twitter", icon: "🐦" },
];

// 地区选项
const regionOptions = [
  { value: "global", label: t("customer.acquisition.regionGlobal") },
  { value: "us", label: t("customer.acquisition.regionUS") },
  { value: "eu", label: t("customer.acquisition.regionEU") },
  { value: "asia", label: t("customer.acquisition.regionAsia") },
  { value: "china", label: t("customer.acquisition.regionChina") },
];

// 监听器
watch(
  () => props.task,
  (newTask) => {
    if (newTask && props.isEdit) {
      Object.assign(formData, {
        name: newTask.name || "",
        description: newTask.description || "",
        type: newTask.type || "customer",
        priority: newTask.priority || "medium",
        platforms: Array.isArray(newTask.platform)
          ? newTask.platform
          : newTask.platform
          ? newTask.platform.split(",")
          : ["linkedin"],
        keywords: newTask.keywords || "",
        region: newTask.region || "global",
        amount: newTask.amount || 100,
        fields: Array.isArray(newTask.fields)
          ? newTask.fields
          : newTask.fields
          ? newTask.fields.split(",")
          : ["name", "email", "company"],
        filters: newTask.filters || "",
        cycle: newTask.cycle || "once",
        startTime: newTask.startTime || "",
        endTime: newTask.endTime || "",
        maxRetries: newTask.maxRetries || 3,
        timeout: newTask.timeout || 60,
        notifications: newTask.notifications || ["complete", "error"],
        notifyEmail: newTask.notifyEmail || "",
        webhookUrl: newTask.webhookUrl || "",
      });
    }
  },
  { immediate: true }
);

watch(visible, (newVisible) => {
  if (newVisible && !props.isEdit) {
    resetForm();
  }
});

// 方法
const resetForm = () => {
  Object.assign(formData, {
    name: "",
    description: "",
    type: "customer",
    priority: "medium",
    platforms: ["linkedin"],
    keywords: "",
    region: "global",
    amount: 100,
    fields: ["name", "email", "company"],
    filters: "",
    cycle: "once",
    startTime: "",
    endTime: "",
    maxRetries: 3,
    timeout: 60,
    notifications: ["complete", "error"],
    notifyEmail: "",
    webhookUrl: "",
  });
  if (taskFormRef.value) {
    taskFormRef.value.clearValidate();
  }
  activeTab.value = "basic";
};

const handleClose = () => {
  visible.value = false;
  resetForm();
};

const validateForm = async () => {
  if (!taskFormRef.value) return false;

  try {
    await taskFormRef.value.validate();
    return true;
  } catch (error) {
    return false;
  }
};

const handleSubmit = async () => {
  const isValid = await validateForm();
  if (!isValid) {
    ElMessage.warning(t("customer.acquisition.formValidationError"));
    return;
  }

  await submitTask(false);
};

const handleSaveAndStart = async () => {
  const isValid = await validateForm();
  if (!isValid) {
    ElMessage.warning(t("customer.acquisition.formValidationError"));
    return;
  }

  await submitTask(true);
};

const submitTask = async (startImmediately = false) => {
  submitting.value = true;
  try {
    const taskData = {
      ...formData,
      platforms: formData.platforms.join(","),
      fields: formData.fields.join(","),
      status: startImmediately ? "running" : "pending",
      createTime: props.isEdit ? undefined : new Date().toISOString(),
    };

    emit("success", taskData, startImmediately);

    const message = startImmediately
      ? t("customer.acquisition.taskCreatedAndStarted")
      : props.isEdit
      ? t("common.updateSuccess")
      : t("customer.acquisition.taskCreated");

    ElMessage.success(message);
    handleClose();
  } catch (error) {
    const errorMsg = props.isEdit
      ? t("common.updateError")
      : t("customer.acquisition.createTaskError");
    ElMessage.error(errorMsg);
  } finally {
    submitting.value = false;
  }
};
</script>

<style scoped>
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.platform-checkboxes,
.fields-checkboxes {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 12px;
}

.platform-label {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.cycle-option {
  margin-left: 8px;
}

.cycle-title {
  font-weight: 500;
  color: #303133;
  margin-bottom: 4px;
}

.cycle-desc {
  font-size: 12px;
  color: #909399;
  line-height: 1.4;
}

.form-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
  line-height: 1.4;
}

:deep(.el-radio) {
  display: flex;
  align-items: flex-start;
  margin-bottom: 16px;
  margin-right: 0;
  width: 100%;
}

:deep(.el-radio__input) {
  margin-top: 2px;
}

:deep(.el-radio__label) {
  padding-left: 8px;
  flex: 1;
}

:deep(.el-tab-pane) {
  padding: 20px 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .platform-checkboxes,
  .fields-checkboxes {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 480px) {
  .platform-checkboxes,
  .fields-checkboxes {
    grid-template-columns: 1fr;
  }
}
</style>
