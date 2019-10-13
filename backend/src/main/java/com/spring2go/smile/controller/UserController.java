package com.spring2go.bookmarker.controller;

import com.spring2go.bookmarker.common.api.Result;
import com.spring2go.bookmarker.common.api.ResultCode;
import com.spring2go.bookmarker.common.exception.ServiceException;
import com.spring2go.bookmarker.dto.ChangePasswordRequest;
import com.spring2go.bookmarker.dto.CreateUserRequest;
import com.spring2go.bookmarker.dto.UserDto;
import com.spring2go.bookmarker.service.UserService;
import com.spring2go.bookmarker.common.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("api/users")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public Result<UserDto> getUser(@PathVariable String id) {
        log.info("process=get_user, user_id = " + id);
        UserDto userDto = userService.getUserById(id);
        if (userDto == null) return Result.fail(ResultCode.NOT_FOUND, "未找到指定id的用户, id = " + id);
        return Result.success(userDto);
    }

    @PostMapping("")
    public Result<UserDto> createUser(@RequestBody @Valid CreateUserRequest createUserRequest) {
        log.info("process=create_user, user_email = " + createUserRequest.getEmail());
        UserDto userDto = userService.createUser(createUserRequest);
        return Result.success(userDto);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PutMapping("/{id}")
    public Result<UserDto> updateUser(@PathVariable String id, @RequestBody @Valid UserDto user) {
        log.info("process=update_user, user_id = " + id);
        if (!StringUtils.equals(id, SecurityUtils.loginUser().getId()) && !SecurityUtils.isCurrentAdmin()) {
            throw new ServiceException(ResultCode.UN_AUTHORIZED, "你不能修改其他用户数据");
        }
        user.setId(id);
        UserDto updatedUserDto = userService.updateUser(user);
        return Result.success(updatedUserDto);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @DeleteMapping("/{id}")
    public Result deleteUser(@PathVariable String id) {
        log.info("process=delete_user, user_id = " + id);
        UserDto userDto = userService.getUserById(id);
        if (userDto == null) {
            throw new ServiceException(ResultCode.NOT_FOUND, "未找到用户, id=" + id);
        }
        if (!StringUtils.equals(userDto.getId(), SecurityUtils.loginUser().getId()) && !SecurityUtils.isCurrentAdmin()) {
            throw new ServiceException(ResultCode.UN_AUTHORIZED, "你不能修改其他用户数据");
        }
        userService.deleteUser(id);
        return Result.success();
    }

    @PostMapping("/change-password")
    @PreAuthorize("hasRole('ROLE_USER')")
    public Result<UserDto> changePassword(@RequestBody @Valid ChangePasswordRequest changePasswordRequest) {
        Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
        String email = currentUser.getName();
        log.info("process=change_password, email=" + email);
        UserDto updatedUserDto = userService.changePassword(email, changePasswordRequest);
        return Result.success(updatedUserDto);
    }
}
