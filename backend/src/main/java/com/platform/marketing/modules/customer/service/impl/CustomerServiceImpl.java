package com.platform.marketing.modules.customer.service.impl;

import com.platform.marketing.modules.customer.entity.Customer;
import com.platform.marketing.modules.customer.repository.CustomerRepository;
import com.platform.marketing.modules.customer.service.CustomerService;
import com.platform.marketing.dto.CustomerImportDto;
import com.platform.marketing.util.ExcelUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Page<Customer> search(String keyword, Pageable pageable) {
        if (keyword == null) keyword = "";
        return customerRepository.search(keyword, pageable);
    }

    @Override
    public Optional<Customer> findById(String id) {
        return customerRepository.findById(id);
    }

    @Override
    @Transactional
    public Customer create(Customer customer) {
        return customerRepository.save(customer);
    }

    @Override
    @Transactional
    public Customer update(String id, Customer customer) {
        Customer existing = customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
        existing.setName(customer.getName());
        existing.setEmail(customer.getEmail());
        existing.setPhone(customer.getPhone());
        existing.setSource(customer.getSource());
        existing.setStatus(customer.getStatus());
        existing.setRemark(customer.getRemark());
        return customerRepository.save(existing);
    }

    @Override
    @Transactional
    public void delete(String id) {
        customerRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void updateStatus(String id, String status) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
        customer.setStatus(status);
        customerRepository.save(customer);
    }

    @Override
    @Transactional
    public void importCustomers(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            List<CustomerImportDto> importedList = ExcelUtils.parseExcel(inputStream);
            List<Customer> customers = importedList.stream().map(dto -> {
                Customer customer = new Customer();
                customer.setName(dto.getName());
                customer.setPhone(dto.getPhone());
                customer.setEmail(dto.getEmail());
                customer.setRemark(dto.getRemark());
                return customer;
            }).collect(Collectors.toList());
            customerRepository.saveAll(customers);
        } catch (IOException e) {
            throw new RuntimeException("导入失败：" + e.getMessage());
        }
    }
}
