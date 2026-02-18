package com.resale.resaleuser.components.userManagement.mappers;

import com.resale.resaleuser.components.auth.dto.PermissionDTO;
import com.resale.resaleuser.components.auth.dto.UserResponseDTO;
import com.resale.resaleuser.components.language.dto.LanguageDTO;
import com.resale.resaleuser.components.userInternal.dto.ProjectDTO;
import com.resale.resaleuser.model.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class UserResponseMapper {

    public UserResponseDTO map(
            User user,
            Set<PermissionDTO> permissions,
            Set<LanguageDTO> languages,
            List<ProjectDTO> projects,
            boolean onCall
    ) {

        return new UserResponseDTO(
                "",
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getMobile(),
                user.getIsActive(),
                user.getIsVerified(),
                user.getStatus(),
                user.getRole(),
                permissions,
                projects,
                languages,
                user.getFcmToken(),
                user.getUserId(),
                onCall
        );
    }
}


