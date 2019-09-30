package com.spring2go.bookmarker.config;

import com.spring2go.bookmarker.constant.Constants;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "bookmarker")
@Data
public class BookmarkerProperties {
    private JwtConfig jwt = new JwtConfig();

    @Data
    public static class JwtConfig {
        private String issuer = "bookmarker";
        private String header = "Authorization";
        private Long expiresIn = Constants.DEFAULT_JWT_TOKEN_EXPIRES;
        private String secret = "";
    }
}
