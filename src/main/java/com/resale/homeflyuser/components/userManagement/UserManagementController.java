package com.resale.homeflyuser.components.userManagement;

import com.resale.homeflyuser.components.userManagement.dto.userProfile.CreateUserDTO;
import com.resale.homeflyuser.components.auth.dto.UserListDTO;
import com.resale.homeflyuser.components.auth.dto.UserResponseDTO;
import com.resale.homeflyuser.components.userManagement.dto.*;
import com.resale.homeflyuser.components.userManagement.dto.userProfile.UpdateUserDTO;
import com.resale.homeflyuser.components.userManagement.services.UserOtpService;
import com.resale.homeflyuser.components.userManagement.services.UserPasswordService;
import com.resale.homeflyuser.logging.LogActivity;
import com.resale.homeflyuser.model.ActionType;
import com.resale.homeflyuser.model.Role;
import com.resale.homeflyuser.security.CheckPermission;
import com.resale.homeflyuser.security.CustomUserPrincipal;
import com.resale.homeflyuser.security.MatchType;
import com.resale.homeflyuser.utils.PaginatedResponseDTO;
import com.resale.homeflyuser.utils.ReturnObject;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@ControllerAdvice
@Validated
@RequestMapping("/user")
public class UserManagementController {

    @Autowired
    UserManagementService userManagementService;
    @Autowired
    UserPasswordService userPasswordService;
    @Autowired
    UserOtpService userOtpService;

    @PostMapping
    @CheckPermission(value = {"user:create", "admin:login"}, match = MatchType.ALL)
    @LogActivity(ActionType.CREATE_USER)
    public ResponseEntity<ReturnObject<?>> createUser(@RequestBody CreateUserDTO dto) {
        ReturnObject<?> response = userManagementService.createUser(dto);
        return ResponseEntity
                .status(response.getStatus() ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST)
                .body(response);
    }


    @PutMapping("/currentUser")
    @CheckPermission(value = {"admin:login"}, match = MatchType.ALL)
    @LogActivity(ActionType.GET_CURRENT_USER)
    public ResponseEntity<ReturnObject<?>> updateUser(@AuthenticationPrincipal CustomUserPrincipal principal,
                                                      @RequestBody UpdateUserDTO updateUserDto) {
        ReturnObject<?> response = userManagementService.updatePersonalInfoForCurrentUser(principal.getId(),updateUserDto);
        return ResponseEntity
                .status(response.getStatus() ? HttpStatus.OK : HttpStatus.BAD_REQUEST)
                .body(response);
    }

/*    @PutMapping("/changePassword")
    @CheckPermission(value = {"admin:login", "sales:login"}, match = MatchType.ANY)
    public ResponseEntity<ReturnObject<String>> changePassword(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @Valid @RequestBody ChangePasswordDTO dto) {
        ReturnObject<String> response = userManagementService.changePassword(principal.getId(), dto);
        return ResponseEntity
                .status(response.getStatus() ? HttpStatus.OK : HttpStatus.BAD_REQUEST)
                .body(response);
    }*/


