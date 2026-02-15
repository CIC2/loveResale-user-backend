package com.resale.homeflyuser.components.userManagement.resolvers;

import com.resale.homeflyuser.components.auth.dto.PermissionDTO;
import com.resale.homeflyuser.model.User;
import com.resale.homeflyuser.model.UserPermission;
import com.resale.homeflyuser.repository.PermissionRepository;
import com.resale.homeflyuser.repository.UserPermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PermissionResolver {

    private final UserPermissionRepository userPermissionRepository;
    private final PermissionRepository permissionRepository;

    public Set<PermissionDTO> resolve(User user) {

        Set<Integer> permissionIds =
                userPermissionRepository.findByUserId(user.getId())
                        .stream()
                        .map(UserPermission::getPermissionId)
                        .collect(Collectors.toSet());

        return permissionRepository.findAllById(permissionIds)
                .stream()
                .map(p -> new PermissionDTO(
                        p.getId(),
                        p.getAction(),
                        p.getResource()
                ))
                .collect(Collectors.toSet());
    }
}


