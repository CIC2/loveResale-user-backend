package com.resale.homeflyuser.components.permission;

import com.resale.homeflyuser.components.permission.dto.PermissionDTO;
import com.resale.homeflyuser.repository.PermissionRepository;
import com.resale.homeflyuser.model.Permission;
import com.resale.homeflyuser.utils.ReturnObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PermissionService {
    @Autowired
    PermissionRepository permissionRepository;

    public ReturnObject<List<PermissionDTO>> getAllPermissions() {
        ReturnObject<List<PermissionDTO>> result = new ReturnObject<>();

        try {
            List<Permission> permissions = permissionRepository.findAll();

            if (permissions.isEmpty()) {
                result.setStatus(false);
                result.setMessage("No permissions found");
                result.setData(null);
                return result;
            }

            List<PermissionDTO> permissionDTOs = permissions.stream()
                    .map(p -> new PermissionDTO(p.getId(), p.getAction(), p.getResource()))
                    .collect(Collectors.toList());

            result.setStatus(true);
            result.setMessage("Permissions retrieved successfully");
            result.setData(permissionDTOs);

        } catch (Exception e) {
            result.setStatus(false);
            result.setMessage("Error retrieving permissions: " + e.getMessage());
            result.setData(null);
        }

        return result;
    }
}

