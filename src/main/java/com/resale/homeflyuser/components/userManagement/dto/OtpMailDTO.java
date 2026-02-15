package com.resale.homeflyuser.components.userManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OtpMailDTO {
    String email;
    String otp;
    String mailSubject;
    String mailContent;
}


