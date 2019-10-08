package com.spring2go.bookmarker.service;

import com.spring2go.bookmarker.common.api.ResultCode;
import com.spring2go.bookmarker.common.exception.ServiceException;
import com.spring2go.bookmarker.constant.Constants;
import com.spring2go.bookmarker.dto.ChangePasswordRequest;
import com.spring2go.bookmarker.dto.CreateUserRequest;
import com.spring2go.bookmarker.dto.UserDto;
import com.spring2go.bookmarker.model.Role;
import com.spring2go.bookmarker.repository.RoleRepository;
import com.spring2go.bookmarker.repository.UserRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.NONE)
@RunWith(SpringRunner.class)
public class UserServiceTest {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Before
    public void setUp() {
        userRepository.deleteAll();

        // 添加缺省Role
        Role defaultRole = new Role().setName(Constants.ROLE_USER);
        roleRepository.insert(defaultRole);
    }


    @Test
    public void testUserServiceFailurePath() {
        // 创建
        CreateUserRequest createUserRequest1 = new CreateUserRequest();
        createUserRequest1.setEmail("test_user1@spring2go.com");
        createUserRequest1.setName("test_user1");
        createUserRequest1.setPassword("test_password1");
        UserDto userDto = userService.createUser(createUserRequest1);
        assertThat(userDto.getEmail()).isEqualTo(createUserRequest1.getEmail());

        // 再次创建, email已经存在
        CreateUserRequest createUserRequest2 = new CreateUserRequest();
        createUserRequest2.setEmail("test_user1@spring2go.com");
        createUserRequest2.setName("test_user11");
        createUserRequest2.setPassword("test_password11");
        try {
            UserDto userDto2 = userService.createUser(createUserRequest1);
        } catch (ServiceException se) {
            assertThat(se.getResultCode()).isEqualTo(ResultCode.EMAIL_USED);
        }

        // 更新未找到用户
        userDto.setId(UUID.randomUUID().toString());
        try {
            userService.updateUser(userDto);
        } catch (ServiceException se) {
            assertThat(se.getResultCode()).isEqualTo(ResultCode.NOT_FOUND);
        }

        // 更新密码, 邮件不存在
        ChangePasswordRequest request = new ChangePasswordRequest().setOldPassword(createUserRequest1.getPassword())
                .setNewPassword("test_password1_new");
        try {
            userService.changePassword("test_user123@spring2go.com", request);
        } catch (ServiceException se) {
            assertThat(se.getResultCode()).isEqualTo(ResultCode.NOT_FOUND);
        }

        // 更新密码, 旧密码不匹配
        request = new ChangePasswordRequest().setOldPassword("test_password123")
                .setNewPassword("test_password1_new");
        try {
            userService.changePassword(userDto.getEmail(), request);
        } catch (ServiceException se) {
            assertThat(se.getResultCode()).isEqualTo(ResultCode.UN_AUTHORIZED);
        }
    }

    @Test
    public void testUserServiceHappyPath() {
        // 创建
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setEmail("test_user1@spring2go.com");
        createUserRequest.setName("test_user1");
        createUserRequest.setPassword("test_password1");
        UserDto userDto = userService.createUser(createUserRequest);
        assertThat(userDto.getEmail()).isEqualTo(createUserRequest.getEmail());

        // 通过Id查询
        UserDto foundUserDto = userService.getUserById(userDto.getId());
        assertThat(foundUserDto).isEqualTo(userDto);

        // 通过Email查询
        foundUserDto = userService.getUserByEmail(userDto.getEmail());
        assertThat(foundUserDto).isEqualTo(userDto);

        // 更新
        userDto.setEmail("test_user1_update@spring2go.com");
        userDto.setName("test_user1_update");
        UserDto updatedUserDto = userService.updateUser(userDto);
        assertThat(updatedUserDto).isEqualTo(userDto);

        // 更新密码
        ChangePasswordRequest request = new ChangePasswordRequest().setOldPassword(createUserRequest.getPassword())
                .setNewPassword("test_password1_new");
        updatedUserDto = userService.changePassword(userDto.getEmail(), request);
        assertThat(updatedUserDto).isEqualTo(userDto);
    }

    @After
    public void destroy() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

}
