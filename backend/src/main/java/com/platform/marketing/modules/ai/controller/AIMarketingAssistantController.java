package com.platform.marketing.modules.ai.controller;

import com.platform.marketing.modules.ai.dto.*;
import com.platform.marketing.modules.ai.service.AIMarketingAssistantService;
import com.platform.marketing.util.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * AI营销助手控制器
 * 提供关键词研究、产品推荐、策略生成、内容创建等AI驱动的营销功能
 */
@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*")
public class AIMarketingAssistantController {

    @Autowired
    private AIMarketingAssistantService aiMarketingAssistantService;

    /**
     * 关键词分析与研究
     */
    @PostMapping("/keywords/analyze")
    public ResponseEntity<List<KeywordAnalysisDto>> analyzeKeywords(@RequestBody KeywordRequestDto request) {
        try {
            List<KeywordAnalysisDto> results = aiMarketingAssistantService.analyzeKeywords(request);
            return ResponseEntity.success(results);
        } catch (Exception e) {
            return ResponseEntity.error("关键词分析失败: " + e.getMessage());
        }
    }

    /**
     * 获取关键词建议
     */
    @GetMapping("/keywords/suggestions")
    public ResponseEntity<List<String>> getKeywordSuggestions(
            @RequestParam String industry,
            @RequestParam String market,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            List<String> suggestions = aiMarketingAssistantService.getKeywordSuggestions(industry, market, limit);
            return ResponseEntity.success(suggestions);
        } catch (Exception e) {
            return ResponseEntity.error("获取关键词建议失败: " + e.getMessage());
        }
    }

    /**
     * 生成产品推荐
     */
    @PostMapping("/products/recommendations")
    public ResponseEntity<List<ProductRecommendationDto>> getProductRecommendations(@RequestBody ProductRequestDto request) {
        try {
            List<ProductRecommendationDto> recommendations = aiMarketingAssistantService.getProductRecommendations(request);
            return ResponseEntity.success(recommendations);
        } catch (Exception e) {
            return ResponseEntity.error("产品推荐生成失败: " + e.getMessage());
        }
    }

    /**
     * 获取个性化产品推荐
     */
    @GetMapping("/products/personalized/{customerId}")
    public ResponseEntity<List<ProductRecommendationDto>> getPersonalizedProducts(@PathVariable String customerId) {
        try {
            List<ProductRecommendationDto> products = aiMarketingAssistantService.getPersonalizedProducts(customerId);
            return ResponseEntity.success(products);
        } catch (Exception e) {
            return ResponseEntity.error("获取个性化推荐失败: " + e.getMessage());
        }
    }

    /**
     * 生成营销策略
     */
    @PostMapping("/strategy/generate")
    public ResponseEntity<List<MarketingStrategyDto>> generateMarketingStrategy(@RequestBody StrategyRequestDto request) {
        try {
            List<MarketingStrategyDto> strategies = aiMarketingAssistantService.generateMarketingStrategy(request);
            return ResponseEntity.success(strategies);
        } catch (Exception e) {
            return ResponseEntity.error("策略生成失败: " + e.getMessage());
        }
    }

    /**
     * 获取策略模板
     */
    @GetMapping("/strategy/templates")
    public ResponseEntity<List<StrategyTemplateDto>> getStrategyTemplates(
            @RequestParam(required = false) String goal,
            @RequestParam(required = false) String industry) {
        try {
            List<StrategyTemplateDto> templates = aiMarketingAssistantService.getStrategyTemplates(goal, industry);
            return ResponseEntity.success(templates);
        } catch (Exception e) {
            return ResponseEntity.error("获取策略模板失败: " + e.getMessage());
        }
    }

    /**
     * 智能内容生成
     */
    @PostMapping("/content/generate")
    public ResponseEntity<ContentGenerationResultDto> generateIntelligentContent(@RequestBody ContentRequestDto request) {
        try {
            ContentGenerationResultDto result = aiMarketingAssistantService.generateIntelligentContent(request);
            return ResponseEntity.success(result);
        } catch (Exception e) {
            return ResponseEntity.error("内容生成失败: " + e.getMessage());
        }
    }

    /**
     * 内容优化建议
     */
    @PostMapping("/content/optimize")
    public ResponseEntity<ContentOptimizationDto> optimizeContent(@RequestBody ContentOptimizationRequestDto request) {
        try {
            ContentOptimizationDto optimization = aiMarketingAssistantService.optimizeContent(request);
            return ResponseEntity.success(optimization);
        } catch (Exception e) {
            return ResponseEntity.error("内容优化失败: " + e.getMessage());
        }
    }

    /**
     * 市场趋势分析
     */
    @GetMapping("/market/trends")
    public ResponseEntity<MarketTrendsDto> analyzeMarketTrends(
            @RequestParam String industry,
            @RequestParam String region,
            @RequestParam(defaultValue = "30") int days) {
        try {
            MarketTrendsDto trends = aiMarketingAssistantService.analyzeMarketTrends(industry, region, days);
            return ResponseEntity.success(trends);
        } catch (Exception e) {
            return ResponseEntity.error("市场趋势分析失败: " + e.getMessage());
        }
    }

    /**
     * 竞争对手分析
     */
    @GetMapping("/market/competitors")
    public ResponseEntity<List<CompetitorAnalysisDto>> getCompetitorAnalysis(
            @RequestParam String industry,
            @RequestParam String market,
            @RequestParam(defaultValue = "5") int limit) {
        try {
            List<CompetitorAnalysisDto> competitors = aiMarketingAssistantService.getCompetitorAnalysis(industry, market, limit);
            return ResponseEntity.success(competitors);
        } catch (Exception e) {
            return ResponseEntity.error("竞争对手分析失败: " + e.getMessage());
        }
    }

    /**
     * 客户洞察分析
     */
    @GetMapping("/insights/customer/{customerId}")
    public ResponseEntity<CustomerInsightsDto> getCustomerInsights(@PathVariable String customerId) {
        try {
            CustomerInsightsDto insights = aiMarketingAssistantService.getCustomerInsights(customerId);
            return ResponseEntity.success(insights);
        } catch (Exception e) {
            return ResponseEntity.error("客户洞察分析失败: " + e.getMessage());
        }
    }

    /**
     * 客户行为预测
     */
    @PostMapping("/insights/predict")
    public ResponseEntity<CustomerPredictionDto> predictCustomerBehavior(@RequestBody CustomerPredictionRequestDto request) {
        try {
            CustomerPredictionDto prediction = aiMarketingAssistantService.predictCustomerBehavior(request);
            return ResponseEntity.success(prediction);
        } catch (Exception e) {
            return ResponseEntity.error("客户行为预测失败: " + e.getMessage());
        }
    }

    /**
     * AI助手对话
     */
    @PostMapping("/assistant/chat")
    public ResponseEntity<AIAssistantResponseDto> askAIAssistant(@RequestBody AIAssistantRequestDto request) {
        try {
            AIAssistantResponseDto response = aiMarketingAssistantService.askAIAssistant(request);
            return ResponseEntity.success(response);
        } catch (Exception e) {
            return ResponseEntity.error("AI助手响应失败: " + e.getMessage());
        }
    }

    /**
     * 获取AI推荐
     */
    @GetMapping("/assistant/recommendations")
    public ResponseEntity<List<AIRecommendationDto>> getAIRecommendations(
            @RequestParam String userId,
            @RequestParam(required = false) String context) {
        try {
            List<AIRecommendationDto> recommendations = aiMarketingAssistantService.getAIRecommendations(userId, context);
            return ResponseEntity.success(recommendations);
        } catch (Exception e) {
            return ResponseEntity.error("获取AI推荐失败: " + e.getMessage());
        }
    }

    /**
     * 获取AI助手仪表板数据
     */
    @GetMapping("/dashboard/stats")
    public ResponseEntity<Map<String, Object>> getAIDashboardStats() {
        try {
            Map<String, Object> stats = aiMarketingAssistantService.getAIDashboardStats();
            return ResponseEntity.success(stats);
        } catch (Exception e) {
            return ResponseEntity.error("获取仪表板数据失败: " + e.getMessage());
        }
    }

    /**
     * 批量处理AI任务
     */
    @PostMapping("/batch/process")
    public ResponseEntity<BatchProcessResultDto> processBatchAITasks(@RequestBody BatchProcessRequestDto request) {
        try {
            BatchProcessResultDto result = aiMarketingAssistantService.processBatchAITasks(request);
            return ResponseEntity.success(result);
        } catch (Exception e) {
            return ResponseEntity.error("批量处理失败: " + e.getMessage());
        }
    }
}