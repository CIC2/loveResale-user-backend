package com.resale.resaleuser.components.userManagement.services;

import com.resale.resaleuser.components.auth.dto.UserResponseDTO;
import com.resale.resaleuser.components.userManagement.dto.userProfile.UpdateUserDTO;
import com.resale.resaleuser.components.userManagement.UserAssignmentService;
import com.resale.resaleuser.components.userManagement.UserFetcher;
import com.resale.resaleuser.components.userManagement.UserSaver;
import com.resale.resaleuser.components.userManagement.mappers.UserResponseAssembler;
import com.resale.resaleuser.components.userManagement.updaters.AdminUserLanguageUpdater;
import com.resale.resaleuser.components.userManagement.updaters.AdminUserPermissionUpdater;
import com.resale.resaleuser.components.userManagement.updaters.AdminUserProfileUpdater;
import com.resale.resaleuser.components.userManagement.updaters.AdminUserProjectUpdater;
import com.resale.resaleuser.model.User;
import com.resale.resaleuser.shared.UserValidator;
import com.resale.resaleuser.utils.MessageUtil;
import com.resale.resaleuser.utils.ReturnObject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminUserUpdateService {

    private final UserFetcher userFetcher;
    private final UserSaver userSaver;
    private final UserValidator userValidator;
    private final AdminUserProfileUpdater profileUpdater;
    private final AdminUserPermissionUpdater permissionUpdater;
    private final AdminUserLanguageUpdater languageUpdater;
    private final AdminUserProjectUpdater projectUpdater;
    private final UserAssignmentService assignmentService;
    private final UserResponseAssembler responseAssembler;
    private final MessageUtil messageUtil;

    @Transactional
    public ReturnObject<UserResponseDTO> updateUser(Integer userId, UpdateUserDTO dto) {

        Optional<User> userOpt = userFetcher.findUser(userId);
        if (userOpt.isEmpty()) {
            return new ReturnObject<>(
                    messageUtil.getMessage("user.not.found"),
                    false,
                    null
            );
        }

        User user = userOpt.get();

        String validationError = userValidator.validateUpdateUser(dto, user);
        if (validationError != null) {
            return new ReturnObject<>(validationError, false, null);
        }

        profileUpdater.update(user, dto);
        permissionUpdater.update(user, dto);
        languageUpdater.update(user, dto);
        projectUpdater.update(user, dto);

        ReturnObject<Void> assignmentResult = assignmentService.handleTeamLeadAssignments(user, dto);
        if (!assignmentResult.getStatus()) {
            return new ReturnObject<>(assignmentResult.getMessage(), false, null);
        }

        User savedUser = userSaver.save(user);

        UserResponseDTO response =
                responseAssembler.build(savedUser);

        return new ReturnObject<>(
                messageUtil.getMessage("user.updated.successfully"),
                true,
                response
        );
    }
}


