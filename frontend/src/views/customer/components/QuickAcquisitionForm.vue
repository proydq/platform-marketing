<template>
  <div class="quick-acquisition-form">
    <el-form
      ref="quickFormRef"
      :model="formData"
      :rules="formRules"
      label-width="120px"
      label-position="left"
    >
      <!-- 平台选择 -->
      <el-form-item
        :label="$t('customer.acquisition.platform')"
        prop="platforms"
        class="platform-form-item"
      >
        <div class="platform-selection">
          <div class="platform-header">
            <h4>{{ $t("customer.acquisition.selectPlatforms") }}</h4>
            <p>{{ $t("customer.acquisition.selectPlatformsDesc") }}</p>
          </div>
          <el-checkbox-group v-model="formData.platforms" class="platform-grid">
            <el-checkbox
              v-for="platform in platformOptions"
              :key="platform.value"
              :label="platform.value"
              class="platform-checkbox"
            >
              <template #default>
                <div class="platform-option">
                  <div class="platform-icon">{{ platform.icon }}</div>
                  <div class="platform-info">
                    <div class="platform-name">{{ platform.label }}</div>
                    <div class="platform-desc">{{ platform.description }}</div>
                  </div>
                </div>
              </template>
            </el-checkbox>
          </el-checkbox-group>
        </div>
      </el-form-item>

      <!-- 采集类型 -->
      <el-form-item
        :label="$t('customer.acquisition.type')"
        prop="type"
        class="type-form-item"
      >
        <div class="type-selection">
          <div class="type-header">
            <h4>{{ $t("customer.acquisition.selectType") }}</h4>
            <p>{{ $t("customer.acquisition.selectTypeDesc") }}</p>
          </div>
          <el-radio-group v-model="formData.type" class="type-radio-group">
            <el-radio value="customer" class="type-radio">
              <template #default>
                <div class="type-option">
                  <div class="type-icon">👤</div>
                  <div class="type-info">
                    <div class="type-title">
                      {{ $t("customer.acquisition.typeCustomer") }}
                    </div>
                    <div class="type-desc">
                      {{ $t("customer.acquisition.typeCustomerDesc") }}
                    </div>
                  </div>
                </div>
              </template>
            </el-radio>
            <el-radio value="comment" class="type-radio">
              <template #default>
                <div class="type-option">
                  <div class="type-icon">💬</div>
                  <div class="type-info">
                    <div class="type-title">
                      {{ $t("customer.acquisition.typeComment") }}
                    </div>
                    <div class="type-desc">
                      {{ $t("customer.acquisition.typeCommentDesc") }}
                    </div>
                  </div>
                </div>
              </template>
            </el-radio>
            <el-radio value="article" class="type-radio">
              <template #default>
                <div class="type-option">
                  <div class="type-icon">📝</div>
                  <div class="type-info">
                    <div class="type-title">
                      {{ $t("customer.acquisition.typeArticle") }}
                    </div>
                    <div class="type-desc">
                      {{ $t("customer.acquisition.typeArticleDesc") }}
                    </div>
                  </div>
                </div>
              </template>
            </el-radio>
          </el-radio-group>
        </div>
      </el-form-item>

      <!-- 关键词配置 -->
      <el-form-item
        :label="$t('customer.acquisition.keywords')"
        prop="keywords"
        class="keywords-form-item"
      >
        <div class="input-section">
          <el-input
            v-model="formData.keywords"
            type="textarea"
            :rows="3"
            :placeholder="$t('customer.acquisition.keywordsPlaceholder')"
            class="keywords-input"
          />
          <div class="form-tip">
            <el-icon><InfoFilled /></el-icon>
            {{ $t("customer.acquisition.keywordsTip") }}
          </div>
        </div>
      </el-form-item>

      <!-- 地区和数量配置 -->
      <el-row :gutter="24" class="config-row">
        <el-col :span="12">
          <el-form-item
            :label="$t('customer.acquisition.region')"
            prop="region"
            class="region-form-item"
          >
            <el-select
              v-model="formData.region"
              :placeholder="$t('customer.acquisition.selectRegion')"
              style="width: 100%"
              size="large"
            >
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
            class="amount-form-item"
          >
            <el-input-number
              v-model="formData.amount"
              :min="1"
              :max="10000"
              :step="10"
              size="large"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <!-- 采集字段 -->
      <el-form-item
        :label="$t('customer.acquisition.fields')"
        prop="fields"
        class="fields-form-item"
      >
        <div class="fields-selection">
          <div class="fields-header">
            <h4>{{ $t("customer.acquisition.selectFields") }}</h4>
            <p>{{ $t("customer.acquisition.selectFieldsDesc") }}</p>
          </div>
          <el-checkbox-group v-model="formData.fields" class="fields-group">
            <el-checkbox value="name" class="field-checkbox">
              <div class="field-option">
                <el-icon><User /></el-icon>
                {{ $t("customer.name") }}
              </div>
            </el-checkbox>
            <el-checkbox value="email" class="field-checkbox">
              <div class="field-option">
                <el-icon><Message /></el-icon>
                {{ $t("customer.email") }}
              </div>
            </el-checkbox>
            <el-checkbox value="phone" class="field-checkbox">
              <div class="field-option">
                <el-icon><Phone /></el-icon>
                {{ $t("customer.phone") }}
              </div>
            </el-checkbox>
            <el-checkbox value="company" class="field-checkbox">
              <div class="field-option">
                <el-icon><OfficeBuilding /></el-icon>
                {{ $t("customer.company") }}
              </div>
            </el-checkbox>
            <el-checkbox value="title" class="field-checkbox">
              <div class="field-option">
                <el-icon><Suitcase /></el-icon>
                {{ $t("customer.acquisition.jobTitle") }}
              </div>
            </el-checkbox>
            <el-checkbox value="location" class="field-checkbox">
              <div class="field-option">
                <el-icon><Location /></el-icon>
                {{ $t("customer.acquisition.location") }}
              </div>
            </el-checkbox>
          </el-checkbox-group>
        </div>
      </el-form-item>

      <!-- 高级设置 -->
      <el-form-item>
        <el-collapse v-model="activeAdvanced">
          <el-collapse-item
            :title="$t('customer.acquisition.advancedSettings')"
            name="advanced"
          >
            <!-- 执行频率 -->
            <el-form-item
              :label="$t('customer.acquisition.frequency')"
              prop="frequency"
            >
              <el-radio-group v-model="formData.frequency">
                <el-radio value="once">{{
                  $t("customer.acquisition.cycleOnce")
                }}</el-radio>
                <el-radio value="daily">{{
                  $t("customer.acquisition.cycleDaily")
                }}</el-radio>
                <el-radio value="weekly">{{
                  $t("customer.acquisition.cycleWeekly")
                }}</el-radio>
              </el-radio-group>
            </el-form-item>

            <!-- 质量要求 -->
            <el-form-item
              :label="$t('customer.acquisition.quality')"
              prop="quality"
            >
              <el-slider
                v-model="formData.quality"
                :min="1"
                :max="5"
                :step="1"
                show-stops
                :marks="qualityMarks"
                style="width: 300px"
              />
            </el-form-item>

            <!-- 过滤条件 -->
            <el-form-item
              :label="$t('customer.acquisition.filters')"
              prop="filters"
            >
              <el-input
                v-model="formData.filters"
                type="textarea"
                :rows="2"
                :placeholder="$t('customer.acquisition.filtersPlaceholder')"
              />
            </el-form-item>
          </el-collapse-item>
        </el-collapse>
      </el-form-item>

      <!-- 操作按钮 -->
      <el-form-item>
        <div class="form-actions">
          <el-button @click="handleReset">
            {{ $t("common.reset") }}
          </el-button>
          <el-button @click="handlePreview">
            <el-icon><View /></el-icon>
            {{ $t("customer.acquisition.preview") }}
          </el-button>
          <el-button type="primary" :loading="submitting" @click="handleSubmit">
            <el-icon><VideoPlay /></el-icon>
            {{ $t("customer.acquisition.startCollection") }}
          </el-button>
        </div>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from "vue";
