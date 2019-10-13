package com.spring2go.bookmarker;

import com.spring2go.bookmarker.client.BookmarkerClient;
import com.spring2go.bookmarker.common.api.Result;
import com.spring2go.bookmarker.dto.AuthenticationRequest;
import com.spring2go.bookmarker.dto.AuthenticationResponse;
import com.spring2go.bookmarker.repository.RoleRepository;
import com.spring2go.bookmarker.repository.UserRepository;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@EnableFeignClients(basePackages = "com.spring2go.bookmarker.client")
@Import(TestConfig.class)
@ActiveProfiles("test")
public abstract class AbstractControllerTest {
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    protected BookmarkerClient bookmarkerClient;

    @Autowired
    protected RoleRepository roleRepository;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected PasswordEncoder passwordEncoder;


    protected void login(String username, String password) {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest();
        authenticationRequest.setUsername(username).setPassword(password);
        Result<AuthenticationResponse> result = bookmarkerClient.createAuthenticationToken(authenticationRequest);
        assertThat(result.isSuccess()).isTrue();
        AuthenticationResponse authenticationResponse = result.getData();
        assertThat(authenticationResponse.getAccessToken()).isNotBlank();
        assertThat(authenticationResponse.getExpiresIn()).isGreaterThan(0L);

        // 设置访问令牌HTTP头
        TestConfig.TEST_TOKEN = authenticationResponse.getAccessToken();
    }

    protected void logout() {
        // 清除访问令牌HTTP头
        TestConfig.TEST_TOKEN = "";
    }
}
