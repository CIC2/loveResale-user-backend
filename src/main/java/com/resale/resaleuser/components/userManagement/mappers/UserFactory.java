package com.resale.resaleuser.components.userManagement.mappers;

import com.resale.resaleuser.components.userManagement.dto.userProfile.CreateUserDTO;
import com.resale.resaleuser.model.Role;
import com.resale.resaleuser.model.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserFactory {

    public UserFactory(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
    private final PasswordEncoder passwordEncoder;

    public User build(CreateUserDTO dto) {
        User user = new User();
        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setMobile(dto.getMobile());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(dto.getRole());
        user.setIsActive(true);
        user.setIsVerified(true);
        user.setStatus(1);

        if (dto.getAssignToUserId() != null && dto.getRole() == Role.SALESMAN) {
            user.setUserId(dto.getAssignToUserId());
        }

        return user;
    }
}


