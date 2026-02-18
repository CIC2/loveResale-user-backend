package com.resale.resaleuser.components.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class  PermissionDTO {
    private Integer id;
    private String action;
    private String resource;
}


