package com.resale.resaleuser.FeignClient;

import com.resale.resaleuser.utils.ReturnObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "appointment-ms",
        url = "${appointment.ms.url}"
)
public interface AppointmentFeignClient {

    @GetMapping("/internal/onCall/{userId}")
    ResponseEntity<ReturnObject<Boolean>> isUserOnCall(
            @PathVariable("userId") Integer userId
    );
}


