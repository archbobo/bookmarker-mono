package com.spring2go.bookmarker.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Data
@Document
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Accessors(chain = true)
public class User extends BaseModel {
    @Id
    private String id;
    @Indexed(name = "user_email_unique", unique = true)
    private String email;
    private String password;
    private String name;
    private Set<String> roleNames;
}
