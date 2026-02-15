package com.resale.homeflyuser.FeignClient;

import com.resale.homeflyuser.components.userManagement.dto.CreateZoomUserDTO;
import com.resale.homeflyuser.components.userManagement.dto.OtpMailDTO;
import com.resale.homeflyuser.utils.ReturnObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "communication-ms", url = "${communication.service.url}")
public interface CommunicationClient {

    @PostMapping("/user/zoom/createUser")
    ReturnObject<String> createZoomUser(@RequestBody CreateZoomUserDTO dto);

    @PostMapping("/mail/user/sendOtpMail")
    ReturnObject<String> sendOtpMail(@RequestBody OtpMailDTO otpMailDTO);
}

