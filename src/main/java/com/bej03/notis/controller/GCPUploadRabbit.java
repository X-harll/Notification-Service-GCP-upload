package com.bej03.notis.controller;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/gcp")
public class GCPUploadRabbit {

    @Autowired
    private Storage storage;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private static final String BUCKET_NAME = "bej03bucket"; // GCP bucket name
    private static final String QUEUE_NAME = "bejo3-queue"; // RabbitMQ queue name

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();

        // Upload file to GCP
        BlobId blobId = BlobId.of(BUCKET_NAME, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        storage.create(blobInfo, file.getBytes());

        // Construct message for RabbitMQ
        String message = String.format("File uploaded to GCP: Bucket=%s, File=%s", BUCKET_NAME, fileName);

        // Send message to RabbitMQ
        rabbitTemplate.convertAndSend(QUEUE_NAME, message);

        return "File uploaded to GCP and notification sent to RabbitMQ!";
    }
}
