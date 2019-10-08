package com.spring2go.bookmarker.controller;

import com.spring2go.bookmarker.AbstractControllerTest;
import com.spring2go.bookmarker.common.api.Result;
import com.spring2go.bookmarker.common.api.ResultCode;
import com.spring2go.bookmarker.constant.Constants;
import com.spring2go.bookmarker.dto.*;
import com.spring2go.bookmarker.model.Role;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UserControllerTest extends AbstractControllerTest {

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
    public void testUserHappyPath() {
        // 创建新用户
        CreateUserRequest createUserRequest = new CreateUserRequest()
                .setName("test_user").setPassword("test_pass").setEmail("test_user@spring2go.com");
        Result<UserDto> result = bookmarkerClient.createUser(createUserRequest);
        assertThat(result.isSuccess()).isTrue();
        UserDto userDto = result.getData();
        assertThat(userDto.getName()).isEqualTo(createUserRequest.getName());
        assertThat(userDto.getEmail()).isEqualTo(createUserRequest.getEmail());
        assertThat(userDto.getRoleNames()).contains(Constants.ROLE_USER);

        // 通过Id查找用户
        result = bookmarkerClient.getUser(userDto.getId());
        assertThat(result.isSuccess()).isTrue();
        UserDto foundUserDto = result.getData();
        assertThat(foundUserDto).isEqualTo(userDto);

        // 登录
        login(createUserRequest.getEmail(), createUserRequest.getPassword());

        // 更新用户
        userDto.setName("updated_user");
        result = bookmarkerClient.updateUser(userDto.getId(), userDto);
        assertThat(result.isSuccess()).isTrue();
        UserDto updatedUserDto = result.getData();
        assertThat(updatedUserDto).isEqualTo(userDto);

        // 更新用户密码
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
        changePasswordRequest.setOldPassword(createUserRequest.getPassword()).setNewPassword("new_pass");
        result = bookmarkerClient.changePassword(changePasswordRequest);
        assertThat(result.isSuccess()).isTrue();
        UserDto passwordChangedUserDto = result.getData();
        assertThat(passwordChangedUserDto).isEqualTo(updatedUserDto);

        // 使用新密码登录
        login(createUserRequest.getEmail(), changePasswordRequest.getNewPassword());

        // 删除该用户
        Result deleteResult = bookmarkerClient.deleteUser(userDto.getId());
        assertThat(deleteResult.isSuccess()).isTrue();

        logout();
        // 校验用户未找到
        result = bookmarkerClient.getUser(userDto.getId());
        assertThat(result.getCode()).isEqualTo(ResultCode.NOT_FOUND);

    }

    @After
    public void destroy() {
        logout();
        roleRepository.deleteAll();
        userRepository.deleteAll();
    }
}
