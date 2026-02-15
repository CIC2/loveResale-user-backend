package com.resale.homeflyuser.FeignClient;

import com.resale.homeflyuser.components.customer.dto.LeadsDTO;
import com.resale.homeflyuser.components.customer.dto.ProfileResponseDTO;
import com.resale.homeflyuser.utils.PaginatedResponseDTO;
import com.resale.homeflyuser.utils.ReturnObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

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


