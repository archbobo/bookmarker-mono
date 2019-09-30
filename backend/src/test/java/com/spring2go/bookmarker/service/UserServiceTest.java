package com.spring2go.bookmarker.service;

import com.spring2go.bookmarker.constant.Constants;
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
    public void testUserServiceHappyPath() {
        CreateUserRequest createUserRequest1 = new CreateUserRequest();
        createUserRequest1.setEmail("test_user1@spring2go.com");
        createUserRequest1.setName("test_user1");
        createUserRequest1.setPassword("test_password1");
        UserDto userDto1 = userService.createUser(createUserRequest1);
        assertThat(userDto1.getEmail()).isEqualTo(createUserRequest1.getEmail());
    }

    @After
    public void destroy() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

}
