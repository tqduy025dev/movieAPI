/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tranquangduy.firebasewithspring.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import java.io.IOException;
import java.io.InputStream;
import javax.annotation.PostConstruct;
import org.springframework.stereotype.Service;

/**
 *
 * @author UserName
 */
@Service
public class FirebaseInitializer {

    @PostConstruct
    private void intiDB() throws IOException {
        InputStream serviceAccount = this.getClass().getClassLoader().getResourceAsStream("./demojavaspring-firebase-adminsdk-yovhy-d8d00e7d10.json");

        assert serviceAccount != null;
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://demojavaspring-default-rtdb.firebaseio.com")
                .setStorageBucket("demojavaspring.appspot.com")
                .build();

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
        }

    }

    public Firestore getFirebase() {
        return FirestoreClient.getFirestore();
    }
    
    

}
