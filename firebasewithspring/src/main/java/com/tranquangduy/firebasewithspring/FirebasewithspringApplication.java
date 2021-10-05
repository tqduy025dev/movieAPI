package com.tranquangduy.firebasewithspring;

import java.io.FileNotFoundException;
import java.util.Collections;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FirebasewithspringApplication {

    public static void main(String[] args) throws FileNotFoundException {
        SpringApplication.run(FirebasewithspringApplication.class, args);
        
//        SpringApplication app = new SpringApplication(FirebasewithspringApplication.class);
//        app.setDefaultProperties(Collections.singletonMap("server.port", "8083"));
//        app.run(args);
    }
}
