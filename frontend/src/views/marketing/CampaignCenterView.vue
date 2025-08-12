<template>
  <div class="om-page-container">
    <!-- 页面头部 -->
    <div class="om-page-header">
      <div class="om-page-header__content">
        <div class="om-page-header__text">
          <h1>{{ $t("menu.campaign") }}</h1>
          <p>创建和管理多渠道营销活动，提升品牌影响力和转化效果</p>
        </div>
        <div class="om-page-header__actions">
          <el-button type="primary" size="large" @click="openEdit(false)">
            <el-icon><Plus /></el-icon>
            {{ $t("campaign.new") }}
          </el-button>
          <el-button size="large" @click="loadData">
            <el-icon><Refresh /></el-icon>
            刷新数据
          </el-button>
        </div>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="om-stats-grid">
      <div class="om-stat-card om-stat-card--neon-blue">
        <div class="om-stat-card__icon">
          <span>🚀</span>
        </div>
        <div class="om-stat-card__content">
          <div class="om-stat-card__value">{{ campaignStats.total }}</div>
          <div class="om-stat-card__label">总活动数</div>
          <div class="om-stat-card__trend om-stat-card__trend--up">
            <span>+{{ campaignStats.newThisMonth }}</span>
          </div>
        </div>
      </div>
      
      <div class="om-stat-card om-stat-card--neon-green">
        <div class="om-stat-card__icon">
          <span>▶️</span>
        </div>
        <div class="om-stat-card__content">
          <div class="om-stat-card__value">{{ campaignStats.running }}</div>
          <div class="om-stat-card__label">进行中</div>
          <div class="om-stat-card__trend om-stat-card__trend--up">
            <span>+{{ campaignStats.runningIncrease }}%</span>
          </div>
        </div>
      </div>

      <div class="om-stat-card om-stat-card--neon-purple">
        <div class="om-stat-card__icon">
          <span>✅</span>
        </div>
        <div class="om-stat-card__content">
          <div class="om-stat-card__value">{{ campaignStats.completed }}</div>
          <div class="om-stat-card__label">已完成</div>
          <div class="om-stat-card__trend om-stat-card__trend--up">
            <span>+{{ campaignStats.completedIncrease }}%</span>
          </div>
        </div>
      </div>

      <div class="om-stat-card om-stat-card--neon-pink">
        <div class="om-stat-card__icon">
          <span>📊</span>
        </div>
        <div class="om-stat-card__content">
          <div class="om-stat-card__value">{{ campaignStats.avgClickRate }}%</div>
          <div class="om-stat-card__label">平均点击率</div>
          <div class="om-stat-card__trend om-stat-card__trend--up">
            <span>+{{ campaignStats.clickRateIncrease }}%</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 营销活动管理卡片 -->
    <div class="om-content-card">
      <div class="om-content-card__header">
        <div class="om-content-card__header-left">
          <div class="om-content-card__icon">
            <el-icon><Operation /></el-icon>
          </div>
          <div class="om-content-card__info">
            <h3>营销活动管理</h3>
            <p>管理所有营销活动的执行状态和效果数据</p>
          </div>
        </div>
        <div class="om-content-card__header-right">
          <div class="om-toolbar__filters">
            <el-select
              v-model="statusFilter"
              :placeholder="$t('campaign.status')"
              clearable
              style="width: 140px"
            >
              <el-option :label="$t('common.all')" value="" />
              <el-option :label="$t('campaign.pending')" value="pending" />
              <el-option :label="$t('campaign.running')" value="running" />
              <el-option :label="$t('campaign.success')" value="success" />
              <el-option :label="$t('campaign.pause')" value="paused" />
            </el-select>
            <el-select
              v-model="channelFilter"
              :placeholder="$t('campaign.channel')"
              clearable
              style="width: 140px"
            >
              <el-option :label="$t('common.all')" value="" />
              <el-option label="Email" value="email" />
              <el-option :label="$t('sidebar.socialMedia')" value="social" />
              <el-option label="Wechat" value="wechat" />
              <el-option :label="$t('common.custom')" value="custom" />
            </el-select>
          </div>
          <div class="om-view-search">
            <el-input
              v-model="search"
              :placeholder="$t('campaign.search')"
              clearable
              style="width: 250px"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
          </div>
        </div>
      </div>
      
      <div class="om-content-card__body">
        <div class="om-table-container">
          <el-table 
            :data="filtered" 
            style="width: 100%"
            v-loading="loading"
            :empty-text="'暂无活动数据'"
          >
            <el-table-column
              prop="name"
              :label="$t('campaign.name')"
              min-width="200"
              show-overflow-tooltip
            >
              <template #default="{ row }">
                <div class="campaign-name-cell">
                  <div class="campaign-title">{{ row.name }}</div>
                  <div class="campaign-meta">
                    <el-tag 
                      v-for="channel in (row.channels || []).slice(0, 2)" 
                      :key="channel"
                      size="small" 
                      class="channel-tag"
                    >
                      {{ channelName(channel) }}
                    </el-tag>
                    <el-tag 
                      v-if="(row.channels || []).length > 2"
                      size="small"
                      type="info"
                    >
                      +{{ (row.channels || []).length - 2 }}
                    </el-tag>
                  </div>
                </div>
              </template>
            </el-table-column>

            <el-table-column :label="$t('campaign.status')" width="120" align="center">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)" size="small" effect="dark">
                  {{ statusText(row.status) }}
                </el-tag>
              </template>
            </el-table-column>

            <el-table-column
              prop="startTime"
              :label="$t('campaign.start')"
              width="150"
              align="center"
            />

            <el-table-column
              prop="endTime"
              :label="$t('campaign.end')"
              width="150"
              align="center"
            />

            <el-table-column :label="$t('campaign.operation')" width="180" fixed="right" align="center">
              <template #default="{ row }">
                <div class="action-buttons">
                  <el-tooltip content="查看详情" placement="top">
                    <el-button size="small" type="primary" @click="openDetail(row)" circle>
                      <el-icon><View /></el-icon>
                    </el-button>
                  </el-tooltip>

                  <el-tooltip content="编辑活动" placement="top">
                    <el-button size="small" type="warning" @click="openEdit(true, row)" circle>
                      <el-icon><Edit /></el-icon>
                    </el-button>
                  </el-tooltip>

                  <el-dropdown trigger="click" @command="(command) => handleCampaignAction(command, row)">
                    <el-button size="small" circle>
                      <el-icon><More /></el-icon>
                    </el-button>
                    <template #dropdown>
                      <el-dropdown-menu>
                        <el-dropdown-item command="publish">
                          <el-icon><VideoPlay /></el-icon>
                          {{ $t("campaign.publish") }}
                        </el-dropdown-item>
                        <el-dropdown-item command="pause">
                          <el-icon><VideoPause /></el-icon>
                          {{ row.status === "paused" ? $t("campaign.resume") : $t("campaign.pause") }}
                        </el-dropdown-item>
                        <el-dropdown-item command="duplicate">
                          <el-icon><DocumentCopy /></el-icon>
                          复制活动
                        </el-dropdown-item>
                        <el-dropdown-item command="delete" divided>
                          <el-icon><Delete /></el-icon>
                          {{ $t("campaign.delete") }}
                        </el-dropdown-item>
                      </el-dropdown-menu>
                    </template>
                  </el-dropdown>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </div>

    <el-drawer
      v-model="editDrawer"
      :title="editMode ? $t('campaign.edit') : $t('campaign.new')"
      size="40%"
    >
      <el-form :model="form" label-width="90px" class="form-section">
        <el-form-item :label="$t('campaign.name')" required>
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item :label="$t('campaign.channel')" required>
          <el-select v-model="form.channels" multiple style="width: 100%">
            <el-option label="Email" value="email" />
            <el-option :label="$t('sidebar.socialMedia')" value="social" />
            <el-option label="Wechat" value="wechat" />
            <el-option :label="$t('campaign.custom')" value="custom" />
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('campaign.start')">
          <el-date-picker
            v-model="form.startTime"
            type="datetime"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item :label="$t('campaign.end')">
          <el-date-picker
            v-model="form.endTime"
            type="datetime"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item :label="$t('campaign.content')">
          <div
            v-for="(c, i) in form.contents"
            :key="i"
            class="campaign-card"
            style="margin-bottom: 10px"
          >
            <RichTextEditor v-model="form.contents[i]" />
          </div>
          <el-button size="small" @click="addContent">{{
            $t("campaign.addContent")
          }}</el-button>
        </el-form-item>
        <el-form-item :label="$t('campaign.cycle')">
          <el-select v-model="form.cycle" style="width: 100%">
            <el-option :label="$t('campaign.once')" value="once" />
            <el-option :label="$t('campaign.daily')" value="daily" />
            <el-option :label="$t('campaign.weekly')" value="weekly" />
          </el-select>
        </el-form-item>
        <div class="action-buttons" style="justify-content: flex-end">
          <el-button @click="editDrawer = false">{{
            $t("campaign.cancel")
          }}</el-button>
          <el-button type="primary" @click="save">{{
            $t("campaign.save")
          }}</el-button>
        </div>
      </el-form>
    </el-drawer>

    <el-drawer
      v-model="detailDrawer"
      :title="$t('campaign.detail')"
      direction="rtl"
      size="40%"
    >
      <template v-if="current">
        <el-descriptions
          :title="current.name"
          column="1"
          border
          style="margin-bottom: 20px"
        >
          <el-descriptions-item :label="$t('campaign.status')">
            <span :class="'status-badge status-' + current.status">{{
              statusText(current.status)
            }}</span>
          </el-descriptions-item>
          <el-descriptions-item :label="$t('campaign.channel')">
            <el-tag
              v-for="c in current.channels"
              :key="c"
              size="small"
              style="margin-right: 4px"
              >{{ channelName(c) }}</el-tag
            >
          </el-descriptions-item>
          <el-descriptions-item
            :label="$t('campaign.start') + '/' + $t('campaign.end')"
            >{{ current.startTime }} -
            {{ current.endTime }}</el-descriptions-item
          >
        </el-descriptions>
        <el-card class="chart-container" style="margin-bottom: 20px">
          <FunnelChart :data="metricsFunnel" />
        </el-card>
        <el-steps
          :active="current.steps ? current.steps.length : 0"
          finish-status="success"
          style="margin-bottom: 20px"
        >
          <el-step v-for="(s, i) in current.steps" :key="i" :title="s" />
        </el-steps>
        <div
          v-for="(c, i) in current.contents"
          :key="i"
          style="margin-bottom: 10px"
          v-html="c"
        />
      </template>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from "vue";
