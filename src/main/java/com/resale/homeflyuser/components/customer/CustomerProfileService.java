package com.resale.homeflyuser.components.customer;

import com.resale.homeflyuser.FeignClient.CustomerInterface;
import com.resale.homeflyuser.components.customer.dto.CustomerByIdPurchaseReasonDTO;
import com.resale.homeflyuser.components.customer.dto.LeadsDTO;
import com.resale.homeflyuser.components.customer.dto.ProfileResponseDTO;
import com.resale.homeflyuser.utils.MessageUtil;
import com.resale.homeflyuser.utils.PaginatedResponseDTO;
import com.resale.homeflyuser.utils.ReturnObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerProfileService {
    @Autowired
    CustomerInterface customerinterface;

    @Autowired
    MessageUtil messageUtil;

    public ReturnObject<ProfileResponseDTO> getCustomerProfile(Integer customerId) {
        try {
            ReturnObject<ProfileResponseDTO> customerResponse = customerinterface.getCustomerProfile(customerId);
            String currentLocale = messageUtil.getCurrentLocale().getLanguage();

            if (customerResponse.getStatus() == null || !customerResponse.getStatus()) {
                return new ReturnObject<>(messageUtil.getMessage("customer.no.find"), false, null);
            }
            if (!customerResponse.getData().getCustomerPurchaseReasonList().isEmpty()) {
                for (CustomerByIdPurchaseReasonDTO customerByIdPurchaseReasonDTO : customerResponse.getData().getCustomerPurchaseReasonList()) {
                    if ("ar".equalsIgnoreCase(currentLocale)) {
                        customerByIdPurchaseReasonDTO.setName(customerByIdPurchaseReasonDTO.getNameAr());
                        if (customerByIdPurchaseReasonDTO.getSubReason() != null) {
                            customerByIdPurchaseReasonDTO.getSubReason().setName(customerByIdPurchaseReasonDTO.getSubReason().getNameAr());
                        }
                    } else {
                        customerByIdPurchaseReasonDTO.setName(customerByIdPurchaseReasonDTO.getNameEn());
                        if (customerByIdPurchaseReasonDTO.getSubReason() != null) {
                            customerByIdPurchaseReasonDTO.getSubReason().setName(customerByIdPurchaseReasonDTO.getSubReason().getNameEn());
                        }
                    }
                }
            }
            return new ReturnObject<>(messageUtil.getMessage("customer.fetch.success"), true, customerResponse.getData());
        } catch (Exception e) {
            return new ReturnObject<>(messageUtil.getMessage("customer.fetch.error") + e.getMessage(), false, null);
        }
    }

    public ReturnObject<PaginatedResponseDTO<LeadsDTO>> getAllLeads(Boolean isNotAssigned,
                                                                    int page,
                                                                    int size) {
        try {
            ReturnObject<PaginatedResponseDTO<LeadsDTO>> allLeads = customerinterface.getAllLeads(isNotAssigned,page,size);
            String currentLocale = messageUtil.getCurrentLocale().getLanguage();

            if (allLeads.getStatus() == null || !allLeads.getStatus()) {
                return new ReturnObject<>(messageUtil.getMessage("customer.no.find"), false, null);
            }

            return new ReturnObject<>(messageUtil.getMessage("customer.fetch.success"), true, allLeads.getData());
        } catch (Exception e) {
            return new ReturnObject<>(messageUtil.getMessage("customer.fetch.error") + e.getMessage(), false, null);
        }
    }
}


