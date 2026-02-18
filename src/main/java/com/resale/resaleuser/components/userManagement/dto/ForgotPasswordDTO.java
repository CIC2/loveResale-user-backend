package com.resale.resaleuser.components.userManagement.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ForgotPasswordDTO {

    @NotBlank(message = "{email.required}")
    @Email(message = "{email.invalid}")
    private String email;
    @NotBlank(message = "{otp.required}")
    @Size(min = 4, max = 6, message = "{otp.size}")
    private String otp;

    @NotBlank(message = "{password.required}")
    @Size(min = 8, message = "{password.min.length}")
    private String newPassword;
}


