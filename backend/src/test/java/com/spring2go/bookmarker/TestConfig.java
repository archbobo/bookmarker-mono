package com.spring2go.bookmarker;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;

/**
 * 设置令牌HTTP头
 */
public class TestConfig {
    public static String TEST_TOKEN = "";

    @Bean
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate requestTemplate) {
                if (!StringUtils.isEmpty(TEST_TOKEN)) {
                    requestTemplate.header("Authorization", "Bearer " + TEST_TOKEN);
                }
            }
        };
    }
}
