package com.resale.homeflyuser.components.customer;

import com.resale.homeflyuser.components.customer.dto.LeadsDTO;
import com.resale.homeflyuser.components.customer.dto.ProfileResponseDTO;
import com.resale.homeflyuser.logging.LogActivity;
import com.resale.homeflyuser.model.ActionType;
import com.resale.homeflyuser.security.CheckPermission;
import com.resale.homeflyuser.security.MatchType;
import com.resale.homeflyuser.utils.PaginatedResponseDTO;
import com.resale.homeflyuser.utils.ReturnObject;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("customerProfile")
@RequiredArgsConstructor
public class CustomerProfileController {
    @Autowired
    CustomerProfileService customerProfileService;
    @GetMapping("{customerId}")
    @CheckPermission(value = {"admin:login", "sales:login"}, match = MatchType.ANY)
    @LogActivity(ActionType.GET_CUSTOMER_PROFILE)
    public ResponseEntity<ReturnObject<ProfileResponseDTO>> getCustomerProfile(@PathVariable Integer customerId) {
        ReturnObject<ProfileResponseDTO> response = customerProfileService.getCustomerProfile(customerId);
        if (response.getStatus() == null || !response.getStatus()) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("leads")
    @CheckPermission(value = {"admin:login", "sales:login"}, match = MatchType.ANY)
    public ResponseEntity<ReturnObject<PaginatedResponseDTO<LeadsDTO>>> getAllLeads(
            @RequestParam(required = false) Boolean isNotAssigned,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){
        ReturnObject<PaginatedResponseDTO<LeadsDTO>> response = customerProfileService.getAllLeads(isNotAssigned,page,size);
        if (response.getStatus() == null || !response.getStatus()) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        return ResponseEntity.ok(response);
    }

}


