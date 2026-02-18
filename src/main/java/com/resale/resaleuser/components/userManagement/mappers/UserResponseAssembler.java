package com.resale.resaleuser.components.userManagement.mappers;

import com.resale.resaleuser.components.auth.dto.UserResponseDTO;
import com.resale.resaleuser.components.userManagement.resolvers.PermissionResolver;
import com.resale.resaleuser.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

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


