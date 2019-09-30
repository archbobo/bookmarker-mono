package com.spring2go.bookmarker;

import com.spring2go.bookmarker.config.BookmarkerProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication()
@EnableConfigurationProperties(value = {BookmarkerProperties.class})
@EnableMongoAuditing
public class BookmarkerApplication {
    public static void main(String[] args) {
        SpringApplication.run(BookmarkerApplication.class, args);
    }
}
