package com.platform.marketing.modules.customer.service;

import com.platform.marketing.modules.customer.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface CustomerService {
    Page<Customer> search(String keyword, Pageable pageable);
    Optional<Customer> findById(String id);
    Customer create(Customer customer);
    Customer update(String id, Customer customer);
    void delete(String id);
    void updateStatus(String id, String status);
    void importCustomers(MultipartFile file);
}
