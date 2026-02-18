package com.resale.resaleuser.components.permission;

import com.resale.resaleuser.components.permission.dto.PermissionDTO;
import com.resale.resaleuser.logging.LogActivity;
import com.resale.resaleuser.model.ActionType;
import com.resale.resaleuser.security.CheckPermission;
import com.resale.resaleuser.security.MatchType;
import com.resale.resaleuser.utils.ReturnObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/permission")
public class PermissionController {
    @Autowired
    PermissionService permissionService;

    @GetMapping
    @CheckPermission(value = {"admin:login", "sales:login"}, match = MatchType.ANY)
    @LogActivity(ActionType.GET_PERMISSIONS)
    public ResponseEntity<ReturnObject<List<PermissionDTO>>> getAllPermissions() {
        ReturnObject<List<PermissionDTO>> result = permissionService.getAllPermissions();

        if (!result.getStatus()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }

        return ResponseEntity.ok(result);
    }

}


