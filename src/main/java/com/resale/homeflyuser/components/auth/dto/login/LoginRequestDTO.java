package com.resale.homeflyuser.components.auth.dto.login;

import com.resale.homeflyuser.model.LoginSource;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestDTO {
    private String email;
    private String password;
    private LoginSource source;
}


