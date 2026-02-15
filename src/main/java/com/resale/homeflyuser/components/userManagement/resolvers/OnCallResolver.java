package com.resale.homeflyuser.components.userManagement.resolvers;

import com.resale.homeflyuser.FeignClient.AppointmentFeignClient;
import com.resale.homeflyuser.model.Role;
import com.resale.homeflyuser.model.User;
import com.resale.homeflyuser.utils.ReturnObject;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OnCallResolver {

    private final AppointmentFeignClient appointmentFeignClient;

    public boolean resolve(User user) {

        if (user.getRole() != Role.SALESMAN && user.getRole() != Role.TEAM_LEAD) {
            return false;
        }

        try {
            ResponseEntity<ReturnObject<Boolean>> response =
                    appointmentFeignClient.isUserOnCall(user.getId());

            return response.getStatusCode().is2xxSuccessful()
                    && response.getBody() != null
                    && response.getBody().getStatus()
                    && Boolean.TRUE.equals(response.getBody().getData());

        } catch (Exception ex) {
            return false;
        }
    }
}


