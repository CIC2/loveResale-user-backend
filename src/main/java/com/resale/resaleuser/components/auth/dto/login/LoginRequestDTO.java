package com.resale.resaleuser.components.auth.dto.login;

import com.resale.resaleuser.model.LoginSource;
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