import { ElMessage } from "element-plus";
import {
  View,
  VideoPlay,
  InfoFilled,
  User,
  Message,
  Phone,
  OfficeBuilding,
  Suitcase,
  Location,
} from "@element-plus/icons-vue";
import { useI18n } from "vue-i18n";

const { t } = useI18n();

// Emits
const emit = defineEmits(["submit", "preview"]);

// 响应式数据
const quickFormRef = ref();
const submitting = ref(false);
const activeAdvanced = ref([]);

const formData = reactive({
  platforms: ["linkedin"],
  type: "customer",
  keywords: "",
  region: "global",
  amount: 100,
  fields: ["name", "email", "company"],
  frequency: "once",
  quality: 3,
  filters: "",
});

// 表单验证规则
const formRules = {
  platforms: [
    {
      required: true,
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
    {
      min: 2,
      max: 500,
      message: t("customer.acquisition.keywordsLength"),
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
};

// 平台选项
const platformOptions = [
  {
    value: "linkedin",
    label: "LinkedIn",
    icon: "🔗",
    description: t("customer.acquisition.platformLinkedInDesc"),
  },
  {
    value: "facebook",
    label: "Facebook",
    icon: "📘",
    description: t("customer.acquisition.platformFacebookDesc"),
  },
  {
    value: "google_maps",
    label: "Google Maps",
    icon: "🗺️",
    description: t("customer.acquisition.platformGoogleMapsDesc"),
  },
  {
    value: "yellow_pages",
    label: "Yellow Pages",
    icon: "📞",
    description: t("customer.acquisition.platformYellowPagesDesc"),
  },
  {
    value: "twitter",
    label: "Twitter",
    icon: "🐦",
    description: t("customer.acquisition.platformTwitterDesc"),
  },
];

// 地区选项
const regionOptions = [
  { value: "global", label: t("customer.acquisition.regionGlobal") },
  { value: "us", label: t("customer.acquisition.regionUS") },
  { value: "eu", label: t("customer.acquisition.regionEU") },
  { value: "asia", label: t("customer.acquisition.regionAsia") },
  { value: "china", label: t("customer.acquisition.regionChina") },
];

// 质量标记
const qualityMarks = computed(() => ({
  1: t("customer.acquisition.qualityBasic"),
  2: t("customer.acquisition.qualityGood"),
  3: t("customer.acquisition.qualityHigh"),
  4: t("customer.acquisition.qualityPremium"),
  5: t("customer.acquisition.qualityPro"),
}));

// 方法
const handleReset = () => {
  if (quickFormRef.value) {
    quickFormRef.value.resetFields();
  }
  Object.assign(formData, {
    platforms: ["linkedin"],
    type: "customer",
    keywords: "",
    region: "global",
    amount: 100,
    fields: ["name", "email", "company"],
    frequency: "once",
    quality: 3,
    filters: "",
  });
};

const handlePreview = async () => {
  if (!quickFormRef.value) return;

  try {
    await quickFormRef.value.validate();
    emit("preview", { ...formData });
  } catch (error) {
    ElMessage.warning(t("customer.acquisition.formValidationError"));
  }
};

const handleSubmit = async () => {
  if (!quickFormRef.value) return;

  try {
    await quickFormRef.value.validate();
  } catch (error) {
    ElMessage.warning(t("customer.acquisition.formValidationError"));
    return;
  }

  submitting.value = true;
  try {
    const taskData = {
      name: generateTaskName(),
      ...formData,
      platforms: formData.platforms.join(","),
      fields: formData.fields.join(","),
      status: "pending",
      createTime: new Date().toISOString(),
    };

    emit("submit", taskData);
    ElMessage.success(t("customer.acquisition.quickTaskCreated"));
    handleReset();
  } catch (error) {
    ElMessage.error(t("customer.acquisition.createTaskError"));
  } finally {
    submitting.value = false;
  }
};

const generateTaskName = () => {
  const platformNames = formData.platforms
    .map((p) => {
      const option = platformOptions.find((opt) => opt.value === p);
      return option ? option.label : p;
    })
    .join("+");

  const typeNames = {
    customer: t("customer.acquisition.typeCustomer"),
    comment: t("customer.acquisition.typeComment"),
    article: t("customer.acquisition.typeArticle"),
  };

  return `${platformNames}_${typeNames[formData.type]}_${formData.amount}`;
};
</script>

<style scoped>
.quick-acquisition-form {
  padding: 28px;
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  border-radius: 20px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.08);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  position: relative;
  overflow: hidden;
}

.quick-acquisition-form::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(
    135deg,
    rgba(102, 126, 234, 0.05) 0%,
    rgba(118, 75, 162, 0.05) 100%
  );
  z-index: -1;
}

/* 表单项间距优化 */
:deep(.el-form-item) {
  margin-bottom: 24px;
}

.platform-form-item,
.type-form-item,
.keywords-form-item,
.fields-form-item {
  margin-bottom: 28px;
}

/* 确保各个区域不重叠 */
.platform-form-item {
  position: relative;
  z-index: 10;
}

.type-form-item {
  position: relative;
  z-index: 9;
  clear: both;
}

.keywords-form-item {
  position: relative;
  z-index: 8;
  clear: both;
}

.fields-form-item {
  position: relative;
  z-index: 7;
  clear: both;
}

/* 表单标签样式统一 */
:deep(.el-form-item__label) {
  font-weight: 600;
  color: #374151;
  padding-bottom: 12px;
}

.platform-selection {
  width: 100%;
  background: rgba(255, 255, 255, 0.95);
  padding: 24px;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
  border: 1px solid rgba(226, 232, 240, 0.3);
  margin-top: 16px;
}

.platform-header {
  margin-bottom: 20px;
  text-align: left;
}

.platform-header h4 {
  margin: 0 0 8px 0;
  font-size: 16px;
  font-weight: 600;
  color: #374151;
}

.platform-header p {
  margin: 0;
  font-size: 14px;
  color: #64748b;
  line-height: 1.5;
}
.platform-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 24px;
  width: 100%;
  margin-top: 20px;
  padding: 1px; /* 防止 margin 折叠，1px 就够 */
}

