package com.resale.homeflyuser.components.userManagement.services;

import com.resale.homeflyuser.components.userManagement.dto.userProfile.UpdateUserDTO;
import com.resale.homeflyuser.components.userManagement.UserFetcher;
import com.resale.homeflyuser.components.userManagement.UserSaver;
import com.resale.homeflyuser.components.userManagement.updaters.UserUpdater;
import com.resale.homeflyuser.components.userManagement.validators.UserUpdateValidator;
import com.resale.homeflyuser.model.User;
import com.resale.homeflyuser.utils.MessageUtil;
import com.resale.homeflyuser.utils.ReturnObject;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserUpdateService {

    private final UserFetcher userFetcher;
    private final UserSaver userSaver;
    private final UserUpdateValidator userUpdateValidator;
    private final UserUpdater userUpdater;
    private final MessageUtil messageUtil;
    private final PasswordEncoder passwordEncoder;

    public ReturnObject<UpdateUserDTO> updateUserPersonalInfo(
            Integer id,
            UpdateUserDTO dto
    ) {

        Optional<User> userOptional = userFetcher.findUser(id);

        if (userOptional.isEmpty()) {
            return new ReturnObject<>(
                    messageUtil.getMessage("cant.found.user"),
                    false,
                    null
            );
        }

        User user = userOptional.get();

        ReturnObject<Void> validationResult =
                userUpdateValidator.validate(dto);

        if (!validationResult.getStatus()) {
            return new ReturnObject<>(
                    validationResult.getMessage(),
                    false,
                    null
            );
        }
        if(dto.getPassword() != null) {
            if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
                return new ReturnObject<>(
                        messageUtil.getMessage("old.password.incorrect"),
                        false,
                        null
                );

            }
        }
        userUpdater.applyUpdates(user, dto);
        userSaver.save(user);

        return new ReturnObject<>(
                messageUtil.getMessage("user.updated.successfully"),
                true,
                dto
        );
    }
}


