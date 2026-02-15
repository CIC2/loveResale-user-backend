package com.resale.homeflyuser.components.userManagement.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendOtpDTO {

    @NotBlank(message = "{email.required}")
    @Email(message = "{email.invalid}")
    private String email;
}


