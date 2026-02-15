package com.resale.homeflyuser.components.auth.dto.login;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDTO {
    private String token;
    private Integer id;
    private String email;
    private String fullName;
    private String role;
    private Boolean isVerified;
    private List<String> permissions;
}


