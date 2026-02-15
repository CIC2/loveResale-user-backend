package com.resale.homeflyuser.components.auth.dto;

import com.resale.homeflyuser.components.language.dto.LanguageDTO;
import com.resale.homeflyuser.components.userInternal.dto.ProjectDTO;
import com.resale.homeflyuser.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDTO {
    private String token;
    private Integer id;
    private String fullName;
    private String email;
    private String mobile;
    private Boolean isActive;
    private Boolean isVerified;
    private Integer status;
    private Role role;
    private Set<PermissionDTO> permissions;
    private String sapId;
    private String c4cId;
    private List<ProjectDTO> projects;
    private Set<LanguageDTO> languages;
    private String fcmToken;
    private Integer teamLeadId;
    private Boolean onCall;


    public UserResponseDTO(
            Integer id,
            String fullName,
            String email,
            String mobile,
            Boolean isActive,
            Boolean isVerified,
            Integer status,
            Role role,
            Set<PermissionDTO> permissions,
            String sapId,
            String c4cId,
            Integer teamLeadId,
            String s) {
        this.token = token;
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.mobile = mobile;
        this.isActive = isActive;
        this.isVerified = isVerified;
        this.status = status;
        this.role = role;
        this.permissions = permissions;
        this.sapId = sapId;
        this.c4cId = c4cId;
        this.projects = null;
        this.languages = null;
        this.teamLeadId = teamLeadId;
    }

    public UserResponseDTO(
            Integer id,
            String fullName,
            String email,
            String mobile,
            Boolean isActive,
            Boolean isVerified,
            Integer status,
            Role role,
            Set<PermissionDTO> permissions,
            String sapId,
            String c4cId,
            Integer teamLeadId,
            Boolean onCall

    ) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.mobile = mobile;
        this.isActive = isActive;
        this.isVerified = isVerified;
        this.status = status;
        this.role = role;
        this.permissions = permissions;
        this.sapId = sapId;
        this.c4cId = c4cId;
        this.projects = null;
        this.languages = null;
        this.teamLeadId = teamLeadId;
        this.onCall = onCall;
    }
}


