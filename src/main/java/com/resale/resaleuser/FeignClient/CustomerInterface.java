package com.resale.resaleuser.FeignClient;

import com.resale.resaleuser.components.customer.dto.LeadsDTO;
import com.resale.resaleuser.components.customer.dto.ProfileResponseDTO;
import com.resale.resaleuser.utils.PaginatedResponseDTO;
import com.resale.resaleuser.utils.ReturnObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "CustomerApp" ,
        url = "${customer.service.url}")
public interface CustomerInterface {

    @GetMapping("/profile/{id}")
    ReturnObject<ProfileResponseDTO> getCustomerProfile(@PathVariable("id") Integer id);

    @GetMapping("/profile/leads")
    ReturnObject<PaginatedResponseDTO<LeadsDTO>> getAllLeads(
            @RequestParam(required = false) Boolean isNotAssigned,
            @RequestParam int page,
            @RequestParam int size
    );

}


