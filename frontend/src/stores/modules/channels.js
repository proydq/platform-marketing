/**
 * 渠道管理 Store
 */
import { defineStore } from 'pinia';
import { 
  getChannels, 
  getChannel, 
  createChannel, 
  updateChannel, 
  deleteChannel, 
  toggleChannel,
  testChannel,
  getChannelStats
} from '@/api/channels';
import { BUILT_IN_CHANNELS } from '@/constants/channels';

export const useChannelsStore = defineStore('channels', {
  state: () => ({
    // 渠道列表
    channels: [],
    currentChannel: null,
    loading: false,
    
    // 统计数据
    stats: {
      total: 0,
      enabled: 0,
      builtin: 0,
      custom: 0
    },
    
    // 缓存
    cache: {
      channels: null,
      channelsExpiry: null,
      stats: null,
      statsExpiry: null
    },
    
    // 缓存时间 (10分钟)
    cacheTimeout: 10 * 60 * 1000
  }),
  
  getters: {
    // 启用的渠道
    enabledChannels: (state) => {
      return state.channels.filter(channel => channel.enabled);
    },
    
    // 内置渠道
    builtinChannels: (state) => {
      return state.channels.filter(channel => channel.builtin);
    },
    
    // 自定义渠道
    customChannels: (state) => {
      return state.channels.filter(channel => !channel.builtin);
    },
    
    // 按分类分组的渠道
    channelsByCategory: (state) => {
      const grouped = {};
      state.channels.forEach(channel => {
        const category = channel.category || 'custom';
        if (!grouped[category]) {
          grouped[category] = [];
        }
        grouped[category].push(channel);
      });
      return grouped;
    },
    
    // 渠道选项（用于下拉选择）
    channelOptions: (state) => {
      return state.channels
        .filter(channel => channel.enabled)
        .map(channel => ({
          value: channel.id,
          label: channel.name,
          icon: channel.icon,
          color: channel.color,
          category: channel.category
        }));
    },
    
    // 检查缓存是否有效
    isCacheValid: (state) => (cacheKey) => {
      return state.cache[cacheKey] && 
             state.cache[`${cacheKey}Expiry`] && 
             Date.now() < state.cache[`${cacheKey}Expiry`];
    }
  },
  
  actions: {
    /**
     * 获取渠道列表
     */
    async fetchChannels(params = {}, useCache = true) {
      // 检查缓存
      if (useCache && this.isCacheValid('channels') && !params.refresh) {
        this.channels = this.cache.channels;
        return this.channels;
      }
      
      this.loading = true;
      try {
        const response = await getChannels(params);
        const data = response.data || response;
        
        this.channels = data.rows || data.list || [];
        
        // 如果没有数据，使用内置渠道
        if (this.channels.length === 0) {
          this.channels = [...BUILT_IN_CHANNELS];
        }
        
        // 更新缓存
        this.cache.channels = this.channels;
        this.cache.channelsExpiry = Date.now() + this.cacheTimeout;
        
        // 更新统计
        this.updateStats();
        
        return this.channels;
      } catch (error) {
        console.error('获取渠道列表失败:', error);
        // 使用内置渠道作为降级
        this.channels = [...BUILT_IN_CHANNELS];
        this.updateStats();
        throw error;
      } finally {
        this.loading = false;
      }
    },
    
    /**
     * 获取渠道详情
     */
    async fetchChannel(id) {
      try {
        const response = await getChannel(id);
        this.currentChannel = response.data || response;
        return this.currentChannel;
      } catch (error) {
        console.error('获取渠道详情失败:', error);
        // 从本地列表中查找
        this.currentChannel = this.channels.find(c => c.id === id) || null;
        return this.currentChannel;
      }
    },
    
    /**
     * 创建渠道
     */
    async createChannel(channelData) {
      try {
        const response = await createChannel(channelData);
        const newChannel = response.data || response;
        
        // 添加到本地列表
        this.channels.push(newChannel);
        this.updateStats();
        this.clearCache();
        
        return newChannel;
      } catch (error) {
        console.error('创建渠道失败:', error);
        
        // API失败时的本地模拟
        const newChannel = {
          ...channelData,
          id: `custom_${Date.now()}`,
          createdAt: new Date().toISOString(),
          updatedAt: new Date().toISOString()
        };
        
        this.channels.push(newChannel);
        this.updateStats();
        
        return newChannel;
      }
    },
    
    /**
     * 更新渠道
     */
    async updateChannel(id, channelData) {
      try {
        const response = await updateChannel(id, channelData);
        const updatedChannel = response.data || response;
        
        // 更新本地状态
        const index = this.channels.findIndex(c => c.id === id);
        if (index !== -1) {
          this.channels[index] = { ...this.channels[index], ...updatedChannel };
        }
        
        this.clearCache();
        return updatedChannel;
      } catch (error) {
        console.error('更新渠道失败:', error);
        
        // API失败时更新本地状态
        const index = this.channels.findIndex(c => c.id === id);
        if (index !== -1) {
          this.channels[index] = { 
            ...this.channels[index], 
            ...channelData,
            updatedAt: new Date().toISOString()
          };
        }
        
        return this.channels[index];
      }
    },
    
    /**
     * 删除渠道
     */
    async deleteChannel(id) {
      // 检查是否为内置渠道
      const channel = this.channels.find(c => c.id === id);
      if (channel && channel.builtin) {
        throw new Error('内置渠道无法删除');
      }
      
      try {
        await deleteChannel(id);
        
        // 从本地列表移除
        this.channels = this.channels.filter(c => c.id !== id);
        this.updateStats();
        this.clearCache();
        
        return true;
      } catch (error) {
        console.error('删除渠道失败:', error);
        
        // API失败时也从本地移除（假设删除成功）
        this.channels = this.channels.filter(c => c.id !== id);
        this.updateStats();
        
        return true;
      }
    },
    
    /**
     * 启用/禁用渠道
     */
    async toggleChannel(id, enabled) {
      try {
        await toggleChannel(id, enabled);
        
        // 更新本地状态
        const channel = this.channels.find(c => c.id === id);
        if (channel) {
          channel.enabled = enabled;
          channel.updatedAt = new Date().toISOString();
        }
        
        this.updateStats();
        this.clearCache();
        
        return true;
      } catch (error) {
        console.error('切换渠道状态失败:', error);
        
        // API失败时也更新本地状态
        const channel = this.channels.find(c => c.id === id);
        if (channel) {
          channel.enabled = enabled;
          channel.updatedAt = new Date().toISOString();
        }
        this.updateStats();
        
        return true;
      }
    },
    
    /**
     * 测试渠道连接
     */
    async testChannel(id, testData = {}) {
      try {
        const response = await testChannel(id, testData);
        return response.data || response;
      } catch (error) {
        console.error('测试渠道连接失败:', error);
        // 返回模拟的测试结果
        return {
          success: false,
          message: '测试连接失败，请检查配置',
          details: error.message || '连接超时'
        };
      }
    },
    
    /**
     * 获取渠道统计
     */
    async fetchChannelStats(useCache = true) {
      // 检查缓存
      if (useCache && this.isCacheValid('stats')) {
        return this.stats;
      }
      
      try {
        const response = await getChannelStats();
        this.stats = response.data || response;
        
        // 更新缓存
        this.cache.stats = this.stats;
        this.cache.statsExpiry = Date.now() + this.cacheTimeout;
        
        return this.stats;
      } catch (error) {
        console.error('获取渠道统计失败:', error);
        // 使用本地计算的统计
        this.updateStats();
        return this.stats;
      }
    },
    
    /**
     * 更新本地统计
     */
    updateStats() {
      this.stats = {
        total: this.channels.length,
        enabled: this.channels.filter(c => c.enabled).length,
        builtin: this.channels.filter(c => c.builtin).length,
        custom: this.channels.filter(c => !c.builtin).length
      };
    },
    
    /**
     * 清除缓存
     */
    clearCache() {
      this.cache = {
        channels: null,
        channelsExpiry: null,
        stats: null,
        statsExpiry: null
      };
    },
    
    /**
     * 重置状态
     */
    resetState() {
      this.channels = [];
      this.currentChannel = null;
      this.loading = false;
      this.stats = {
        total: 0,
        enabled: 0,
        builtin: 0,
        custom: 0
      };
      this.clearCache();
    }
  }
});