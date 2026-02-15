package com.resale.homeflyuser.components.userManagement.dto.userProfile;

import com.resale.homeflyuser.model.Role;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class UpdateUserDTO implements UserAssignmentDTO, UserPermissionDTO {
    private Integer id;
    private String fullName;
    private String mobile;
    private String password;
    private String newPassword;
    private Integer assignToUserId;
    private Role role;
    private List<Integer> permissionIds;
    private Set<Integer> languageIds;
    private List<Integer> projectIds;
    private List<Integer> assignedUserIds;
}


