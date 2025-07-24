package com.platform.marketing.dashboard.repository;

import com.platform.marketing.dashboard.entity.RecentTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecentTaskRepository extends JpaRepository<RecentTask, String> {
    List<RecentTask> findTop10ByOrderByCreatedAtDesc();
}
