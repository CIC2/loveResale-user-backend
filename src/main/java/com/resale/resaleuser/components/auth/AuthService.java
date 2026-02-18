package com.resale.resaleuser.components.auth;

import com.resale.resaleuser.FeignClient.AppointmentFeignClient;
import com.resale.resaleuser.FeignClient.CommunicationClient;
import com.resale.resaleuser.components.auth.dto.PermissionDTO;
import com.resale.resaleuser.components.auth.dto.UserResponseDTO;
import com.resale.resaleuser.components.language.dto.LanguageDTO;
import com.resale.resaleuser.components.userInternal.dto.ProjectDTO;
import com.resale.resaleuser.model.*;
import com.resale.resaleuser.repository.*;
import com.resale.resaleuser.components.auth.dto.login.LoginRequestDTO;
import com.resale.resaleuser.security.JwtUtil;
import com.resale.resaleuser.utils.MessageUtil;
import com.resale.resaleuser.utils.ReturnObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AuthService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    UserPermissionRepository userPermissionRepository;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PermissionRepository permissionRepository;

    @Autowired
    MessageUtil messageUtil;

    @Autowired
    CommunicationClient communicationClient;
    @Autowired
    AppointmentFeignClient appointmentFeignClient;
    @Autowired
    UserLanguageRepository userLanguageRepository;
    @Autowired
    LanguageRepository languageRepository;
    @Autowired
    UserProjectRepository userProjectRepository;
    @Autowired
    ProjectRepository projectRepository;

    public ReturnObject<UserResponseDTO> login(LoginRequestDTO request) {

        if (request.getEmail() == null || request.getEmail().isBlank()
                || request.getPassword() == null || request.getPassword().isBlank()) {
            return new ReturnObject<>(
                    messageUtil.getMessage("error.missing_credentials"),
                    false,
                    null
            );
        }

        if (request.getSource() == null) {
            return new ReturnObject<>(
                    messageUtil.getMessage("error.missing_source"),
                    false,
                    null
            );
        }

        User user = userRepository.findByEmail(request.getEmail()).orElse(null);

        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return new ReturnObject<>(
                    messageUtil.getMessage("error.invalid_credentials"),
                    false,
                    null
            );
        }

        if (!Boolean.TRUE.equals(user.getIsActive())) {
            return new ReturnObject<>(
                    messageUtil.getMessage("error.deactivated_account"),
                    false,
                    null
            );
        }

        if (!Boolean.TRUE.equals(user.getIsVerified())) {
            return new ReturnObject<>(
                    messageUtil.getMessage("error.inactive_account"),
                    false,
                    null
            );
        }

        Set<Integer> permissionIds = userPermissionRepository.findByUserId(user.getId())
                .stream()
                .map(UserPermission::getPermissionId)
                .collect(Collectors.toSet());

        List<Permission> permissions = permissionRepository.findAllById(permissionIds);

        Set<PermissionDTO> permissionDTOs = permissions.stream()
                .map(p -> new PermissionDTO(p.getId(), p.getAction(), p.getResource()))
                .collect(Collectors.toSet());

        Set<String> permissionKeys = permissions.stream()
                .map(p -> p.getResource() + ":" + p.getAction())
                .collect(Collectors.toSet());

        boolean hasAccess = switch (request.getSource()) {
            case ADMIN -> permissionKeys.contains("admin:login");
            case SALESMAN -> permissionKeys.contains("sales:login");
        };

        if (!hasAccess) {
            return new ReturnObject<>(
                    messageUtil.getMessage("error.unauthorized_portal"),
                    false,
                    null
            );
        }

        Locale locale = messageUtil.getCurrentLocale();

        Set<Integer> languageIds = userLanguageRepository.findByUserId(user.getId())
                .stream()
                .map(UserLanguage::getLanguageId)
                .collect(Collectors.toSet());

        List<Language> languages = languageRepository.findAllById(languageIds);

        Set<LanguageDTO> languageDTOs = languages.stream()
                .map(lang -> {
                    String name = locale.getLanguage().equalsIgnoreCase("ar")
                            ? lang.getNameAr()
                            : lang.getNameEn();
                    return new LanguageDTO(lang.getId(), name, lang.getNameAr());
                })
                .collect(Collectors.toSet());

        Set<Integer> projectIds = userProjectRepository.findByUserId(user.getId())
                .stream()
                .map(UserProject::getProjectId)
                .collect(Collectors.toSet());

        List<Project> projects = projectRepository.findAllById(projectIds);

        List<ProjectDTO> projectDTOs = projects.stream()
                .map(project -> {
                    String name = locale.getLanguage().equalsIgnoreCase("ar")
                            ? project.getNameAr()
                            : project.getNameEn();
                    return new ProjectDTO(project.getId(), name, project.getNameAr());
                })
                .toList();

        boolean onCall = false;

        if (user.getRole() == Role.SALESMAN || user.getRole() == Role.TEAM_LEAD) {
            try {
                ResponseEntity<ReturnObject<Boolean>> onCallResponse =
                        appointmentFeignClient.isUserOnCall(user.getId());

                if (onCallResponse.getStatusCode().is2xxSuccessful()
                        && onCallResponse.getBody() != null
                        && onCallResponse.getBody().getStatus()) {

                    onCall = Boolean.TRUE.equals(onCallResponse.getBody().getData());
                }
            } catch (Exception ex) {
                onCall = false;
            }
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("iss", "user-ms");
        claims.put("userId", user.getId());
        claims.put("role", user.getRole().name());
        claims.put("portal", request.getSource().name());

        String token = jwtUtil.generateToken(
                claims,
                user.getEmail(),
                user.getId()
        );

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
                projectDTOs,
                languageDTOs,
                user.getFcmToken(),
                user.getUserId(),
                onCall
        );

        return new ReturnObject<>(
                messageUtil.getMessage("login.success"),
                true,
                responseDTO
        );
    }

}

