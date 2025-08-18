package com.platform.marketing.modules.ai.service;

import com.platform.marketing.modules.ai.dto.*;
import java.util.List;
import java.util.Map;

/**
 * AI营销助手服务接口
 */
public interface AIMarketingAssistantService {

    /**
     * 关键词分析
     */
    List<KeywordAnalysisDto> analyzeKeywords(KeywordRequestDto request);

    /**
     * 获取关键词建议
     */
    List<String> getKeywordSuggestions(String industry, String market, int limit);

    /**
     * 获取产品推荐
     */
    List<ProductRecommendationDto> getProductRecommendations(ProductRequestDto request);

    /**
     * 获取个性化产品推荐
     */
    List<ProductRecommendationDto> getPersonalizedProducts(String customerId);

    /**
     * 生成营销策略
     */
    List<MarketingStrategyDto> generateMarketingStrategy(StrategyRequestDto request);

    /**
     * 获取策略模板
     */
    List<StrategyTemplateDto> getStrategyTemplates(String goal, String industry);

    /**
     * 智能内容生成
     */
    ContentGenerationResultDto generateIntelligentContent(ContentRequestDto request);

    /**
     * 内容优化
     */
    ContentOptimizationDto optimizeContent(ContentOptimizationRequestDto request);

    /**
     * 市场趋势分析
     */
    MarketTrendsDto analyzeMarketTrends(String industry, String region, int days);

    /**
     * 竞争对手分析
     */
    List<CompetitorAnalysisDto> getCompetitorAnalysis(String industry, String market, int limit);

    /**
     * 客户洞察分析
     */
    CustomerInsightsDto getCustomerInsights(String customerId);

    /**
     * 客户行为预测
     */
    CustomerPredictionDto predictCustomerBehavior(CustomerPredictionRequestDto request);

    /**
     * AI助手对话
     */
    AIAssistantResponseDto askAIAssistant(AIAssistantRequestDto request);

    /**
     * 获取AI推荐
     */
    List<AIRecommendationDto> getAIRecommendations(String userId, String context);

    /**
     * 获取仪表板统计数据
     */
    Map<String, Object> getAIDashboardStats();

    /**
     * 批量处理AI任务
     */
    BatchProcessResultDto processBatchAITasks(BatchProcessRequestDto request);
}