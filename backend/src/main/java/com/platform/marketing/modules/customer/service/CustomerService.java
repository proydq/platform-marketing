package com.platform.marketing.modules.customer.service;

import com.platform.marketing.modules.customer.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface CustomerService {
    // 搜索客户（支持多条件）
    Page<Customer> search(String keyword, String status, String source, Pageable pageable);
    
    // 简单关键词搜索（保持向后兼容）
    Page<Customer> search(String keyword, Pageable pageable);
    
    // 基础CRUD操作
    Optional<Customer> findById(String id);
    Customer create(Customer customer);
    Customer update(String id, Customer customer);
    void delete(String id);
    
    // 状态管理
    void updateStatus(String id, String status);
    
    // 批量操作
    void batchDelete(List<String> customerIds);
    
    // 数据导入导出
    String importCustomers(MultipartFile file);
    String exportCustomers(String keyword, String status, String source);
    
    // 统计功能
    List<String> getDistinctSources();
}
