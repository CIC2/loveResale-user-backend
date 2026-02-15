package com.resale.homeflyuser.components.userManagement.updaters;

import com.resale.homeflyuser.components.auth.dto.PermissionDTO;
import com.resale.homeflyuser.components.userManagement.dto.userProfile.UserPermissionDTO;
import com.resale.homeflyuser.model.Permission;
import com.resale.homeflyuser.model.Role;
import com.resale.homeflyuser.model.User;
import com.resale.homeflyuser.model.UserPermission;
import com.resale.homeflyuser.repository.PermissionRepository;
import com.resale.homeflyuser.repository.UserPermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AdminUserPermissionUpdater {

    private final UserPermissionRepository userPermissionRepository;
    private final PermissionRepository permissionRepository;

    public void update(User user, UserPermissionDTO dto) {

        List<Integer> permissionIds = dto.getPermissionIds();
        if (permissionIds == null || permissionIds.isEmpty()) {
            return ;
        }

        if (user.getRole() != Role.SALESMAN
                && user.getRole() != Role.TEAM_LEAD
                && user.getRole() != Role.ADMIN) {
            return;
        }

        // Remove old permissions
        userPermissionRepository.deleteByUserId(user.getId());

        // Save new permissions
        List<Permission> permissions = permissionRepository.findAllById(permissionIds);
        Set<UserPermission> userPermissions = permissions.stream()
                .map(p -> new UserPermission(null, user.getId(), p.getId()))
                .collect(Collectors.toSet());
        userPermissionRepository.saveAll(userPermissions);
    }
}


