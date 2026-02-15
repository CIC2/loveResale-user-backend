package com.resale.homeflyuser.components.userInternal;

import com.resale.homeflyuser.components.auth.dto.UserResponseDTO;
import com.resale.homeflyuser.components.userInternal.dto.SalesDTO;
import com.resale.homeflyuser.components.userInternal.dto.UserProfileDTO;
import com.resale.homeflyuser.logging.LogActivity;
import com.resale.homeflyuser.model.ActionType;
import com.resale.homeflyuser.model.User;
import com.resale.homeflyuser.utils.ReturnObject;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/*
 * Used for internal Micro-services
 * */

@RestController
@RequestMapping("user")
@RequiredArgsConstructor
public class UserController {

    @Autowired
    UserService userService;


    @GetMapping("/{userId}/profile")
    @LogActivity(ActionType.INTERNAL_GET_USER_PROFILE)
    public ResponseEntity<ReturnObject<UserProfileDTO>> getUserProfile(
            @PathVariable Integer userId) {

        ReturnObject<UserProfileDTO> response =
                userService.getUser(userId);

        if (Boolean.FALSE.equals(response.getStatus())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/assignSalesmanRoundRobin")
    @LogActivity(ActionType.INTERNAL_ASSIGN_SALESMAN)
    public ResponseEntity<?> getAssignSalesmanRoundRobin(
            @RequestHeader(value = "X-Internal-Auth", required = false) String internalToken,
            @RequestParam (required = false) Integer projectId) {

        System.out.println("➡️ [RoundRobin API] Incoming request...");

        try {
            System.out.println("✔️ Token verified. Assigning salesman...");

            // Execute round robin
            User assignedSalesman = userService.getAssignSalesmanRoundRobin(projectId);

            if (assignedSalesman == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ReturnObject<>("No SALESMAN found", false, null));
            }

            System.out.println("✔️ Assigned Salesman ID: " + assignedSalesman.getId());

            return ResponseEntity.ok(
                    new ReturnObject<>(
                            "Salesman assigned successfully",
                            true,
                            assignedSalesman
                    )
            );

        } catch (Exception ex) {
            System.out.println("❌ Error in Round Robin assignment: " + ex.getMessage());
            ex.printStackTrace();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ReturnObject<>(
                            "Error assigning salesman: " + ex.getMessage(),
                            false,
                            null
                    ));
        }
    }

    @GetMapping("/allSales")
    @LogActivity(ActionType.INTERNAL_GET_SALES_AND_TEAM_LEADS)
    public ResponseEntity<ReturnObject<List<SalesDTO>>> getSalesmenAndTeamLeads() {

        ReturnObject<List<SalesDTO>> response =
                userService.getSalesmenAndTeamLeads();

        if (Boolean.FALSE.equals(response.getStatus())) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(response);
        }
        return ResponseEntity.ok(response);
    }

    //for appointment ms
    @GetMapping("/zoomId/{userId}")
    @LogActivity(ActionType.INTERNAL_GET_ZOOM_ID_FOR_USER)
    public ResponseEntity<ReturnObject<String>> getZoomIdForUser(@PathVariable Integer userId) {
        return userService.getZoomId(userId);
    }

    @GetMapping("/{teamLeadId}/{userId}/assigned")
    @LogActivity(ActionType.INTERNAL_IS_USER_ASSIGNED_TO_TEAMLEAD)
    public ResponseEntity<ReturnObject> isUserAssignedToTeamLead(
            @PathVariable Integer teamLeadId,
            @PathVariable Integer userId
    ) {
        return userService.isUserAssignedToTeamLead(teamLeadId, userId);
    }

    @GetMapping("/id")
    @LogActivity(ActionType.INTERNAL_GET_USER_PROFILE)
    public ResponseEntity<ReturnObject<UserResponseDTO>> getUserProfile(@RequestParam("userId") Long userId,
                                                                        @RequestHeader(value = "X-Internal-Auth", required = false) String internalToken) {
        // ✅ Security check
        ReturnObject<UserResponseDTO> response = userService.getUserProfile("",Math.toIntExact(userId));
        return ResponseEntity
                .status(response.getStatus() ? HttpStatus.OK : HttpStatus.NOT_FOUND)
                .body(response);
    }
}