.platform-checkbox {
  margin: 0 !important;
  width: 100%;
  position: relative;
  z-index: 1;
}

.platform-option {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px 16px;
  border: 2px solid rgba(226, 232, 240, 0.5);
  border-radius: 12px;
  transition: all 0.3s ease;
  cursor: pointer;
  min-height: 100px;
  justify-content: center;
  background: rgba(255, 255, 255, 0.8);
  position: relative;
  overflow: visible;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.platform-option:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.15);
  border-color: #667eea;
  background: rgba(255, 255, 255, 0.95);
}

.platform-checkbox.is-checked .platform-option {
  border-color: #667eea;
  background: rgba(236, 240, 255, 0.8);
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.2);
  transform: none;
}

.platform-checkbox:hover .platform-option {
  border-color: #a5b4fc;
}

.platform-info {
  text-align: center;
  width: 100%;
}

.platform-icon {
  font-size: 28px;
  margin-bottom: 8px;
  display: block;
}

.platform-desc {
  font-size: 12px;
  color: #6b7280;
  text-align: center;
  line-height: 1.4;
  opacity: 0.8;
}

.type-selection {
  width: 100%;
  background: rgba(255, 255, 255, 0.95);
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  border: 1px solid rgba(226, 232, 240, 0.3);
  margin-top: 12px;
}

