<template>
  <div class="channel-management">
    <div class="page-header">
      <h1>{{ t("channels.management") }}</h1>
      <p class="subtitle">管理和配置多渠道营销集成</p>
    </div>

    <div class="stats-cards">
      <div class="stat-card">
        <div class="stat-icon">🔗</div>
        <div class="stat-content">
          <div class="stat-number">{{ stats.totalChannels }}</div>
          <div class="stat-label">总渠道数</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">✅</div>
        <div class="stat-content">
          <div class="stat-number">{{ stats.enabledChannels }}</div>
          <div class="stat-label">已启用</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">⚙️</div>
        <div class="stat-content">
          <div class="stat-number">{{ stats.customChannels }}</div>
          <div class="stat-label">自定义渠道</div>
        </div>
      </div>
    </div>

    <div class="channel-list">
      <div class="list-header">
        <h2>渠道列表</h2>
        <el-button type="primary" @click="showCreateDialog = true">
          创建渠道
        </el-button>
      </div>

      <div class="channel-grid">
        <div
          v-for="channel in channels"
          :key="channel.id"
          class="channel-card"
          :class="{ disabled: !channel.enabled }"
        >
          <div class="channel-header">
            <div class="channel-icon">{{ channel.icon }}</div>
            <div class="channel-info">
              <h3>{{ channel.name }}</h3>
              <p>{{ channel.description }}</p>
            </div>
            <el-switch
              v-model="channel.enabled"
              @change="toggleChannel(channel)"
            />
          </div>

          <div class="channel-features">
            <div class="feature-tag" v-if="channel.supportsBatch">批量支持</div>
            <div class="feature-tag" v-if="channel.supportsTemplate">
              模板支持
            </div>
            <div class="feature-tag" v-if="channel.supportsTracking">
              追踪支持
            </div>
          </div>

          <div class="channel-actions">
            <el-button size="small" @click="testConnection(channel)">
              测试连接
            </el-button>
            <el-button size="small" @click="editChannel(channel)">
              配置
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 创建/编辑渠道对话框 -->
    <el-dialog v-model="showCreateDialog" title="创建渠道" width="600px">
      <el-form :model="channelForm" label-width="100px">
        <el-form-item label="渠道名称">
          <el-input v-model="channelForm.name" placeholder="请输入渠道名称" />
        </el-form-item>
        <el-form-item label="渠道类型">
          <el-select v-model="channelForm.type" placeholder="选择渠道类型">
            <el-option label="邮件营销" value="email" />
            <el-option label="社交媒体" value="social" />
            <el-option label="即时消息" value="messaging" />
            <el-option label="自定义" value="custom" />
          </el-select>
        </el-form-item>
        <el-form-item label="API配置">
          <el-input
            v-model="channelForm.apiEndpoint"
            placeholder="API端点URL"
            style="margin-bottom: 10px"
          />
          <el-input
            v-model="channelForm.apiKey"
            placeholder="API密钥"
            type="password"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" @click="saveChannel">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import { useI18n } from "vue-i18n";
import { ElMessage } from "element-plus";

const { t } = useI18n();

const stats = reactive({
  totalChannels: 8,
  enabledChannels: 5,
  customChannels: 2,
});

const showCreateDialog = ref(false);

const channelForm = reactive({
  name: "",
  type: "",
  apiEndpoint: "",
  apiKey: "",
});

const channels = ref([
  {
    id: 1,
    name: "SendGrid邮件",
    description: "企业级邮件发送服务",
    icon: "📧",
    type: "email",
    enabled: true,
    supportsBatch: true,
    supportsTemplate: true,
    supportsTracking: true,
  },
  {
    id: 2,
    name: "WhatsApp Business",
    description: "WhatsApp商业版API",
    icon: "💬",
    type: "messaging",
    enabled: true,
    supportsBatch: true,
    supportsTemplate: true,
    supportsTracking: false,
  },
  {
    id: 3,
    name: "LinkedIn营销",
    description: "LinkedIn广告和消息推送",
    icon: "🔗",
    type: "social",
    enabled: true,
    supportsBatch: false,
    supportsTemplate: true,
    supportsTracking: true,
  },
  {
    id: 4,
    name: "Facebook广告",
    description: "Facebook广告投放平台",
    icon: "📘",
    type: "social",
    enabled: false,
    supportsBatch: true,
    supportsTemplate: false,
    supportsTracking: true,
  },
  {
    id: 5,
    name: "AWS SES",
    description: "Amazon简单邮件服务",
    icon: "☁️",
    type: "email",
    enabled: true,
    supportsBatch: true,
    supportsTemplate: false,
    supportsTracking: false,
  },
]);

const toggleChannel = (channel) => {
  // 模拟API调用
  setTimeout(() => {
    const action = channel.enabled ? "启用" : "禁用";
    ElMessage.success(`渠道 ${action} 成功`);
  }, 500);
};

const testConnection = (channel) => {
  ElMessage.info("正在测试连接...");

  // 模拟连接测试
  setTimeout(() => {
    const success = Math.random() > 0.3;
    if (success) {
      ElMessage.success(`${channel.name} 连接测试成功`);
    } else {
      ElMessage.error(`${channel.name} 连接测试失败`);
    }
  }, 2000);
};

const editChannel = (channel) => {
  ElMessage.info("渠道配置功能开发中...");
};

const saveChannel = () => {
  // 表单验证和保存逻辑
  if (!channelForm.name || !channelForm.type) {
    ElMessage.error("请填写必填字段");
    return;
  }

  ElMessage.success("渠道创建成功");
  showCreateDialog.value = false;

  // 重置表单
  Object.assign(channelForm, {
    name: "",
    type: "",
    apiEndpoint: "",
    apiKey: "",
  });
};

onMounted(() => {
  // 初始化数据
});
</script>

<style scoped>
.channel-management {
  padding: 24px;
}

.page-header {
  margin-bottom: 24px;
}

.page-header h1 {
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 600;
}

.subtitle {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.stats-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
  margin-bottom: 24px;
}

.stat-card {
  background: white;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  font-size: 32px;
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f5f5;
  border-radius: 50%;
}

.stat-number {
  font-size: 24px;
  font-weight: 600;
  color: #333;
}

.stat-label {
  font-size: 14px;
  color: #666;
  margin-top: 4px;
}

.channel-list {
  background: white;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 24px;
}

.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.list-header h2 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
}

.channel-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 16px;
}

.channel-card {
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 20px;
  transition: all 0.3s ease;
}

.channel-card:hover {
  border-color: #409eff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.1);
}

.channel-card.disabled {
  opacity: 0.6;
}

.channel-header {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  margin-bottom: 16px;
}

.channel-icon {
  font-size: 24px;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f5f5;
  border-radius: 50%;
  flex-shrink: 0;
}

.channel-info {
  flex: 1;
}

.channel-info h3 {
  margin: 0 0 4px 0;
  font-size: 16px;
  font-weight: 600;
}

.channel-info p {
  margin: 0;
  font-size: 12px;
  color: #666;
  line-height: 1.4;
}

.channel-features {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 16px;
}

.feature-tag {
  background: #e8f4fd;
  color: #409eff;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.channel-actions {
  display: flex;
  gap: 8px;
}

.channel-actions .el-button {
  flex: 1;
}
</style>
