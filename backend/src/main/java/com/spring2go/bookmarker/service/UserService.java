package com.spring2go.bookmarker.service;

import com.spring2go.bookmarker.common.api.ResultCode;
import com.spring2go.bookmarker.common.exception.ServiceException;
import com.spring2go.bookmarker.constant.Constants;
import com.spring2go.bookmarker.dto.ChangePasswordRequest;
import com.spring2go.bookmarker.dto.CreateUserRequest;
import com.spring2go.bookmarker.dto.UserDto;
import com.spring2go.bookmarker.model.Role;
import com.spring2go.bookmarker.model.User;
import com.spring2go.bookmarker.repository.RoleRepository;
import com.spring2go.bookmarker.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public UserDto getUserById(String id) {
        User user = userRepository.findById(id);
        if (user == null) return null;
        return modelMapper.map(user, UserDto.class);
    }

    @Transactional(readOnly = true)
    public UserDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) return null;
        return modelMapper.map(user, UserDto.class);
    }

    public UserDto createUser(CreateUserRequest request) {
        if (userRepository.checkExistsByEmail(request.getEmail())) {
            throw new ServiceException(ResultCode.EMAIL_USED, "这个电子邮件地址已经被使用 " + request.getEmail());
        }
        User newUser = modelMapper.map(request, User.class);
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        Role defaultRole = roleRepository.findByName(Constants.ROLE_USER);
        newUser.getRoleNames().add(defaultRole.getName());
        User createdUser = userRepository.create(newUser);
        return modelMapper.map(createdUser, UserDto.class);
    }

    public UserDto updateUser(UserDto userDto) {
        User existingUser = userRepository.findById(userDto.getId());
        if (existingUser == null) {
            throw new ServiceException(ResultCode.NOT_FOUND, "未找到这个id的用户 " + userDto.getId());
        }
        User user2Update = modelMapper.map(userDto, User.class);
        user2Update.setPassword(existingUser.getPassword());
        user2Update.setRoleNames(existingUser.getRoleNames());
        User updatedUser = userRepository.update(user2Update);
        return modelMapper.map(updatedUser, UserDto.class);
    }

    public void deleteUser(String userId) {
        userRepository.deleteById(userId);
    }

    public UserDto changePassword(String email, ChangePasswordRequest changePasswordRequest) {
        User existingUser = userRepository.findByEmail(email);
        if (existingUser == null) {
            throw new ServiceException(ResultCode.EMAIL_USED, "这个电子邮件地址已经被使用 " + email);
        }
        if (!passwordEncoder.matches(changePasswordRequest.getOldPassword(), existingUser.getPassword())) {
            throw new ServiceException(ResultCode.UN_AUTHORIZED, "旧密码不匹配");
        }
        existingUser.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        User updatedUser = userRepository.update(existingUser);
        return modelMapper.map(updatedUser, UserDto.class);
    }

}