.type-header {
  margin-bottom: 20px;
  text-align: left;
}

.type-header h4 {
  margin: 0 0 8px 0;
  font-size: 16px;
  font-weight: 600;
  color: #374151;
}

.type-header p {
  margin: 0;
  font-size: 14px;
  color: #64748b;
  line-height: 1.5;
}

.type-radio-group {
  display: flex;
  gap: 8px;
  margin: 50px;
}

.type-option {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  background: rgba(248, 250, 252, 0.6);
  border-radius: 8px;
  transition: all 0.3s ease;
  border: 1px solid rgba(226, 232, 240, 0.4);
  margin-left: 0;
}

.type-icon {
  font-size: 20px;
  margin-right: 12px;
  flex-shrink: 0;
}

.type-info {
  flex: 1;
}

.type-option:hover {
  background: rgba(255, 255, 255, 0.95);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06);
  transform: translateY(-2px);
}

.type-title {
  font-weight: 600;
  color: #374151;
  margin-bottom: 6px;
  font-size: 15px;
}

.type-desc {
  font-size: 13px;
  color: #64748b;
  line-height: 1.5;
  opacity: 0.8;
}

.input-section {
  width: 100%;
}

.keywords-input {
  margin-bottom: 12px;
}

.form-tip {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #64748b;
  line-height: 1.5;
  padding: 12px 16px;
  background: linear-gradient(
    135deg,
    rgba(102, 126, 234, 0.05) 0%,
    rgba(118, 75, 162, 0.05) 100%
  );
  border-radius: 10px;
  border-left: 4px solid #667eea;
}

