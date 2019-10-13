package com.spring2go.smile.config;

import com.spring2go.smile.constant.Constants;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "smile")
@Data
public class BookmarkerProperties {
    private JwtConfig jwt = new JwtConfig();

    @Data
    public static class JwtConfig {
        private String issuer = "spring2go";
        private String header = "Authorization";
        private Long expiresIn = Constants.DEFAULT_JWT_TOKEN_EXPIRES;
        private String secret = "";
    }
}
