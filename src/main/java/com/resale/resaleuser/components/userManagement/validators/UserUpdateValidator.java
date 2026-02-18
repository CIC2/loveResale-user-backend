package com.resale.resaleuser.components.userManagement.validators;

import com.resale.resaleuser.components.userManagement.dto.userProfile.UpdateUserDTO;
import com.resale.resaleuser.utils.MessageUtil;
import com.resale.resaleuser.utils.ReturnObject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserUpdateValidator {

    private final MessageUtil messageUtil;

    public ReturnObject<Void> validate(UpdateUserDTO dto) {

        if (dto.getPassword() != null || dto.getNewPassword() != null) {

            if (dto.getPassword() == null || dto.getNewPassword() == null) {
                return new ReturnObject<>(
                        messageUtil.getMessage("user.passwords.mismatch"),
                        false,
                        null
                );
            }

            if (dto.getPassword().equals(dto.getNewPassword())) {
                return new ReturnObject<>(
                        messageUtil.getMessage("user.passwords.didnt.change"),
                        false,
                        null
                );
            }
        }

        return new ReturnObject<>("", true, null);
    }
}


