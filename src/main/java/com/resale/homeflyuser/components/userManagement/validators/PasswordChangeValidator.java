package com.resale.homeflyuser.components.userManagement.validators;

import com.resale.homeflyuser.components.userManagement.dto.ChangePasswordDTO;
import com.resale.homeflyuser.model.User;
import com.resale.homeflyuser.utils.MessageUtil;
import com.resale.homeflyuser.utils.ReturnObject;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PasswordChangeValidator {

    private final PasswordEncoder passwordEncoder;
    private final MessageUtil messageUtil;

    public ReturnObject<Void> validate(User user, ChangePasswordDTO dto) {

        // Old password incorrect
        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            return new ReturnObject<>(
                    messageUtil.getMessage("password.old.incorrect"),
                    false,
                    null
            );
        }

        // New password same as old
        if (passwordEncoder.matches(dto.getNewPassword(), user.getPassword())) {
            return new ReturnObject<>(
                    messageUtil.getMessage("password.same.as.old"),
                    false,
                    null
            );
        }

        // New & confirm mismatch
        if (!dto.getNewPassword().equals(dto.getConfirmNewPassword())) {
            return new ReturnObject<>(
                    messageUtil.getMessage("password.mismatch"),
                    false,
                    null
            );
        }

        return new ReturnObject<>("", true, null);
    }
}


