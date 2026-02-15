package com.resale.homeflyuser.components.userManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateZoomUserDTO {
    private String email;
    private String firstName;
    private String lastName;
}


