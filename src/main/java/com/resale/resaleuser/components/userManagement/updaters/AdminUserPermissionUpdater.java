package com.resale.resaleuser.components.userManagement.updaters;

import com.resale.resaleuser.components.userManagement.dto.userProfile.UserPermissionDTO;
import com.resale.resaleuser.model.Permission;
import com.resale.resaleuser.model.Role;
import com.resale.resaleuser.model.User;
import com.resale.resaleuser.model.UserPermission;
import com.resale.resaleuser.repository.PermissionRepository;
import com.resale.resaleuser.repository.UserPermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

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


