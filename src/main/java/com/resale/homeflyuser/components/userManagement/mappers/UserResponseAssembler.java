package com.resale.homeflyuser.components.userManagement.mappers;

import com.resale.homeflyuser.components.auth.dto.UserResponseDTO;
import com.resale.homeflyuser.components.userManagement.resolvers.LanguageResolver;
import com.resale.homeflyuser.components.userManagement.resolvers.PermissionResolver;
import com.resale.homeflyuser.components.userManagement.resolvers.ProjectResolver;
import com.resale.homeflyuser.model.User;
import com.resale.homeflyuser.utils.MessageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@RequiredArgsConstructor
public class UserResponseAssembler {

    private final PermissionResolver permissionResolver;
    public UserResponseDTO build(User user) {


        return new UserResponseDTO(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getMobile(),
                user.getIsActive(),
                user.getIsVerified(),
                user.getStatus(),
                user.getRole(),
                permissionResolver.resolve(user),
                user.getUserId(),
                ""
        );
    }
}


