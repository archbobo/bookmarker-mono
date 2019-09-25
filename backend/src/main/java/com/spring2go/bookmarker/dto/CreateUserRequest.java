package com.spring2go.bookmarker.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Accessors(chain = true)
public class CreateUserRequest {
    @NotBlank(message = "用户名不能为空")
    private String name;
    @NotBlank(message = "邮件地址不能为空")
    @Email(message = "邮件地址无效")
    private String email;
    @NotBlank(message = "密码不能为空")
    private String password;
}
