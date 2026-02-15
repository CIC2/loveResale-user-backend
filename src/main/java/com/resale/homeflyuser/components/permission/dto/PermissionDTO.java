package com.resale.homeflyuser.components.permission.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PermissionDTO {
    private Integer id;
    private String action;
    private String resource;
}


