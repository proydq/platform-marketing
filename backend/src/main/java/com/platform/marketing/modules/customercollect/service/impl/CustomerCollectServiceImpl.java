package com.platform.marketing.modules.customercollect.service.impl;

import com.platform.marketing.modules.customercollect.entity.CustomerCollect;
import com.platform.marketing.modules.customercollect.repository.CustomerCollectRepository;
import com.platform.marketing.modules.customercollect.service.CustomerCollectService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerCollectServiceImpl implements CustomerCollectService {

    private final CustomerCollectRepository customerCollectRepository;

    public CustomerCollectServiceImpl(CustomerCollectRepository customerCollectRepository) {
        this.customerCollectRepository = customerCollectRepository;
    }

    @Override
    public Page<CustomerCollect> search(String keyword, Pageable pageable) {
        if (keyword == null) keyword = "";
        return customerCollectRepository.search(keyword, pageable);
    }

    @Override
    public Optional<CustomerCollect> findById(String id) {
        return customerCollectRepository.findById(id);
    }

    @Override
    @Transactional
    public CustomerCollect create(CustomerCollect customerCollect) {
        return customerCollectRepository.save(customerCollect);
    }

    @Override
    @Transactional
    public CustomerCollect update(String id, CustomerCollect customerCollect) {
        CustomerCollect existing = customerCollectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("CustomerCollect not found"));
        existing.setName(customerCollect.getName());
        existing.setPhone(customerCollect.getPhone());
        existing.setEmail(customerCollect.getEmail());
        existing.setCompany(customerCollect.getCompany());
        existing.setJobTitle(customerCollect.getJobTitle());
        existing.setSource(customerCollect.getSource());
        existing.setNotes(customerCollect.getNotes());
        return customerCollectRepository.save(existing);
    }

    @Override
    @Transactional
    public void delete(String id) {
        customerCollectRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteBatch(List<String> ids) {
        ids.forEach(customerCollectRepository::deleteById);
    }
}
