package com.resale.resaleuser.components.userManagement.dto.userProfile;

import com.resale.resaleuser.model.Role;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class CreateUserDTO implements UserAssignmentDTO , UserPermissionDTO {
    private String fullName;
    private String email;
    private String mobile;
    private String password;
    private String confirmPassword;
    private Role role;
    private Integer assignToUserId;
    private List<Integer> permissionIds;
    private Set<Integer> languageIds;
    private List<Integer> projectIds;
    private List<Integer> assignedUserIds;
}


