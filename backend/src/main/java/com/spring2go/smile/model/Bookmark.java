package com.spring2go.bookmarker.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

@Data
@Document
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Accessors(chain = true)
public class Bookmark extends BaseModel {
    @Id
    private String id;
    private String url;
    private String title;
    @Indexed
    private String createdBy;
    private Set<String> tags = new HashSet<>();
}
