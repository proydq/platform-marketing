package com.platform.marketing.modules.customer.service.impl;

import com.platform.marketing.modules.customer.entity.Customer;
import com.platform.marketing.modules.customer.repository.CustomerRepository;
import com.platform.marketing.modules.customer.service.CustomerService;
import com.platform.marketing.dto.CustomerImportDto;
import com.platform.marketing.util.ExcelUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.criteria.Predicate;
import java.util.Optional;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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
    public Page<Customer> search(String keyword, String status, String source, Pageable pageable) {
        Specification<Customer> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // 关键词搜索（姓名、邮箱、电话）
            if (StringUtils.hasText(keyword)) {
                String likePattern = "%" + keyword + "%";
                Predicate keywordPredicate = criteriaBuilder.or(
                    criteriaBuilder.like(root.get("name"), likePattern),
                    criteriaBuilder.like(root.get("email"), likePattern),
                    criteriaBuilder.like(root.get("phone"), likePattern)
                );
                predicates.add(keywordPredicate);
            }
            
            // 状态筛选
            if (StringUtils.hasText(status)) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }
            
            // 来源筛选
            if (StringUtils.hasText(source)) {
                predicates.add(criteriaBuilder.equal(root.get("source"), source));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        
        return customerRepository.findAll(spec, pageable);
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
    public void batchDelete(List<String> customerIds) {
        if (customerIds != null && !customerIds.isEmpty()) {
            customerRepository.deleteAllById(customerIds);
        }
    }

    @Override
    @Transactional
    public String importCustomers(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            List<CustomerImportDto> importedList = ExcelUtils.parseExcel(inputStream);
            List<Customer> customers = importedList.stream().map(dto -> {
                Customer customer = new Customer();
                customer.setName(dto.getName());
                customer.setPhone(dto.getPhone());
                customer.setEmail(dto.getEmail());
                customer.setRemark(dto.getRemark());
                customer.setStatus("active"); // 默认状态
                customer.setSource("import"); // 导入来源
                return customer;
            }).collect(Collectors.toList());
            customerRepository.saveAll(customers);
            return "成功导入 " + customers.size() + " 条客户记录";
        } catch (IOException e) {
            throw new RuntimeException("导入失败：" + e.getMessage());
        }
    }

    @Override
    public String exportCustomers(String keyword, String status, String source) {
        // 这里可以实现导出逻辑，返回文件URL或路径
        // 暂时返回简单消息
        return "导出功能正在开发中";
    }

    @Override
    public List<String> getDistinctSources() {
        return customerRepository.findDistinctSources();
    }
}
