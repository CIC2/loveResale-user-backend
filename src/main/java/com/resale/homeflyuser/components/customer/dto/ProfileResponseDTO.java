package com.resale.homeflyuser.components.customer.dto;
import com.resale.homeflyuser.components.customer.dto.CustomerByIdPurchaseReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponseDTO {
    private String fullName;
    private String arabicFullName;
    private String mobile;
    private String email;
    private String nationality;
    private String address;
    private Boolean isVerified;
    private String nationalId;
    private String passportNumber;
    //
    private String education;
    private String occupation;
    private String gender;

    private String country;
    private String governorate;
    private String city;
    private String district;
    private String street;
    private String building;
    private String floor;
    private String apartment;
    private Boolean isNewCustomer;
    private List<String> projectNames;
    private List<CustomerByIdPurchaseReasonDTO> customerPurchaseReasonList;
}


