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

import java.util.UUID;

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

    @Test
    public void testUserFailurePath() {
        // 创建新用户1
        CreateUserRequest createUserRequest1 = new CreateUserRequest()
                .setName("test_user1").setPassword("test_pass1").setEmail("test_user1@spring2go.com");
        Result<UserDto> result1 = bookmarkerClient.createUser(createUserRequest1);
        assertThat(result1.isSuccess()).isTrue();
        UserDto userDto1 = result1.getData();
        assertThat(userDto1.getName()).isEqualTo(createUserRequest1.getName());
        assertThat(userDto1.getEmail()).isEqualTo(createUserRequest1.getEmail());
        assertThat(userDto1.getRoleNames()).contains(Constants.ROLE_USER);

        // 创建新用户2
        CreateUserRequest createUserRequest2 = new CreateUserRequest()
                .setName("test_user2").setPassword("test_pass2").setEmail("test_user2@spring2go.com");
        Result<UserDto> result2 = bookmarkerClient.createUser(createUserRequest2);
        assertThat(result2.isSuccess()).isTrue();
        UserDto userDto2 = result2.getData();
        assertThat(userDto2.getName()).isEqualTo(createUserRequest2.getName());
        assertThat(userDto2.getEmail()).isEqualTo(createUserRequest2.getEmail());
        assertThat(userDto2.getRoleNames()).contains(Constants.ROLE_USER);

        // 查找不存在用户
        Result<UserDto> result3 = bookmarkerClient.getUser(UUID.randomUUID().toString());
        assertThat(result3.getCode()).isEqualTo(ResultCode.NOT_FOUND);

        // 用户1登录
        login("test_user1@spring2go.com", "test_pass1");

        // 用户1不能修改用户2的数据
        userDto2.setName("update_user2");
        Result<UserDto> result4 = bookmarkerClient.updateUser(userDto2.getId(), userDto2);
        assertThat(result4.getCode()).isEqualTo(ResultCode.UN_AUTHORIZED);

        // 删除不存在的用户
        Result result5 =  bookmarkerClient.deleteUser(UUID.randomUUID().toString());
        assertThat(result5.getCode()).isEqualTo(ResultCode.NOT_FOUND);

        // 用户1不能删除用户2
        Result result6 = bookmarkerClient.deleteUser(userDto2.getId());
        assertThat(result6.getCode()).isEqualTo(ResultCode.UN_AUTHORIZED);
    }

    @After
    public void destroy() {
        logout();
        roleRepository.deleteAll();
        userRepository.deleteAll();
    }
}
