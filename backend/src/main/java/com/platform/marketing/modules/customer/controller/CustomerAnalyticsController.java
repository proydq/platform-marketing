package com.platform.marketing.modules.customer.controller;

import com.platform.marketing.modules.customer.service.CustomerAnalyticsService;
import com.platform.marketing.util.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/customer-analytics")
@CrossOrigin(origins = "*")
public class CustomerAnalyticsController {

    @Autowired
    private CustomerAnalyticsService customerAnalyticsService;

    /**
     * 获取客户分析概览统计
     */
    @GetMapping("/overview")
    public ResponseEntity<Map<String, Object>> getOverview(@RequestParam Map<String, Object> params) {
        Map<String, Object> overview = customerAnalyticsService.getOverviewStats(params);
        return ResponseEntity.success(overview);
    }

    /**
     * 获取客户增长趋势数据
     */
    @GetMapping("/growth-trend")
    public ResponseEntity<List<Map<String, Object>>> getGrowthTrend(@RequestParam Map<String, Object> params) {
        List<Map<String, Object>> data = customerAnalyticsService.getGrowthTrendData(params);
        return ResponseEntity.success(data);
    }

    /**
     * 获取客户来源分布数据
     */
    @GetMapping("/source-distribution")
    public ResponseEntity<List<Map<String, Object>>> getSourceDistribution(@RequestParam Map<String, Object> params) {
        List<Map<String, Object>> data = customerAnalyticsService.getSourceDistributionData(params);
        return ResponseEntity.success(data);
    }

    /**
     * 获取客户状态分布数据
     */
    @GetMapping("/status-distribution")
    public ResponseEntity<List<Map<String, Object>>> getStatusDistribution(@RequestParam Map<String, Object> params) {
        List<Map<String, Object>> data = customerAnalyticsService.getStatusDistributionData(params);
        return ResponseEntity.success(data);
    }

    /**
     * 获取转化率数据
     */
    @GetMapping("/conversion-rate")
    public ResponseEntity<List<Map<String, Object>>> getConversionRate(@RequestParam Map<String, Object> params) {
        List<Map<String, Object>> data = customerAnalyticsService.getConversionRateData(params);
        return ResponseEntity.success(data);
    }

    /**
     * 获取分析表格数据
     */
    @GetMapping("/table-data")
    public ResponseEntity<List<Map<String, Object>>> getTableData(@RequestParam Map<String, Object> params) {
        List<Map<String, Object>> data = customerAnalyticsService.getAnalyticsTableData(params);
        return ResponseEntity.success(data);
    }

    /**
     * 导出分析报告
     */
    @PostMapping("/export")
    public ResponseEntity<String> exportReport(@RequestBody Map<String, Object> params) {
        customerAnalyticsService.exportAnalyticsReport(params);
        return ResponseEntity.success("导出成功");
    }
}