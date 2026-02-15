package com.resale.homeflyuser.components.auth.dto;

import com.resale.homeflyuser.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserListDTO {
    private Integer id;
    private String fullName;
    private String email;
    private Boolean isActive;
    private Role role;
    private Integer teamLeadId;
}


