package com.bej03.notis.service;

import com.bej03.notis.model.Record;
import com.bej03.notis.model.RecordStatus;
import com.bej03.notis.repository.RecordRepository;
import jakarta.transaction.Transactional;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class BatchProcessingConsumer {

    private static final Logger logger = LoggerFactory.getLogger(BatchProcessingConsumer.class);

    @Autowired
    private RecordRepository recordRepository;

    // Listens for messages from the RabbitMQ queue
    @RabbitListener(queues = "batch-id-queue")
    public void processBatchIdMessage(String batchId) {
        logger.info("Received Batch ID: {}", batchId);

        // Step 1: Retrieve records associated with the batchId
        List<Record> records = recordRepository.findByBatchId(batchId);
        logger.info("Retrieved {} records for Batch ID: {}", records.size(), batchId);

        if (records.isEmpty()) {
            logger.warn("No records found for Batch ID: {}", batchId);
            return;
        }

        // Step 2: Process each record
        records.stream().forEach(this::processRecord);


        // Step 3: Update records' status to 'PROCESSED' after successful processing
        updateRecordsStatus(records);

        logger.info("Batch processing complete for Batch ID: {}", batchId);
    }

    // Process individual records
    private void processRecord(Record record) {
        try {
            // Example processing logic: This is where your record processing happens
            logger.info("Processing record: {}", record.getId());

            // If processing is successful, mark it as processed
            record.setStatus(RecordStatus.PROCESSED);
            logger.info("Record {} processed successfully", record.getId());

        } catch (Exception e) {
            // If an error occurs, mark the record as failed
            record.setStatus(RecordStatus.FAILED);
            logger.error("Error processing record with ID: {}", record.getId(), e);
        }
    }

    // This method updates the status of the records in the database
    @Transactional
    private void updateRecordsStatus(List<Record> records) {
        try {
            // Save all updated records in the database
            recordRepository.saveAll(records);
            logger.info("Updated records' status to PROCESSED (or FAILED) as needed.");
        } catch (Exception e) {
            // If an error occurs while saving, log the error
            logger.error("Error updating records' status", e);
        }
    }
}
