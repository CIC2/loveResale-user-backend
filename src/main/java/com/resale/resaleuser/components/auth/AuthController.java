package com.resale.resaleuser.components.auth;

import com.resale.resaleuser.components.auth.dto.UserResponseDTO;
import com.resale.resaleuser.components.auth.dto.login.LoginRequestDTO;
import com.resale.resaleuser.utils.ReturnObject;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    AuthService authService;


    @PostMapping("/login")
    public ResponseEntity<ReturnObject<UserResponseDTO>> login(
            @RequestBody LoginRequestDTO request,
            HttpServletResponse response) {

        ReturnObject<UserResponseDTO> result = authService.login(request);

        if (!result.getStatus()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
        }

        String token = result.getData().getToken();
        String cookieName = switch (request.getSource()) {
            case ADMIN -> "ADMIN_AUTH_TOKEN";
            case SALESMAN -> "SALES_AUTH_TOKEN";
        };

        ResponseCookie cookie = ResponseCookie.from(cookieName, token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(Duration.ofDays(7))
                .sameSite("Strict")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok(result);
    }
}