import { ElMessage } from "element-plus";
import { useI18n } from "vue-i18n";
import FunnelChart from "../../components/charts/FunnelChart.vue";
import RichTextEditor from "../../components/RichTextEditor.vue";
import {
  getCampaignList,
  getCampaignDetail,
  createCampaign,
  updateCampaign,
  deleteCampaign,
  publishCampaign,
  toggleCampaignStatus,
} from "@/api/campaign";

const campaigns = ref([]);
const statusFilter = ref("");
const channelFilter = ref("");
const search = ref("");

const { t } = useI18n();

const editDrawer = ref(false);
const detailDrawer = ref(false);
const editMode = ref(false);
const current = ref(null);
const metricsFunnel = computed(() => {
  if (!current.value || !current.value.metrics) return [];
  return [
    { step: t("campaign.sent"), value: current.value.metrics.sent },
    { step: t("campaign.opens"), value: current.value.metrics.opens },
    { step: t("campaign.clicks"), value: current.value.metrics.clicks },
  ];
});

const form = reactive({
  id: null,
  name: "",
  channels: [],
  startTime: "",
  endTime: "",
  contents: [""],
  cycle: "once",
});

async function loadData() {
  const res = await getCampaignList({
    page: 0,
    size: 100,
    keyword: search.value,
  });
  campaigns.value = res.data?.rows || [];
}
onMounted(loadData);

