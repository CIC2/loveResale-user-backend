package com.resale.homeflyuser.components.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeadsDTO {
    Long id;
    String name;
    String mobile;
    String mail;
}


