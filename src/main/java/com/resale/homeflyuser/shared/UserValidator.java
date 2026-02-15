package com.resale.homeflyuser.shared;

import com.resale.homeflyuser.components.userManagement.dto.userProfile.CreateUserDTO;
import com.resale.homeflyuser.components.userManagement.dto.userProfile.UpdateUserDTO;
import com.resale.homeflyuser.model.Role;
import com.resale.homeflyuser.model.User;
import com.resale.homeflyuser.repository.UserRepository;
import com.resale.homeflyuser.utils.MessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class UserValidator {

    @Autowired
    UserRepository userRepository;

    @Autowired
    MessageUtil messageUtil;

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&_])[A-Za-z\\d@$!%*?&_]{8,}$");
    private static final Pattern MOBILE_PATTERN =
            Pattern.compile("^\\d{10,}$");


    public String validateCreateUser(CreateUserDTO dto) {
        if (dto.getFullName() == null || dto.getFullName().isBlank()) {
            return messageUtil.getMessage("user.fullname.required");
        }
        if (!dto.getFullName().matches("^[A-Za-z ]+$")) {
            return messageUtil.getMessage("user.fullname.invalidCharacters");
        }
        if (dto.getEmail() == null || dto.getEmail().isBlank()) {
            return messageUtil.getMessage("user.email.required");
        }
        if (!EMAIL_PATTERN.matcher(dto.getEmail()).matches()) {
            return messageUtil.getMessage("user.email.invalid");
        }

        if (dto.getMobile() == null || dto.getMobile().isBlank()) {
            return messageUtil.getMessage("user.mobile.required");
        }
        if (!MOBILE_PATTERN.matcher(dto.getMobile()).matches()) {
            return messageUtil.getMessage("user.mobile.invalid");
        }

        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
            return messageUtil.getMessage("user.password.required");
        }
        if (!PASSWORD_PATTERN.matcher(dto.getPassword()).matches()) {
            return messageUtil.getMessage("user.password.weak");
        }
        if (dto.getConfirmPassword() == null || dto.getConfirmPassword().isBlank()) {
            return messageUtil.getMessage("user.confirm_password.required");
        }
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            return messageUtil.getMessage("user.passwords.mismatch");
        }

        if (dto.getRole() == null) {
            return messageUtil.getMessage("user.role.required");
        }

        Role role;
        try {
            role = Role.valueOf(dto.getRole().name());
        } catch (IllegalArgumentException e) {
            return messageUtil.getMessage("user.role.invalid");
        }

        if ((role == Role.SALESMAN || role == Role.TEAM_LEAD)) {


            if (dto.getLanguageIds() == null || dto.getLanguageIds().isEmpty()) {
                return messageUtil.getMessage("user.language.required");
            }

            if (dto.getProjectIds() == null || dto.getProjectIds().isEmpty()) {
                return messageUtil.getMessage("user.project.required");
            }
        }

        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            return messageUtil.getMessage("user.email.exists");
        }

        boolean mobileExists = userRepository.findAll()
                .stream()
                .anyMatch(u -> u.getMobile().equals(dto.getMobile()));
        if (mobileExists) {
            return messageUtil.getMessage("user.mobile.exists");
        }
        return null;
    }


    public String validateUpdateUser(UpdateUserDTO dto, User existingUser) {

        if (dto.getFullName() != null && dto.getFullName().isBlank()) {
            return messageUtil.getMessage("user.fullname.required");
        }
        if (!dto.getFullName().matches("^[A-Za-z ]+$")) {
            return messageUtil.getMessage("user.fullname.invalidCharacters");
        }
        if (dto.getMobile() != null && !MOBILE_PATTERN.matcher(dto.getMobile()).matches()) {
            return messageUtil.getMessage("user.mobile.invalid");
        }

        if (dto.getPassword() != null) {
            if (!PASSWORD_PATTERN.matcher(dto.getPassword()).matches()) {
                return messageUtil.getMessage("user.password.weak");
            }
            if (dto.getNewPassword() == null || dto.getPassword().equals(dto.getNewPassword())) {
                return messageUtil.getMessage("user.passwords.didnt.change");
            }
        }

        if (dto.getRole() != null) {
            try {
                Role.valueOf(dto.getRole().name());
            } catch (IllegalArgumentException e) {
                return messageUtil.getMessage("user.role.invalid");
            }
        }

        if ((dto.getLanguageIds() != null || dto.getProjectIds() != null) &&
                !(dto.getRole() == Role.SALESMAN || dto.getRole() == Role.TEAM_LEAD)) {
            return messageUtil.getMessage("user.languages.projects.not.allowed.for.role");
        }

        if ((existingUser.getRole() == Role.SALESMAN || existingUser.getRole() == Role.TEAM_LEAD)) {
            if (dto.getLanguageIds() != null && dto.getLanguageIds().isEmpty()) {
                return messageUtil.getMessage("user.language.required");
            }
            if (dto.getProjectIds() != null && dto.getProjectIds().isEmpty()) {
                return messageUtil.getMessage("user.project.required");
            }
        }

        return null;
    }

}


