package com.spring2go.bookmarker.model;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.util.Date;

@Data
public abstract class BaseModel {
    @CreatedDate
    private Date createdAt;

    @LastModifiedDate
    private Date updatedAt;
}
