package com.resale.resaleuser.security;

import com.resale.resaleuser.repository.PermissionRepository;
import com.resale.resaleuser.components.userInternal.UserService;
import com.resale.resaleuser.utils.ReturnObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class CheckPermissionAspect {

    @Autowired
    private UserService userService;

    @Autowired
    private PermissionRepository permissionRepository;

    @Around("@annotation(checkPermission)")
    public Object verifyPermission(ProceedingJoinPoint joinPoint, CheckPermission checkPermission) throws Throwable {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !(auth.getPrincipal() instanceof CustomUserPrincipal userPrincipal)) {
            ReturnObject<?> error = new ReturnObject<>("Unauthorized - user not authenticated", false, null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }


        String[] requiredPermissions = checkPermission.value();
        boolean matchAny = (checkPermission.match() == MatchType.ANY);
        boolean hasPermission = (matchAny ? false : true);


        for (String permission : requiredPermissions) {
            String[] parts = permission.split(":");
            if (parts.length != 2) {
                ReturnObject<?> error = new ReturnObject<>(
                        "Invalid permission format in @CheckPermission. Expected 'resource:ACTION'. Got: " + permission,
                        false,
                        null
                );
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
            }

            String resource = parts[0].trim();
            String action = parts[1].trim();

            boolean userHas = userPrincipal.hasPermission(action, resource);


            if (matchAny && userHas) {
                return joinPoint.proceed();
            }

            if (!matchAny && !userHas) {
                String msg = String.format("Access denied: Missing permission '%s:%s'.", resource, action);
                ReturnObject<?> error = new ReturnObject<>(msg, false, null);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
            }
        }

        if (matchAny) {
            String message = String.format("Access denied: Missing any of required permissions %s.",
                    String.join(", ", requiredPermissions));
            ReturnObject<?> error = new ReturnObject<>(message, false, null);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        }

        return joinPoint.proceed();
    }
}


