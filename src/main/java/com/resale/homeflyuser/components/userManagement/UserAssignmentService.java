package com.resale.homeflyuser.components.userManagement;

import com.resale.homeflyuser.components.userManagement.dto.userProfile.UserAssignmentDTO;
import com.resale.homeflyuser.model.Role;
import com.resale.homeflyuser.model.User;
import com.resale.homeflyuser.repository.UserRepository;
import com.resale.homeflyuser.utils.ReturnObject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserAssignmentService {

    private final UserRepository userRepository;

    public ReturnObject<Void> handleTeamLeadAssignments(User existingUser, UserAssignmentDTO dto) {

        // Assign salesman to team lead
        if (dto.getAssignToUserId() != null) {
            if (existingUser.getRole() == Role.SALESMAN) {
                existingUser.setUserId(dto.getAssignToUserId());
            } else {
                String message = "Can't assign team lead for non-Salesman";
                return new ReturnObject<>(message, false, null);
            }
        }

        // Assign salesmen to team lead
        if (existingUser.getRole() == Role.TEAM_LEAD && dto.getAssignedUserIds() != null) {
            for (Integer salesmanId : dto.getAssignedUserIds()) {
                userRepository.findById(salesmanId)
                        .ifPresent(salesman -> {
                            // Only assign if the target is SALESMAN
                            if (salesman.getRole() == Role.SALESMAN) {
                                salesman.setUserId(existingUser.getId());
                                userRepository.save(salesman);
                            }
                        });
            }
        }
        return new ReturnObject<>("Assignments handled successfully", true, null);
    }
}


