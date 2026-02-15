package com.resale.homeflyuser.components.userManagement;

import com.resale.homeflyuser.FeignClient.AppointmentFeignClient;
import com.resale.homeflyuser.FeignClient.CommunicationClient;
import com.resale.homeflyuser.FeignClient.ExternalServiceSync;
import com.resale.homeflyuser.components.userManagement.dto.userProfile.CreateUserDTO;
import com.resale.homeflyuser.components.auth.dto.PermissionDTO;
import com.resale.homeflyuser.components.auth.dto.UserListDTO;
import com.resale.homeflyuser.components.auth.dto.UserResponseDTO;
import com.resale.homeflyuser.components.language.dto.LanguageDTO;
import com.resale.homeflyuser.components.userInternal.dto.ProjectDTO;
import com.resale.homeflyuser.components.userManagement.dto.*;
import com.resale.homeflyuser.components.userManagement.dto.userProfile.UpdateUserDTO;
import com.resale.homeflyuser.components.userManagement.mappers.UserFactory;
import com.resale.homeflyuser.components.userManagement.mappers.UserResponseAssembler;
import com.resale.homeflyuser.components.userManagement.resolvers.LanguageResolver;
import com.resale.homeflyuser.components.userManagement.resolvers.PermissionResolver;
import com.resale.homeflyuser.components.userManagement.resolvers.ProjectResolver;
import com.resale.homeflyuser.components.userManagement.services.AdminUserUpdateService;
import com.resale.homeflyuser.components.userManagement.services.UserPasswordService;
import com.resale.homeflyuser.components.userManagement.services.UserUpdateService;
import com.resale.homeflyuser.components.userManagement.updaters.AdminUserLanguageUpdater;
import com.resale.homeflyuser.components.userManagement.updaters.AdminUserPermissionUpdater;
import com.resale.homeflyuser.components.userManagement.updaters.AdminUserProjectUpdater;
import com.resale.homeflyuser.model.*;
import com.resale.homeflyuser.repository.*;
import com.resale.homeflyuser.security.CustomUserPrincipal;
import com.resale.homeflyuser.shared.UserValidator;
import com.resale.homeflyuser.utils.MessageUtil;
import com.resale.homeflyuser.utils.PaginatedResponseDTO;
import com.resale.homeflyuser.utils.ReturnObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
@Service
@Slf4j
public class UserManagementService {

    @Autowired
    private UserResponseAssembler userResponseAssembler;
    @Autowired
    private AdminUserPermissionUpdater adminUserPermissionUpdater;
    @Autowired
    private AdminUserLanguageUpdater adminUserLanguageUpdater;
    @Autowired
    private AdminUserProjectUpdater adminUserProjectUpdater;
    @Autowired
    private ExternalServiceSync externalServiceSync;
    @Autowired
    private UserPasswordService userPasswordService;
    @Autowired
    private UserAssignmentService userAssignmentService;
    @Autowired
    private AdminUserUpdateService adminUserUpdateService;
    @Autowired
    private UserUpdateService userUpdatePersonalInfoService;
    @Autowired
    private PermissionResolver permissionResolver;
    @Autowired
    private LanguageResolver languageResolver;
    @Autowired
    private ProjectResolver projectResolver;
    @Autowired
    UserValidator userValidator;
    @Autowired
    UserFetcher userFetcher;
    @Autowired
    UserSaver userSaver;
    @Autowired
    PermissionRepository permissionRepository;
    @Autowired
    UserPermissionRepository userPermissionRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    MessageUtil messageUtil;
    @Autowired
    UserFactory userFactory;
    @Autowired
    UserLanguageRepository userLanguageRepository;
    @Autowired
    UserProjectRepository userProjectRepository;
    @Autowired
    LanguageRepository languageRepository;
    @Autowired
    ProjectRepository projectRepository;
    @Autowired
    CommunicationClient communicationClient;
    @Autowired
    AppointmentFeignClient appointmentFeignClient;
    // Constants