const filtered = computed(() => {
  return campaigns.value.filter((c) => {
    const sOk = !statusFilter.value || c.status === statusFilter.value;
    const cOk =
      !channelFilter.value || c.channels.includes(channelFilter.value);
    const qOk = !search.value || c.name.includes(search.value);
    return sOk && cOk && qOk;
  });
});

function statusText(s) {
  const map = {
    pending: t("campaign.pending"),
    running: t("campaign.running"),
    success: t("campaign.success"),
    paused: t("campaign.pause"),
  };
  return map[s] || s;
}

function channelName(c) {
  const map = {
    email: "Email",
    social: t("sidebar.socialMedia"),
    wechat: "Wechat",
    custom: t("campaign.custom"),
  };
  return map[c] || c;
}

function openEdit(edit, row) {
  editMode.value = edit;
  if (edit && row) {
    Object.assign(form, JSON.parse(JSON.stringify(row)));
    if (!form.contents || !form.contents.length) form.contents = [""];
  } else {
    Object.assign(form, {
      id: null,
      name: "",
      channels: [],
      startTime: "",
      endTime: "",
      contents: [""],
      cycle: "once",
    });
  }
  editDrawer.value = true;
}

function addContent() {
  form.contents.push("");
}

async function save() {
  if (editMode.value) {
    await updateCampaign(form.id, form);
  } else {
    await createCampaign(form);
  }
  editDrawer.value = false;
  ElMessage.success(t("common.saveSuccess"));
  loadData();
}

async function remove(row) {
  await deleteCampaign(row.id);
  ElMessage.success(t("common.deleted"));
  loadData();
}

async function openDetail(row) {
  const res = await getCampaignDetail(row.id);
  current.value = res.data;
  if (!current.value.contents) current.value.contents = row.contents || [];
  detailDrawer.value = true;
}

async function publish(row) {
  await publishCampaign({ id: row.id });
  ElMessage.success(t("campaign.publish"));
  loadData();
}

async function togglePause(row) {
  await toggleCampaignStatus({ id: row.id });
  ElMessage.success(
    row.status === "paused" ? t("campaign.resume") : t("campaign.pause")
  );
  loadData();
}
</script>
