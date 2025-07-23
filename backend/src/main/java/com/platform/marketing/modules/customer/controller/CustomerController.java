package com.platform.marketing.modules.customer.controller;

import com.platform.marketing.modules.customer.entity.Customer;
import com.platform.marketing.modules.customer.service.CustomerService;
import com.platform.marketing.util.ResponseEntity;
import com.platform.marketing.util.ResponsePageDataEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    @PreAuthorize("hasPermission('customer:list')")
    public ResponseEntity<ResponsePageDataEntity<Customer>> list(@RequestParam(defaultValue = "") String keyword,
                                                                 @RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "10") int size) {
        Page<Customer> p = customerService.search(keyword, PageRequest.of(page, size));
        return ResponseEntity.success(new ResponsePageDataEntity<>(p.getTotalElements(), p.getContent()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasPermission('customer:view')")
    public ResponseEntity<Customer> get(@PathVariable String id) {
        return customerService.findById(id)
                .map(ResponseEntity::success)
                .orElse(ResponseEntity.fail(404, "Not Found"));
    }

    @PostMapping
    @PreAuthorize("hasPermission('customer:create')")
    public ResponseEntity<Customer> create(@RequestBody Customer customer) {
        return ResponseEntity.success(customerService.create(customer));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasPermission('customer:update')")
    public ResponseEntity<Customer> update(@PathVariable String id, @RequestBody Customer customer) {
        return ResponseEntity.success(customerService.update(id, customer));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasPermission('customer:delete')")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        customerService.delete(id);
        return ResponseEntity.success(null);
    }

    @PostMapping("/update-status")
    @PreAuthorize("hasPermission('customer:status')")
    public ResponseEntity<Void> updateStatus(@RequestBody java.util.Map<String, Object> body) {
        String id = (String) body.get("id");
        String status = (String) body.get("status");
        if (id == null || status == null) {
            return ResponseEntity.fail(400, "id and status required");
        }
        customerService.updateStatus(id, status);
        return ResponseEntity.success(null);
    }
}
