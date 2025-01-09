package com.bej03.notis.service;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Storage;
import com.opencsv.exceptions.CsvException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.Arrays;

import com.opencsv.CSVReader;
import com.bej03.notis.model.Record;
import com.bej03.notis.repository.RecordRepository;

@Service
public class FileProcessingConsumer {

    @Autowired
    private Storage storage;

    @Autowired
    private RecordRepository recordRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private static final String BATCH_QUEUE = "batch-id-queue";

    @RabbitListener(queues = "bejo3-queue")  // Listen to the RabbitMQ queue
    public void processFileUploadMessage(String message) throws IOException, CsvException {
        // Extract bucket and file name from the message
        String[] parts = message.split(", ");
        String bucketName = parts[0].split("=")[1].trim();
        String fileName = parts[1].split("=")[1].trim();

        // Download the file from GCP
        Blob blob = storage.get(bucketName, fileName);
        byte[] content = blob.getContent();

        // Generate a Batch ID (can use UUID or any other method)
        String batchId = UUID.randomUUID().toString();

        // Parse the CSV file
        List<Record> records = parseCSV(content, batchId);

        // Insert records into the database
        insertRecordsIntoDB(records);

        // Send Batch ID to RabbitMQ
        sendBatchIdToQueue(batchId);

        System.out.println("File processed, records inserted, and Batch ID sent to RabbitMQ!");
    }

    private List<Record> parseCSV(byte[] content, String batchId) throws IOException, CsvException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(content);
        Reader reader = new InputStreamReader(byteArrayInputStream, StandardCharsets.UTF_8);
        CSVReader csvReader = new CSVReader(reader);

        List<String[]> rows = csvReader.readAll();
        return mapRowsToRecords(rows, batchId);
    }

    private List<Record> mapRowsToRecords(List<String[]> rows, String batchId) {
        // Skip the header row (assuming first row contains column names)
        rows = rows.subList(1, rows.size());

        return rows.stream()
                .map(row -> {
                    // Print out the row data to debug
                    System.out.println("Row data: " + Arrays.toString(row));

                    // Skip invalid rows or rows with empty fields
                    if (row.length < 3 || row[0].isEmpty() || row[1].isEmpty() || row[2].isEmpty()) {
                        System.out.println("Skipping invalid row: " + Arrays.toString(row));
                        return null;  // Skip invalid rows
                    }

                    // Map the row fields to Record
                    String field1 = row[0];
                    String field2 = row[1];
                    String field3 = row[2];

                    // Return new Record, passing batchId
                    return new Record(field1, field2, field3, batchId);
                })
                .filter(record -> record != null)  // Filter out any null records due to invalid rows
                .toList();
    }

    private void insertRecordsIntoDB(List<Record> records) {
        // Save records with the batchId
        recordRepository.saveAll(records);
    }

    private void sendBatchIdToQueue(String batchId) {
        // Send the Batch ID to the RabbitMQ queue for further processing
        rabbitTemplate.convertAndSend(BATCH_QUEUE, batchId);

    }
}
