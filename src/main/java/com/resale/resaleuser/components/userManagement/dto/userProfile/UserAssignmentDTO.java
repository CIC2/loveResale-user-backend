package com.resale.resaleuser.components.userManagement.dto.userProfile;

import com.resale.resaleuser.model.Role;

import java.util.List;

public interface UserAssignmentDTO {
    Integer getAssignToUserId();
    List<Integer> getAssignedUserIds();
    Role getRole();
}

