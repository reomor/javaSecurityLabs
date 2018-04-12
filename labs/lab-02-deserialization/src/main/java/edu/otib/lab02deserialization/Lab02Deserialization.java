package edu.otib.lab02deserialization;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Lab02Deserialization {

    public static void main(String[] args) {
        System.getProperties().setProperty("org.apache.commons.collections.enableUnsafeSerialization", "true");
        SpringApplication.run(Lab02Deserialization.class, args);
    }
}
