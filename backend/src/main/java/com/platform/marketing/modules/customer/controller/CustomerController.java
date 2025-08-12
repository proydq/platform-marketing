package com.platform.marketing.modules.customer.controller;

import com.platform.marketing.modules.customer.entity.Customer;
import com.platform.marketing.modules.customer.service.CustomerService;
import com.platform.marketing.util.ResponseEntity;
import com.platform.marketing.util.ResponsePageDataEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

/**
 * 客户管理控制器
 * 
 * @author System
 * @version 1.0
 * @since 2025-08-12
 */
@RestController
@RequestMapping("/api/v1/customers")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    /**
     * 分页查询客户列表
     */
    @GetMapping
    @PreAuthorize("hasPermission('customer:list')")
    public ResponseEntity<ResponsePageDataEntity<Customer>> list(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "") String status,
            @RequestParam(defaultValue = "") String source,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Customer> customerPage = customerService.search(keyword, status, source, pageable);
            ResponsePageDataEntity<Customer> response = new ResponsePageDataEntity<>(
                    customerPage.getTotalElements(), 
                    customerPage.getContent()
            );
            return ResponseEntity.success("查询成功", response);
        } catch (Exception e) {
            return ResponseEntity.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID获取客户详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasPermission('customer:view')")
    public ResponseEntity<Customer> getById(@PathVariable String id) {
        try {
            return customerService.findById(id)
                    .map(customer -> ResponseEntity.success("查询成功", customer))
                    .orElse(ResponseEntity.fail(404, "客户不存在"));
        } catch (Exception e) {
            return ResponseEntity.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 创建客户
     */
    @PostMapping
    @PreAuthorize("hasPermission('customer:create')")
    public ResponseEntity<Customer> create(@Valid @RequestBody Customer customer) {
        try {
            Customer createdCustomer = customerService.create(customer);
            return ResponseEntity.success("创建成功", createdCustomer);
        } catch (Exception e) {
            return ResponseEntity.error("创建失败: " + e.getMessage());
        }
    }

    /**
     * 更新客户信息
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasPermission('customer:update')")
    public ResponseEntity<Customer> update(@PathVariable String id, @Valid @RequestBody Customer customer) {
        try {
            Customer updatedCustomer = customerService.update(id, customer);
            return ResponseEntity.success("更新成功", updatedCustomer);
        } catch (Exception e) {
            return ResponseEntity.error("更新失败: " + e.getMessage());
        }
    }

    /**
     * 删除客户
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasPermission('customer:delete')")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        try {
            customerService.delete(id);
            return ResponseEntity.success("删除成功", null);
        } catch (Exception e) {
            return ResponseEntity.error("删除失败: " + e.getMessage());
        }
    }

    /**
     * 更新客户状态
     */
    @PostMapping("/update-status")
    @PreAuthorize("hasPermission('customer:status')")
    public ResponseEntity<Void> updateStatus(@RequestBody java.util.Map<String, Object> body) {
        try {
            String id = (String) body.get("id");
            String status = (String) body.get("status");
            
            if (id == null || status == null) {
                return ResponseEntity.fail(400, "客户ID和状态不能为空");
            }
            
            customerService.updateStatus(id, status);
            return ResponseEntity.success("状态更新成功", null);
        } catch (Exception e) {
            return ResponseEntity.error("状态更新失败: " + e.getMessage());
        }
    }

    /**
     * 批量删除客户
     */
    @DeleteMapping("/batch")
    @PreAuthorize("hasPermission('customer:delete')")
    public ResponseEntity<Void> batchDelete(@RequestBody List<String> customerIds) {
        try {
            if (customerIds == null || customerIds.isEmpty()) {
                return ResponseEntity.fail(400, "请选择要删除的客户");
            }
            
            customerService.batchDelete(customerIds);
            return ResponseEntity.success("批量删除成功", null);
        } catch (Exception e) {
            return ResponseEntity.error("批量删除失败: " + e.getMessage());
        }
    }

    /**
     * 导入客户数据
     */
    @PostMapping("/import")
    @PreAuthorize("hasPermission('customer:import')")
    public ResponseEntity<String> importCustomers(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.fail(400, "请选择要导入的文件");
            }
            
            String result = customerService.importCustomers(file);
            return ResponseEntity.success("导入成功", result);
        } catch (Exception e) {
            return ResponseEntity.error("导入失败: " + e.getMessage());
        }
    }

    /**
     * 导出客户数据
     */
    @GetMapping("/export")
    @PreAuthorize("hasPermission('customer:export')")
    public ResponseEntity<String> exportCustomers(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "") String status,
            @RequestParam(defaultValue = "") String source) {
        try {
            String result = customerService.exportCustomers(keyword, status, source);
            return ResponseEntity.success("导出成功", result);
        } catch (Exception e) {
            return ResponseEntity.error("导出失败: " + e.getMessage());
        }
    }

    /**
     * 获取客户来源统计
     */
    @GetMapping("/sources")
    @PreAuthorize("hasPermission('customer:list')")
    public ResponseEntity<List<String>> getSources() {
        try {
            List<String> sources = customerService.getDistinctSources();
            return ResponseEntity.success("查询成功", sources);
        } catch (Exception e) {
            return ResponseEntity.error("查询失败: " + e.getMessage());
        }
    }
}
