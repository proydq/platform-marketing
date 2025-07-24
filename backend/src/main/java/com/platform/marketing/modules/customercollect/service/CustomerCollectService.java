package com.platform.marketing.modules.customercollect.service;

import com.platform.marketing.modules.customercollect.entity.CustomerCollect;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CustomerCollectService {
    Page<CustomerCollect> search(String keyword, Pageable pageable);
    Optional<CustomerCollect> findById(String id);
    CustomerCollect create(CustomerCollect customerCollect);
    void delete(String id);
    void deleteBatch(List<String> ids);
}
