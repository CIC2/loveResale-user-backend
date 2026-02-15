package com.resale.homeflyuser.components.userManagement.dto;

import com.resale.homeflyuser.components.auth.dto.PermissionDTO;
import com.resale.homeflyuser.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserByIdResponseDTO {
    private Integer id;
    private String fullName;
    private String email;
    private String mobile;
    private Boolean isActive;
    private Boolean isVerified;
    private Integer status;
    private Role role;
    private Set<PermissionDTO> permissions;
    private Integer teamLeadId;
    private Set<UserItemsDTO> projects;
    private Set<UserItemsDTO> languages;
    private Set<UserItemsDTO> teamMembers;
}


