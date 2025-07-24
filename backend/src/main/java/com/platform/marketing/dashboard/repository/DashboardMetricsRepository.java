package com.platform.marketing.dashboard.repository;

import com.platform.marketing.dashboard.entity.DashboardMetrics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DashboardMetricsRepository extends JpaRepository<DashboardMetrics, String> {
    DashboardMetrics findTopByOrderByCreatedAtDesc();
    List<DashboardMetrics> findTop30ByOrderByCreatedAtAsc();
}
