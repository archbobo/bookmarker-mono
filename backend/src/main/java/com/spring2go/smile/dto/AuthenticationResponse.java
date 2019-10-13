package com.spring2go.bookmarker.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AuthenticationResponse {
    @JsonProperty("access_token")
    private String accessToken = "";
    @JsonProperty("expires_in")
    private Long expiresIn = 0L;
}
