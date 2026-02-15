package com.resale.homeflyuser.components.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerByIdPurchaseSubReasonDTO {
        private Integer id;
        private String name;
        private String nameEn;
        private String nameAr;
}

