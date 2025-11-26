package com.dtao.alien.repository;

import com.dtao.alien.model.Report;
import com.dtao.alien.model.ReportStage;
import com.dtao.alien.model.ReportStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends MongoRepository<Report, String> {

    // 🧾 User’s own reports
    List<Report> findByCreatedBy(String email);

    long countByStatus(ReportStatus status);

    // 🔍 Fetch reports by current single stage
    List<Report> findByCurrentStage(ReportStage stage);

    // 🔍 Fetch reports matching multiple stages (used for Principal)
    List<Report> findByCurrentStageIn(List<ReportStage> stages);

    // 🔍 Generic status-based filter (optional)
    List<Report> findByStatus(String status);

    // 🧠 NEW (Optional): Fetch all reports sorted by last updated (latest first)
    List<Report> findAllByOrderByUpdatedAtDesc();
}
