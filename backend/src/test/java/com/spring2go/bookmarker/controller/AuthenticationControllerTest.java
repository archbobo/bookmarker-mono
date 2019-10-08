package com.spring2go.bookmarker.controller;

import com.spring2go.bookmarker.AbstractControllerTest;
import com.spring2go.bookmarker.common.api.Result;
import com.spring2go.bookmarker.config.BookmarkerProperties;
import com.spring2go.bookmarker.constant.Constants;
import com.spring2go.bookmarker.dto.AuthenticationResponse;
import com.spring2go.bookmarker.dto.CreateUserRequest;
import com.spring2go.bookmarker.dto.UserDto;
import com.spring2go.bookmarker.model.Role;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

public class AuthenticationControllerTest extends AbstractControllerTest {

    @Autowired
    private BookmarkerProperties bookmarkerProperties;

    @Before
    public void setUp() {
        // 预设置缺省角色
        Role userRole = new Role().setName(Constants.ROLE_USER);
        roleRepository.insert(userRole);

        Role adminRole = new Role().setName(Constants.ROLE_ADMIN);
        roleRepository.insert(adminRole);

        // 清除之前登录
        logout();
    }

    @Test
    public void testAuth() {
        // 创建新用户
        CreateUserRequest createUserRequest = new CreateUserRequest()
                .setName("test_user").setPassword("test_pass").setEmail("test_user@spring2go.com");
        Result<UserDto> result = bookmarkerClient.createUser(createUserRequest);
        assertThat(result.isSuccess()).isTrue();
        UserDto userDto = result.getData();
        assertThat(userDto.getName()).isEqualTo(createUserRequest.getName());
        assertThat(userDto.getEmail()).isEqualTo(createUserRequest.getEmail());
        assertThat(userDto.getRoleNames()).contains(Constants.ROLE_USER);

        // 登录
        login(createUserRequest.getEmail(), createUserRequest.getPassword());

        // 获取刷新令牌
        Result<AuthenticationResponse> refreshTokenResult = bookmarkerClient.refreshAuthenticationToken();
        assertThat(refreshTokenResult.isSuccess()).isTrue();
        AuthenticationResponse authenticationResponse = refreshTokenResult.getData();
        assertThat(authenticationResponse.getAccessToken()).isNotBlank();
        assertThat(authenticationResponse.getExpiresIn()).isEqualTo(bookmarkerProperties.getJwt().getExpiresIn());

        // 获取登录者用户信息
        Result<UserDto> meResult = bookmarkerClient.me();
        assertThat(meResult.isSuccess()).isTrue();
        assertThat(meResult.getData()).isEqualTo(userDto);
    }

    @After
    public void destroy() {
        logout();
        roleRepository.deleteAll();
        userRepository.deleteAll();
    }
}
