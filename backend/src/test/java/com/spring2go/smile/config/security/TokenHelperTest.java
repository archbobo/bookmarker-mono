package com.spring2go.bookmarker.config.security;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
public class TokenHelperTest {
    @Autowired
    private TokenHelper tokenHelper;

    @Test
    public void testTokenHelper() {
        String token = tokenHelper.generateToken("bobo@spring2go.com");
        assertThat(token).isNotNull();
        String username = tokenHelper.getUsernameFromToken(token);
        assertThat(username).isEqualTo("bobo@spring2go.com");
    }
}
