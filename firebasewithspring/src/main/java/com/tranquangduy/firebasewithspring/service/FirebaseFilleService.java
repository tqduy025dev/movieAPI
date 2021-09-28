/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tranquangduy.firebasewithspring.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.HttpMethod;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.cloud.StorageClient;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author UserName
 */
@Service
public class FirebaseFilleService {

    private Storage storage;

    @EventListener
    public void init(ApplicationReadyEvent event) throws IOException {

        InputStream serviceAccount = this.getClass().getClassLoader().getResourceAsStream("./demojavaspring-firebase-adminsdk-yovhy-e445839113.json");

        storage = StorageOptions.newBuilder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setProjectId("demojavaspring.appspot.com")
                .build()
                .getService();

    }

    public String upLoadFile(MultipartFile file) throws IOException {
        String fileName = generateFileName(file.getOriginalFilename());
        Bucket bucket = StorageClient.getInstance().bucket();

        Map<String, String> map = new HashMap<>();
        map.put("firebaseStorageDownloadTokens", fileName);

        BlobId blobId = BlobId.of(bucket.getName(), fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setMetadata(map)
                .setContentType(file.getContentType())
                .build();
        
        storage.create(blobInfo, file.getBytes());
       
        String urlDownload = "https://storage.googleapis.com/demojavaspring.appspot.com/"+fileName;
        
        return urlDownload;
    }

    private String generateFileName(String originalFileName) {
        return UUID.randomUUID().toString() + "." + getExtension(originalFileName);
    }

    private String getExtension(String originalFileName) {
        return StringUtils.getFilenameExtension(originalFileName);
    }

}
