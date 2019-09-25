package com.spring2go.bookmarker.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Accessors(chain = true)
public class BookmarkDto {
    private String id;
    @NotBlank(message = "URL不能为空")
    private String url;
    private String title;
    @JsonProperty("created_user_id")
    private String createdUserId;
    @JsonProperty("created_user_name")
    private String createdUserName;
    @JsonProperty("created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
    private Set<String> tags = new HashSet<>();
}