.fields-selection {
  width: 100%;
  background: rgba(255, 255, 255, 0.95);
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  border: 1px solid rgba(226, 232, 240, 0.3);
  margin-top: 12px;
}

.fields-header {
  margin-bottom: 20px;
  text-align: left;
}

.fields-header h4 {
  margin: 0 0 8px 0;
  font-size: 16px;
  font-weight: 600;
  color: #374151;
}

.fields-header p {
  margin: 0;
  font-size: 14px;
  color: #64748b;
  line-height: 1.5;
}

.fields-group {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(160px, 1fr));
  gap: 12px;
}

.field-checkbox {
  margin: 0 !important;
  width: 100%;
}

.field-option {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 12px;
  background: rgba(248, 250, 252, 0.6);
  border-radius: 8px;
  transition: all 0.3s ease;
  border: 1px solid rgba(226, 232, 240, 0.4);
  font-size: 13px;
  font-weight: 500;
  color: #374151;
  white-space: nowrap;
}

.field-option:hover {
  background: rgba(255, 255, 255, 0.9);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 16px;
  margin-top: 32px;
  padding-top: 24px;
  border-top: 2px solid rgba(102, 126, 234, 0.1);
}

:deep(.type-radio .el-radio) {
  display: flex;
  align-items: center;
  margin: 0;
  width: 100%;
  background: transparent;
  border: none;
  padding: 0;
}

:deep(.type-radio .el-radio__input) {
  margin-right: 12px;
  flex-shrink: 0;
}

:deep(.type-radio .el-radio__label) {
  padding-left: 0;
  flex: 1;
  font-weight: 500;
  color: #374151;
}

:deep(.type-radio.is-checked .type-option) {
  background: rgba(236, 240, 255, 0.8);
  border-color: #667eea;
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.15);
}

:deep(.field-checkbox .el-checkbox) {
  margin: 0;
  width: 100%;
}

:deep(.field-checkbox .el-checkbox__input) {
  margin-right: 0;
  position: absolute;
  opacity: 0;
}

:deep(.field-checkbox .el-checkbox__label) {
  padding-left: 0;
  flex: 1;
  width: 100%;
}

:deep(.field-checkbox.is-checked .field-option) {
  background: rgba(236, 240, 255, 0.8);
  border-color: #667eea;
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.15);
}

:deep(.type-radio:hover .type-option) {
  background: rgba(255, 255, 255, 0.9);
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  transform: translateY(-1px);
}

:deep(.field-checkbox:hover .field-option) {
  background: rgba(255, 255, 255, 0.9);
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

:deep(.el-collapse) {
  border: none;
  border-radius: 16px;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.8);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
}

:deep(.el-collapse-item__header) {
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  border: none;
  padding: 22px 28px;
  font-weight: 600;
  color: #374151;
  font-size: 15px;
}

:deep(.el-collapse-item__content) {
  padding: 28px;
  background: rgba(255, 255, 255, 0.95);
}

:deep(.el-slider) {
  margin: 28px 0;
}

:deep(.el-input__wrapper) {
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.06);
  transition: all 0.3s ease;
  border: 1px solid #e2e8f0;
  background: rgba(255, 255, 255, 0.9);
}

:deep(.el-input__wrapper:hover) {
  box-shadow: 0 4px 20px rgba(102, 126, 234, 0.15);
  border-color: #667eea;
  background: rgba(255, 255, 255, 1);
}

:deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 4px 20px rgba(102, 126, 234, 0.25);
  border-color: #667eea;
  background: rgba(255, 255, 255, 1);
}

:deep(.el-select .el-input__wrapper) {
  border-radius: 12px;
}

:deep(.el-input-number .el-input__wrapper) {
  border-radius: 12px;
}

