package com.resale.resaleuser.components.userManagement.updaters;

import com.resale.resaleuser.components.userManagement.dto.userProfile.UpdateUserDTO;
import com.resale.resaleuser.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserUpdater {

    @Autowired
    PasswordEncoder passwordEncoder;

    public void applyUpdates(User user, UpdateUserDTO dto) {

        if (dto.getFullName() != null) {
            user.setFullName(dto.getFullName());
        }

        if (dto.getMobile() != null) {
            user.setMobile(dto.getMobile());
        }

        if (dto.getNewPassword() != null) {
            user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        }
    }
}


