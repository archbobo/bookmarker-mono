package com.spring2go.bookmarker.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

@Data
@Accessors(chain = true)
public class ChangePasswordRequest {
    @NotBlank(message = "Old password cannot be blank")
    private String oldPassword;
    @NotBlank(message = "New password cannot be blank")
    private String newPassword;
}