    @GetMapping("/profile")
    @CheckPermission(value = {"admin:login", "sales:login"}, match = MatchType.ANY)
    @LogActivity(ActionType.GET_PROFILE)
    public ResponseEntity<ReturnObject<UserResponseDTO>> getUserProfile(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            HttpServletRequest request) {
        Integer userId = principal.getId();
        String token = "";
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("SALES_AUTH_TOKEN".equals(cookie.getName())||"ADMIN_AUTH_TOKEN".equals(cookie.getName())) {
                    System.out.println("JWT: " + cookie.getValue());
                    token = cookie.getValue();
                }
            }
        }
        ReturnObject<UserResponseDTO> response = userManagementService.getUserProfile(token,userId);
        return ResponseEntity
                .status(response.getStatus() ? HttpStatus.OK : HttpStatus.NOT_FOUND)
                .body(response);
    }

    @GetMapping("userProfile/{userId}")
    @CheckPermission(value = {"admin:login"}, match = MatchType.ANY)
    @LogActivity(ActionType.GET_PROFILE_BY_ID)
    public ResponseEntity<ReturnObject<UserByIdResponseDTO>> getUserById(@PathVariable("userId") Long userId) {
        ReturnObject<UserByIdResponseDTO> response = userManagementService.getUserById(Math.toIntExact(userId));
        return ResponseEntity
                .status(response.getStatus() ? HttpStatus.OK : HttpStatus.NOT_FOUND)
                .body(response);
    }


    @PutMapping("/status")
    @CheckPermission(value = {"admin:login"}, match = MatchType.ANY)
    @LogActivity(ActionType.CHANGE_STATUS)
    public ResponseEntity<ReturnObject<?>> changeSalesmanStatus(@RequestParam Integer userId) {
        ReturnObject<?> response = userManagementService.changeSalesmanStatus(userId);
        return ResponseEntity.status(response.getStatus() ? HttpStatus.OK : HttpStatus.BAD_REQUEST).body(response);
    }

    @GetMapping("")
    @CheckPermission(value = {"admin:login"})
    @LogActivity(ActionType.GET_ALL_USERS)
    public ResponseEntity<ReturnObject<PaginatedResponseDTO<UserListDTO>>> getAllUsers(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "is_active", required = false) Boolean isActive,
            @RequestParam(value = "role", required = false) Role role,
            @RequestParam(value = "note_assigned", required = false) Boolean notAssigned,
            @RequestParam(value = "for_user_id", required = false) Integer forUserId,
            @RequestParam(value = "is_paginated", required = false) Boolean isPaginated,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        ReturnObject<PaginatedResponseDTO<UserListDTO>> response =
                userManagementService.getAllUsers(search, isActive, role, notAssigned,forUserId,isPaginated, page, size);
        return ResponseEntity
                .status(response.getStatus() ? HttpStatus.OK : HttpStatus.BAD_REQUEST)
                .body(response);
    }


    @PostMapping("/logout")
    @LogActivity(ActionType.LOGOUT)
    public ResponseEntity<ReturnObject<String>> logout(
            @AuthenticationPrincipal CustomUserPrincipal principal) {
        ReturnObject<String> response = userManagementService.logout(principal);
        return ResponseEntity
                .status(response.getStatus() ? HttpStatus.OK : HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @PostMapping("/forgotPassword/sendOtp")
    @LogActivity(ActionType.SEND_FORGET_PASSWORD_OTP)
    public ResponseEntity<ReturnObject<?>> sendOtp(@Valid @RequestBody SendOtpDTO dto) {
        ReturnObject<?> response = userOtpService.sendOtp(dto);
        if(response.getStatus()) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(response);
        }else{
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/forgotPassword/verifyOtp")
    @LogActivity(ActionType.VERIFY_FORGET_PASSWORD_OTP)
    public ResponseEntity<ReturnObject<String>> verifyOtp(@Valid @RequestBody VerifyOtpDTO dto) {
        ReturnObject<String> response = userOtpService.verifyOtp(dto);
        return ResponseEntity
                .status(response.getStatus() ? HttpStatus.OK : HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @PostMapping("/forgotPassword/reset")
    @LogActivity(ActionType.RESET_FORGET_PASSWORD)
    public ResponseEntity<ReturnObject<String>> forgotPasswordReset(@Valid @RequestBody ForgotPasswordDTO dto) {
        ReturnObject<String> response = userPasswordService.forgotPasswordReset(dto);
        return ResponseEntity
                .status(response.getStatus() ? HttpStatus.OK : HttpStatus.BAD_REQUEST)
                .body(response);
    }


    @PutMapping("/{userId}")
    @CheckPermission(value = {"admin:login"})
    @LogActivity(ActionType.UPDATE_USER)
    public ResponseEntity<ReturnObject<UserResponseDTO>> updateUser(
            @PathVariable Integer userId,
            @RequestBody UpdateUserDTO dto) {
        ReturnObject<UserResponseDTO> result = userManagementService.updateUser(userId, dto);
        return ResponseEntity
                .status(result.getStatus() ? HttpStatus.OK : HttpStatus.BAD_REQUEST)
                .body(result);
    }


    @PutMapping("/updateFcmToken")
    @LogActivity(ActionType.UPDATE_TOKEN)
    public ResponseEntity<?> updateFcmToken(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @RequestBody UpdateFcmTokenDTO dto
    ) {
        Integer userId = principal.getId();

        ReturnObject<Boolean> result = userManagementService.updateFcmToken(userId, dto);

        if (!result.getStatus()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }

        return ResponseEntity.ok(result);
    }

}

