package com.tranquangduy.firebasewithspring.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.SetOptions;
import com.google.cloud.firestore.WriteResult;
import com.tranquangduy.firebasewithspring.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class UserService {
    @Autowired
    private FirebaseInitializer db;
    private static final String COLLECTION_NAME = "User";

    public boolean saveUser(User user){
        DocumentReference docRef = db.getFirebase().collection(COLLECTION_NAME).document(user.getUsername());
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        user.setPassword(encoder.encode(user.getPassword()));
        Map<String,Object> map = new HashMap<>();
        map.put("username",user.getUsername());
        map.put("password",user.getPassword());
        ApiFuture<WriteResult> result = docRef.set(map, SetOptions.merge());

        try {
            result.get();
        } catch (InterruptedException | ExecutionException ex) {
            return false;
        }

        return true;
    }


    public User getUser(String username) throws ExecutionException, InterruptedException {
        ApiFuture<DocumentSnapshot> future = db.getFirebase().collection(COLLECTION_NAME).document(username).get();
        DocumentSnapshot snapshot = future.get();
        return snapshot.toObject(User.class);
    }
}
