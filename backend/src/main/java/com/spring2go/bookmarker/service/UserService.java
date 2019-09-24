package com.spring2go.bookmarker.service;

import com.spring2go.bookmarker.constant.Constants;
import com.spring2go.bookmarker.dto.ChangePasswordRequest;
import com.spring2go.bookmarker.dto.UserDto;
import com.spring2go.bookmarker.exception.BookmarkException;
import com.spring2go.bookmarker.exception.UserNotFoundException;
import com.spring2go.bookmarker.model.Role;
import com.spring2go.bookmarker.model.User;
import com.spring2go.bookmarker.repository.RoleRepository;
import com.spring2go.bookmarker.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
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

    public UserDto createUser(UserDto userDto) {
        if (userRepository.checkExistsByEmail(userDto.getEmail())) {
            throw new BookmarkException("这个电子邮件地址已经被使用 " + userDto.getEmail());
        }
        User newUser = modelMapper.map(userDto, User.class);
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        Role defaultRole = roleRepository.findByName(Constants.ROLE_USER);
        newUser.getRoleIds().add(defaultRole.getId());
        User createdUser = userRepository.create(newUser);
        return modelMapper.map(createdUser, UserDto.class);
    }

    public UserDto updateUser(UserDto userDto) {
        User existingUser = userRepository.findById(userDto.getId());
        if (existingUser == null) {
            throw new UserNotFoundException("未找到这个id的用户 " + userDto.getId());
        }
        User user2Update = modelMapper.map(userDto, User.class);
        user2Update.setPassword(existingUser.getPassword());
        user2Update.setRoleIds(existingUser.getRoleIds());
        User updatedUser = userRepository.update(user2Update);
        return modelMapper.map(updatedUser, UserDto.class);
    }

    public UserDto changePassword(String email, ChangePasswordRequest changePasswordRequest) {
        User existingUser = userRepository.findByEmail(email);
        if (existingUser == null) {
            throw new BookmarkException("这个电子邮件地址已经被使用 " + email);
        }
        if (!passwordEncoder.matches(changePasswordRequest.getOldPassword(), existingUser.getPassword())) {
            throw new UserNotFoundException("老密码不匹配");
        }
        existingUser.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        User updatedUser = userRepository.update(existingUser);
        return modelMapper.map(updatedUser, UserDto.class);
    }

}
