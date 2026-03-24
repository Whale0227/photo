package com.photo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class PhotoApplication {

    public static void main(String[] args) {
        SpringApplication.run(PhotoApplication.class, args);
    }

}