    public ReturnObject<UserResponseDTO> createUser(CreateUserDTO dto) {

        // 1️⃣ Validate input
        String validationError = userValidator.validateCreateUser(dto);
        if (validationError != null) {
            return new ReturnObject<>(validationError, false, null);
        }

        // 2️⃣ Build User object in-memory
        User user = userFactory.build(dto);

        // 3️⃣ Save user
        User savedUser = userSaver.save(user);

        // 4️⃣ Handle team lead / salesman assignments
        ReturnObject<Void> assignmentResult = userAssignmentService.handleTeamLeadAssignments(savedUser, dto);
        if (!assignmentResult.getStatus()) {
            return new ReturnObject<>(assignmentResult.getMessage(), false, null);
        }

        // 5️⃣ Assign permissions
        adminUserPermissionUpdater.update(savedUser, dto);

        // 6️⃣ Assign languages
        adminUserLanguageUpdater.update(savedUser, dto);

        // 7️⃣ Assign projects
        adminUserProjectUpdater.update(savedUser, dto);

        // 8️⃣ External services (Zoom / Queue)
        externalServiceSync.createZoomUser(savedUser);
        externalServiceSync.syncToQueue(savedUser);

        // 9️⃣ Build response
        UserResponseDTO responseDTO = userResponseAssembler.build(savedUser);

        return new ReturnObject<>(messageUtil.getMessage("user.created.successfully"), true, responseDTO);
    }

