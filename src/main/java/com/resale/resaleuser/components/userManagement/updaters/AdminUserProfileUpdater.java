package com.resale.resaleuser.components.userManagement.updaters;

import com.resale.resaleuser.components.userManagement.dto.userProfile.UpdateUserDTO;
import com.resale.resaleuser.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminUserProfileUpdater {

    private final PasswordEncoder passwordEncoder;


    public void update(User user, UpdateUserDTO dto) {

        if (dto.getFullName() != null) {
            user.setFullName(dto.getFullName());
        }

        if (dto.getMobile() != null) {
            user.setMobile(dto.getMobile());
        }

        if (dto.getRole() != null) {
            user.setRole(dto.getRole());
        }

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

    }
}


