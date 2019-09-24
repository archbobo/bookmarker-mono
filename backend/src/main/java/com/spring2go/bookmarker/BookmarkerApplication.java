package com.spring2go.bookmarker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing
public class BookmarkerApplication {
    public static void main(String[] args) {
        SpringApplication.run(BookmarkerApplication.class, args);
    }
}
