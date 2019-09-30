package com.spring2go.bookmarker.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

@Data
@Accessors(chain = true)
public class AuthenticationRequest {
    @NotBlank(message = "用户名不能为空")
    private String username = "";
    @NotBlank(message = "密码不能为空")
    private String password = "";
}