:deep(.el-button) {
  border-radius: 12px;
  font-weight: 600;
  padding: 14px 28px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: none;
  position: relative;
  overflow: hidden;
}

:deep(.el-button--primary) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.3);
}

:deep(.el-button--primary:hover) {
  transform: translateY(-3px);
  box-shadow: 0 8px 30px rgba(102, 126, 234, 0.4);
}

:deep(.el-button--default) {
  background: rgba(255, 255, 255, 0.95);
  color: #64748b;
  border: 1px solid rgba(100, 116, 139, 0.2);
  backdrop-filter: blur(10px);
}

:deep(.el-button--default:hover) {
  background: rgba(255, 255, 255, 1);
  color: #374151;
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.1);
}

/* 动画增强 */
@keyframes slideInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.quick-acquisition-form {
  animation: fadeIn 0.8s ease-out;
}

.platform-selection {
  animation: slideInUp 0.8s ease-out 0.2s both;
}

.platform-option:nth-child(1) {
  animation: slideInUp 0.6s ease-out 0.3s both;
}
.platform-option:nth-child(2) {
  animation: slideInUp 0.6s ease-out 0.4s both;
}
.platform-option:nth-child(3) {
  animation: slideInUp 0.6s ease-out 0.5s both;
}
.platform-option:nth-child(4) {
  animation: slideInUp 0.6s ease-out 0.6s both;
}
.platform-option:nth-child(5) {
  animation: slideInUp 0.6s ease-out 0.7s both;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .quick-acquisition-form {
    padding: 20px 16px;
    border-radius: 16px;
  }

  .platform-selection {
    padding: 16px;
    border-radius: 12px;
  }

  .platform-grid {
    grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
    gap: 16px;
  }

  .platform-form-item,
  .type-form-item,
  .keywords-form-item,
  .fields-form-item {
    margin-bottom: 20px;
  }

  .platform-option {
    padding: 20px 16px;
    min-height: 100px;
  }

  .fields-group {
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
    padding: 16px;
  }

  .form-actions {
    flex-direction: column;
    gap: 12px;
  }

  :deep(.el-button) {
    width: 100%;
    justify-content: center;
    padding: 12px 20px;
  }
}

@media (max-width: 480px) {
  .quick-acquisition-form {
    padding: 16px;
    border-radius: 12px;
    margin: 0 -8px;
  }

  .platform-selection {
    padding: 16px;
  }

  .platform-grid {
    grid-template-columns: 1fr;
    gap: 12px;
  }

  .platform-option {
    padding: 16px;
    min-height: 80px;
  }

  .platform-icon {
    font-size: 28px;
    margin-bottom: 8px;
  }

  .platform-name {
    font-size: 14px;
  }

  .fields-group {
    grid-template-columns: 1fr;
    padding: 12px;
  }

  .type-option {
    padding: 12px;
  }

  .form-tip {
    padding: 12px;
  }

  :deep(.el-collapse-item__header) {
    padding: 16px 20px;
    font-size: 14px;
  }

  :deep(.el-collapse-item__content) {
    padding: 20px;
  }
}
/* 1) 确保 checkbox 组真的用上 Grid 布局 */
:deep(.el-checkbox-group.platform-grid) {
  display: grid !important;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  column-gap: 24px;
  row-gap: 28px; /* 行距更大一点，拉开上下两行 */
  width: 100%;
  margin-top: 20px;
  margin-bottom: 24px; /* 和“采集类型”块拉开距离 */
  padding: 1px; /* 避免子项 margin 折叠 */
  box-sizing: border-box;
}

/* 2) Grid 子项要铺满格子，避免高度不一导致视觉“贴” */
:deep(.el-checkbox.platform-checkbox) {
  display: block; /* 避免 inline 布局的奇怪缝隙 */
  height: 100%;
  margin: 0 !important;
  width: 100%;
}

/* 3) 卡片自身占满格子高度，内部留好内边距 */
.platform-option {
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

/* 4) 再保险一点：让整个平台选择区域和下一个表单项分开 */
.platform-selection {
  margin-bottom: 20px;
}

/* 如果还觉得跟下面贴：把表单项间距再拉大一点 */
.platform-form-item {
  margin-bottom: 36px !important;
}
</style>
