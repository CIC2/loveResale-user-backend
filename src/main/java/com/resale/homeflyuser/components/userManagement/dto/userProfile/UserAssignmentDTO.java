package com.resale.homeflyuser.components.userManagement.dto.userProfile;

import com.resale.homeflyuser.model.Role;

import java.util.List;

public interface UserAssignmentDTO {
    Integer getAssignToUserId();
    List<Integer> getAssignedUserIds();
    Role getRole();
}

