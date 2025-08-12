package com.platform.marketing.modules.customercollect.controller;

import com.platform.marketing.modules.customercollect.entity.CustomerCollect;
import com.platform.marketing.modules.customercollect.service.CustomerCollectService;
import com.platform.marketing.util.ResponseEntity;
import com.platform.marketing.util.ResponsePageDataEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer-collect")
public class CustomerCollectController {

    private final CustomerCollectService customerCollectService;

    public CustomerCollectController(CustomerCollectService customerCollectService) {
        this.customerCollectService = customerCollectService;
    }

    @GetMapping
    @PreAuthorize("hasPermission('customer-collect:list')")
    public ResponseEntity<ResponsePageDataEntity<CustomerCollect>> list(@RequestParam(defaultValue = "") String keyword,
                                                                       @RequestParam(defaultValue = "0") int page,
                                                                       @RequestParam(defaultValue = "10") int size) {
        Page<CustomerCollect> p = customerCollectService.search(keyword, PageRequest.of(page, size));
        return ResponseEntity.success(new ResponsePageDataEntity<CustomerCollect>(p.getTotalElements(), p.getContent()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasPermission('customer-collect:view')")
    public ResponseEntity<CustomerCollect> get(@PathVariable String id) {
        return customerCollectService.findById(id)
                .map(ResponseEntity::success)
                .orElse(ResponseEntity.fail(404, "Not Found"));
    }

    @PostMapping
    @PreAuthorize("hasPermission('customer-collect:create')")
    public ResponseEntity<CustomerCollect> create(@RequestBody CustomerCollect customerCollect) {
        return ResponseEntity.success(customerCollectService.create(customerCollect));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasPermission('customer-collect:update')")
    public ResponseEntity<CustomerCollect> update(@PathVariable String id, @RequestBody CustomerCollect customerCollect) {
        return ResponseEntity.success(customerCollectService.update(id, customerCollect));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasPermission('customer-collect:delete')")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        customerCollectService.delete(id);
        return ResponseEntity.success(null);
    }

    @DeleteMapping
    @PreAuthorize("hasPermission('customer-collect:batch-delete')")
    public ResponseEntity<Void> deleteBatch(@RequestBody List<String> ids) {
        customerCollectService.deleteBatch(ids);
        return ResponseEntity.success(null);
    }

}