    public ReturnObject<UserResponseDTO> getUserProfile(String token,Integer userId) {
        Optional<User> userOpt = userFetcher.findUser(userId);
        if (userOpt.isEmpty()) {
            String message = messageUtil.getMessage("user.not.found");
            return new ReturnObject<>(message, false, null);
        }
        Locale locale = messageUtil.getCurrentLocale();
        User user = userOpt.get();
        Set<PermissionDTO> permissionDTOs = permissionResolver.resolve(user);
        Set<LanguageDTO> languageDTOs = languageResolver.resolve(user,locale);
        List<ProjectDTO> projectDTOs = projectResolver.resolve(user,locale);
        boolean onCall = isUserOnCall(user);
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
    // Helper method for checking if the user is on call

    public ReturnObject<UserByIdResponseDTO> getUserById(Integer userId) {
        Optional<User> userOpt = userFetcher.findUser(userId);
        if (userOpt.isEmpty()) {
            String message = messageUtil.getMessage("user.not.found");
            return new ReturnObject<>(message, false, null);
        }
        Locale currentLocale = messageUtil.getCurrentLocale();
        User user = userOpt.get();
        // Get user permissions
        Set<PermissionDTO> permissionDTOs = userPermissionRepository.findByUserId(userId).stream()
                .map(up -> permissionRepository.findById(up.getPermissionId())
                        .map(perm -> new PermissionDTO(perm.getId(), perm.getAction(), perm.getResource())))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());

        Set<UserItemsDTO> projectsList = userProjectRepository.findByUserId(userId).stream()
                .map(up -> projectRepository.findById(up.getProjectId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(project -> UserItemsDTO.builder()
                        .id(project.getId())
                        .name(messageUtil.getLocalizedName(project.getNameEn(), project.getNameAr(), currentLocale))
                        .build())
                .collect(Collectors.toSet());

        Set<UserItemsDTO> teamMembers = userFetcher.findTeamMembersByLeaderId(userId);

        Set<UserItemsDTO> languagesList = userLanguageRepository.findByUserId(userId).stream()
                .map(ul -> languageRepository.findById(ul.getLanguageId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(lang -> UserItemsDTO.builder()
                        .id(lang.getId())
                        .name(messageUtil.getLocalizedName(lang.getNameEn(), lang.getNameAr(), currentLocale))
                        .build())
                .collect(Collectors.toSet());

        // Build UserResponseDTO
        UserByIdResponseDTO responseDTO = new UserByIdResponseDTO(
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
                user.getUserId(),
                projectsList,
                languagesList,
                teamMembers
        );

        String message = messageUtil.getMessage("user.profile.retrieved.successfully");
        return new ReturnObject<>(message, true, responseDTO);
    }


    public ReturnObject<String> changeSalesmanStatus(Integer userId) {
        Optional<User> userOpt = userFetcher.findUser(userId);
        if (userOpt.isEmpty()) {
            return new ReturnObject<>(messageUtil.getMessage("salesman.notfound"), false, null);
        }
        User user = userOpt.get();

        // Allow only SALESMAN role
        if (user.getRole() != Role.SALESMAN) {
            return new ReturnObject<>(messageUtil.getMessage("salesman.not.salesman"), false, null);
        }

        // Toggle is_active field
        boolean newStatus = !user.getIsActive();
        user.setIsActive(newStatus);
        userSaver.save(user);

        String messageKey = newStatus ? "salesman.status.changed.active" : "salesman.status.changed.inactive";
        return new ReturnObject<>(messageUtil.getMessage(messageKey), true, null);
    }


    public ReturnObject<PaginatedResponseDTO<UserListDTO>> getAllUsers(
            String search, Boolean isActive, Role role,Boolean notAssigned,Integer forUserId,
            Boolean isPaginated,int page, int size) {

        List<User> users = userFetcher.findAllUsers();

        // Apply search filter
        if (search != null && !search.isEmpty()) {
            String lowerSearch = search.toLowerCase();
            users = users.stream()
                    .filter(u -> u.getFullName().toLowerCase().contains(lowerSearch) ||
                            u.getEmail().toLowerCase().contains(lowerSearch))
                    .collect(Collectors.toList());
        }

        // Apply isActive filter
        if (isActive != null) {
            users = users.stream()
                    .filter(u -> Objects.equals(u.getIsActive(), isActive))
                    .collect(Collectors.toList());
        }

        // Apply role filter
        if (role != null) {
            users = users.stream()
                    .filter(u -> u.getRole() == role)
                    .collect(Collectors.toList());
        }

        if (notAssigned != null) {
            if (notAssigned) {
                // Only unassigned
                users = users.stream()
                        .filter(u -> u.getUserId() == null)
                        .collect(Collectors.toList());
            } else {
                // Only assigned
                users = users.stream()
                        .filter(u -> u.getUserId() != null)
                        .collect(Collectors.toList());
            }
        }

        if (forUserId != null) {
            //TODO check If Admin then this can be used.
            users = users.stream()
                    .filter(u -> Objects.equals(u.getUserId(), forUserId))
                    .collect(Collectors.toList());
        }

        // Map to DTO
        List<UserListDTO> result = users.stream()
                .map(u -> new UserListDTO(u.getId(), u.getFullName(), u.getEmail(), u.getIsActive(), u.getRole(),u.getUserId()))
                .collect(Collectors.toList());

        int totalElements = result.size();

        List<UserListDTO> paginatedContent;
        int totalPages;
        boolean last;

        if (isPaginated != null && !isPaginated) {
            // Return all users without slicing
            paginatedContent = result;
            totalPages = 1;
            last = true;
            page = 0;
            size = totalElements;
        } else {
            // Apply pagination
            int fromIndex = Math.min(page * size, totalElements);
            int toIndex = Math.min(fromIndex + size, totalElements);
            paginatedContent = result.subList(fromIndex, toIndex);

            totalPages = (int) Math.ceil((double) totalElements / size);
            last = page >= totalPages - 1;
        }

        PaginatedResponseDTO<UserListDTO> paginatedResponse =
                new PaginatedResponseDTO<>(paginatedContent, page, size, totalElements, totalPages, last);

        String message = messageUtil.getMessage("users.fetched.success");
        return new ReturnObject<>(message, true, paginatedResponse);
    }



    public ReturnObject<String> logout(CustomUserPrincipal principal) {
        if (principal == null) {
            String message = messageUtil.getMessage("user.not.authenticated");
            return new ReturnObject<>(message, false, null);
        }
        String message = messageUtil.getMessage("logout.successfully");
        return new ReturnObject<>(message, true, "Logged out successfully");
    }


    // API 1: Send OTP



    @Transactional
    public ReturnObject<UserResponseDTO> updateUser(Integer userId, UpdateUserDTO dto) {
        return adminUserUpdateService.updateUser(userId, dto);
    }


    public ReturnObject<?> updatePersonalInfoForCurrentUser(Integer id, UpdateUserDTO updateUserDto) {
        return userUpdatePersonalInfoService.updateUserPersonalInfo(id,updateUserDto);
    }

    public ReturnObject<String> changePassword(Integer userId, ChangePasswordDTO dto) {
        return userPasswordService.changePassword(userId, dto);
    }


    public ReturnObject<Boolean> updateFcmToken(Integer userId, UpdateFcmTokenDTO dto) {
        Optional<User> userOpt = userFetcher.findUser(userId);
        if (userOpt.isEmpty()) {
            return new ReturnObject<>(messageUtil.getMessage("salesman.notfound"), false, null);
        }
        User user = userOpt.get();
        user.setFcmToken(dto.getFcmToken());
        userSaver.save(user);
        return new ReturnObject<>("FCM Token updated successfully", true, true);
    }
    private boolean isUserOnCall(User user) {
        if (user.getRole() != Role.SALESMAN && user.getRole() != Role.TEAM_LEAD) {
            return false;
        }
        try {
            ResponseEntity<ReturnObject<Boolean>> response =
                    appointmentFeignClient.isUserOnCall(user.getId());

            return response.getStatusCode().is2xxSuccessful()
                    && response.getBody() != null
                    && Boolean.TRUE.equals(response.getBody().getData());

        } catch (Exception ex) {
            return false;
        }
    }
}

