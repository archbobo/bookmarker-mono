package com.spring2go.bookmarker.controller;

import com.spring2go.bookmarker.annotation.Loggable;
import com.spring2go.bookmarker.dto.ChangePasswordRequest;
import com.spring2go.bookmarker.dto.CreateUserRequest;
import com.spring2go.bookmarker.dto.UserDto;
import com.spring2go.bookmarker.exception.BadRequestException;
import com.spring2go.bookmarker.exception.UserNotFoundException;
import com.spring2go.bookmarker.service.UserService;
import com.spring2go.bookmarker.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("api/users")
@Loggable
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable String id) {
        log.info("process=get_user, user_id" + id);
        UserDto userDto = userService.getUserById(id);
        if (userDto == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@RequestBody @Valid CreateUserRequest createUserRequest) {
        log.info("process=create_user, user_email=" + createUserRequest.getEmail());
        UserDto userDto = new UserDto()
                .setName(createUserRequest.getName())
                .setEmail(createUserRequest.getEmail())
                .setPassword(createUserRequest.getPassword());
        return userService.createUser(userDto);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PutMapping("/{id}")
    public UserDto updateUser(@PathVariable String id, @RequestBody @Valid UserDto user) {
        log.info("process=update_user, user_id" + id);
        if (!StringUtils.equals(id, SecurityUtils.loginUser().getId()) && !SecurityUtils.isCurrentAdmin()) {
            throw new BadRequestException("你不能修改其他用户数据");
        }
        user.setId(id);
        return userService.updateUser(user);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable String id) {
        log.info("process=delete_user, user_id" + id);
        UserDto userDto = userService.getUserById(id);
        if (userDto == null ||
                (!StringUtils.equals(userDto.getId(), SecurityUtils.loginUser().getId()) && !SecurityUtils.isCurrentAdmin())) {
            throw new UserNotFoundException("User not found with id=" + id);
        }
        userService.deleteUser(id);
    }

    @PostMapping("/change-password")
    @PreAuthorize("hasRole('ROLE_USER')")
    public void changePassword(@RequestBody @Valid ChangePasswordRequest changePasswordRequest) {
        Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
        String email = currentUser.getName();
        log.info("process=change_password, email=" + email);
        userService.changePassword(email, changePasswordRequest);
    }
}
