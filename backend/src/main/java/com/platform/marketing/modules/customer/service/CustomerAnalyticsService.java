package com.platform.marketing.modules.customer.service;

import java.util.List;
import java.util.Map;

public interface CustomerAnalyticsService {
    
    /**
     * 获取客户分析概览统计
     */
    Map<String, Object> getOverviewStats(Map<String, Object> params);
    
    /**
     * 获取客户增长趋势数据
     */
    List<Map<String, Object>> getGrowthTrendData(Map<String, Object> params);
    
    /**
     * 获取客户来源分布数据
     */
    List<Map<String, Object>> getSourceDistributionData(Map<String, Object> params);
    
    /**
     * 获取客户状态分布数据
     */
    List<Map<String, Object>> getStatusDistributionData(Map<String, Object> params);
    
    /**
     * 获取转化率数据
     */
    List<Map<String, Object>> getConversionRateData(Map<String, Object> params);
    
    /**
     * 获取分析表格数据
     */
    List<Map<String, Object>> getAnalyticsTableData(Map<String, Object> params);
    
    /**
     * 导出分析报告
     */
    void exportAnalyticsReport(Map<String, Object> params);
}