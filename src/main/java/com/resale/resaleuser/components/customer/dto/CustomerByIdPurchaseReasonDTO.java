package com.resale.resaleuser.components.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerByIdPurchaseReasonDTO {
    private Integer id;
    private String name;
    private String nameEn;
    private String nameAr;
    private LocalDateTime createdAt;
    private CustomerByIdPurchaseSubReasonDTO subReason;
}


