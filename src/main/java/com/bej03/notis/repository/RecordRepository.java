package com.bej03.notis.repository;

import com.bej03.notis.model.Record;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecordRepository extends JpaRepository<Record, Long> {

    List<Record> findByBatchId(String batchId);
}
