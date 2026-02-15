package com.resale.homeflyuser.components.userManagement.updaters;

import com.resale.homeflyuser.components.userManagement.dto.userProfile.CreateUserDTO;
import com.resale.homeflyuser.components.userManagement.dto.userProfile.UpdateUserDTO;
import com.resale.homeflyuser.model.Role;
import com.resale.homeflyuser.model.User;
import com.resale.homeflyuser.model.UserProject;
import com.resale.homeflyuser.repository.UserProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AdminUserProjectUpdater {

    private final UserProjectRepository userProjectRepository;

    public Set<Integer> update(User user, UpdateUserDTO dto) {
        return updateProjects(user, dto.getProjectIds());
    }

    /**
     * Update user projects from CreateUserDTO and return updated project IDs
     */
    public Set<Integer> update(User user, CreateUserDTO dto) {
        return updateProjects(user, dto.getProjectIds());
    }

    private Set<Integer> updateProjects(User user, List<Integer> projectIds) {
        if (projectIds == null || projectIds.isEmpty()) {
            return Collections.emptySet();
        }

        if (user.getRole() != Role.SALESMAN && user.getRole() != Role.TEAM_LEAD) {
            return Collections.emptySet();
        }

        // Delete old projects
        userProjectRepository.deleteByUserId(user.getId());

        // Save new projects
        List<UserProject> userProjects = projectIds.stream()
                .map(pid -> new UserProject(null, user.getId(), pid))
                .toList();
        userProjectRepository.saveAll(userProjects);

        // Return updated project IDs
        return userProjects.stream()
                .map(UserProject::getProjectId)
                .collect(Collectors.toSet());
    }
}


