//package com.bej03.notis.controller;
//
//import com.google.cloud.storage.BlobId;
//import com.google.cloud.storage.BlobInfo;
//import com.google.cloud.storage.Storage;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//
//@RestController
//@RequestMapping("/gcp")
//public class GCPStorageController {
//
//    @Autowired
//    private Storage storage;
//
//    // File upload endpoint to handle multipart file upload
//    @PostMapping("/upload")
//    public String uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
//        // Define the GCP bucket name
//        String bucketName = "bej03bucket";  // Your bucket name
//
//        // Get the file name from the uploaded file
//        String fileName = file.getOriginalFilename();
//
//        // Create BlobId for the file in GCP
//        BlobId blobId = BlobId.of(bucketName, fileName);
//
//        // Create BlobInfo with metadata about the file
//        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
//
//        // Convert the uploaded file to a byte array
//        byte[] fileBytes = file.getBytes();
//
//        // Upload the file to GCP storage
//        storage.create(blobInfo, fileBytes);
//
//        // Construct the file's public URL (for reference)
//        String fileUrl = "https://storage.googleapis.com/" + bucketName + "/" + fileName;
//
//        // Return a success message along with the file URL
//        return "File uploaded successfully to GCP! File URL: " + fileUrl;
//    }
//}
//
//
//
//
//
//
