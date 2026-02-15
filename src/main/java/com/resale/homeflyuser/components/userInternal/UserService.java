package com.resale.homeflyuser.components.userInternal;


import com.resale.homeflyuser.FeignClient.AppointmentFeignClient;
import com.resale.homeflyuser.components.auth.dto.PermissionDTO;
import com.resale.homeflyuser.components.auth.dto.UserResponseDTO;
import com.resale.homeflyuser.components.language.dto.LanguageDTO;
import com.resale.homeflyuser.components.userInternal.dto.*;
import com.resale.homeflyuser.model.*;
import com.resale.homeflyuser.repository.*;
import com.resale.homeflyuser.utils.MessageUtil;
import com.resale.homeflyuser.utils.ReturnObject;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    MessageUtil messageUtil;
    @Autowired
    UserLanguageRepository userLanguageRepository;
    @Autowired
    LanguageRepository languageRepository;
    @Autowired
    ProjectRepository projectRepository;
    @Autowired
    UserProjectRepository userProjectRepository;
    @Autowired
    PermissionRepository permissionRepository;
    @Autowired
    UserPermissionRepository userPermissionRepository;
    @Autowired
    ModelRepository modelRepository;
    @Autowired
    AppointmentFeignClient appointmentFeignClient;

    public ReturnObject<UserProfileDTO> getUser(Integer userId) {

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return new ReturnObject<>("User not found", false, null);
        }

        // Languages
        List<LanguageDTO> languages =
                userLanguageRepository.findByUserId(userId).stream()
                        .map(ul -> languageRepository.findById(ul.getLanguageId()).orElse(null))
                        .filter(Objects::nonNull)
                        .map(lang -> new LanguageDTO(
                                lang.getId(),
                                lang.getNameEn(),
                                lang.getNameAr()
                        ))
                        .toList();

        // User projects
        List<Project> projects =
                userProjectRepository.findByUserId(userId).stream()
                        .map(up -> projectRepository.findById(up.getProjectId()).orElse(null))
                        .filter(Objects::nonNull)
                        .toList();

        // Project IDs
        List<Integer> projectIds =
                projects.stream()
                        .map(Project::getId)
                        .toList();

        List<Integer> modelIds =
                projectIds.isEmpty()
                        ? List.of()
                        : modelRepository.findModelIdsByProjectIds(projectIds);


        // Project DTOs
        List<ProjectDTO> projectDTOs =
                projects.stream()
                        .map(p -> new ProjectDTO(
                                p.getId(),
                                p.getNameEn(),
                                p.getNameAr()
                        ))
                        .toList();

        UserProfileDTO dto = new UserProfileDTO(
                user.getId(),
                user.getFullName(),
                user.getUserId(),
                languages,
                projectDTOs,
                modelIds
        );

        return new ReturnObject<>(
                "User profile retrieved successfully",
                true,
                dto
        );
    }



    public ReturnObject<List<SalesDTO>> getSalesmenAndTeamLeads() {

        List<User> users = userRepository.findByRoleIn(
                List.of(Role.SALESMAN, Role.TEAM_LEAD)
        );

        if (users == null || users.isEmpty()) {
            return new ReturnObject<>(
                    "No salesmen or team leads found",
                    false,
                    null
            );
        }

        List<SalesDTO> result = users.stream()
                .map(user -> new SalesDTO(
                        user.getId(),
                        user.getFullName()
                ))
                .toList();

        return new ReturnObject<>(
                "Salesmen and team leads retrieved successfully",
                true,
                result
        );
    }

    public ResponseEntity<ReturnObject> isUserAssignedToTeamLead(Integer teamLeadId, Integer userId) {
        Boolean isUserExist = userRepository.existsByIdAndUserId(userId, teamLeadId);
        ReturnObject returnObject = new ReturnObject();
        returnObject.setData(isUserExist);
        if (isUserExist) {
            returnObject.setStatus(true);
            return ResponseEntity.status(HttpStatus.OK).body(returnObject);
        } else {
            returnObject.setStatus(false);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(returnObject);
        }
    }

    public User getAssignSalesmanRoundRobin(Integer projectId) {

        // First: salesmen assigned to this project
        List<User> salesmen;

        if (projectId != null) {
            salesmen = userRepository.findSalesmenOrderedByLastAssigned(projectId);
        } else {
            salesmen = userRepository.findSalesmenOrderedByLastAssigned();
        }
        // Fallback: if no salesmen assigned for this project
        if (salesmen.isEmpty()) {
            salesmen = userRepository.findSalesmenOrderedByLastAssigned();
            if (salesmen.isEmpty()) return null; // no salesmen at all
        }

        User selected = salesmen.get(0);

        selected.setLastAssigned(Timestamp.from(Instant.now()));
        userRepository.save(selected);

        return selected;
    }

    @Transactional(readOnly = true)
    public ResponseEntity<ReturnObject<String>> getZoomId(Integer userId) {
        Optional<User> optionalUser = userRepository.findById(userId);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ReturnObject<>("User not found with ID: " + userId, false, null));
        }

        User user = optionalUser.get();

        String getZoomId = user.getZoomId();

        if (getZoomId == null || getZoomId.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ReturnObject<>("Zoom ID not configured for user: " + userId, false, null));
        }

        return ResponseEntity.ok(
                new ReturnObject<>("Zoom ID fetched successfully", true, getZoomId)
        );
    }

    public ReturnObject<UserResponseDTO> getUserProfile(String token, Integer userId) {
        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            String message = messageUtil.getMessage("user.not.found");
            return new ReturnObject<>(message, false, null);
        }

        // Get user permissions
        Set<PermissionDTO> permissionDTOs = new HashSet<>();
        Set<UserPermission> userPermissions = userPermissionRepository.findByUserId(userId);
        for (UserPermission up : userPermissions) {
            Permission perm = permissionRepository.findById(up.getPermissionId()).orElse(null);
            if (perm != null) {
                permissionDTOs.add(new PermissionDTO(perm.getId(), perm.getAction(), perm.getResource()));
            }
        }

        List<ProjectDTO> projectDTOs = new ArrayList<>();
        List<UserProject> userProjects = userProjectRepository.findByUserId(userId);
        for (UserProject up : userProjects) {
            Project project = projectRepository.findById(up.getProjectId()).orElse(null);
            if (project != null) {
                projectDTOs.add(new ProjectDTO(
                        project.getId(),
                        project.getNameEn(),
                        project.getNameAr()
                ));
            }
        }

        Set<LanguageDTO> languageDTOs = new HashSet<>();
        List<UserLanguage> userLanguages = userLanguageRepository.findByUserId(userId);
        for (UserLanguage ul : userLanguages) {
            Language lang = languageRepository.findById(ul.getLanguageId()).orElse(null);
            if (lang != null) {
                languageDTOs.add(new LanguageDTO(
                        lang.getId(),
                        lang.getNameEn(),
                        lang.getNameAr()
                ));
            }
        }

        boolean onCall = false;
        if (user.getRole() == Role.SALESMAN || user.getRole() == Role.TEAM_LEAD) {

            try {
                ResponseEntity<ReturnObject<Boolean>> onCallResponse =
                        appointmentFeignClient.isUserOnCall(user.getId());

                if (onCallResponse.getStatusCode().is2xxSuccessful()
                        && onCallResponse.getBody() != null
                        && onCallResponse.getBody().getStatus()) {

                    onCall = Boolean.TRUE.equals(
                            onCallResponse.getBody().getData()
                    );
                }

            } catch (Exception ex) {
                onCall = false;
            }
        }
        // Build UserResponseDTO
        UserResponseDTO responseDTO = new UserResponseDTO(
                token,
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getMobile(),
                user.getIsActive(),
                user.getIsVerified(),
                user.getStatus(),
                user.getRole(),
                permissionDTOs,
                user.getSapId(),
                user.getC4cId(),
                projectDTOs,
                languageDTOs,
                user.getFcmToken(),
                user.getUserId(),
                onCall
        );

        String message = messageUtil.getMessage("user.profile.retrieved.successfully");
        return new ReturnObject<>(message, true, responseDTO);
    }

}



