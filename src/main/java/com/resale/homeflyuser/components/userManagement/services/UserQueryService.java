package com.resale.homeflyuser.components.userManagement.services;

import com.resale.homeflyuser.components.auth.dto.UserResponseDTO;
import com.resale.homeflyuser.components.userManagement.UserFetcher;
import com.resale.homeflyuser.components.userManagement.mappers.UserResponseMapper;
import com.resale.homeflyuser.components.userManagement.resolvers.LanguageResolver;
import com.resale.homeflyuser.components.userManagement.resolvers.OnCallResolver;
import com.resale.homeflyuser.components.userManagement.resolvers.PermissionResolver;
import com.resale.homeflyuser.components.userManagement.resolvers.ProjectResolver;
import com.resale.homeflyuser.model.User;
import com.resale.homeflyuser.utils.MessageUtil;
import com.resale.homeflyuser.utils.ReturnObject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserQueryService {

    private final UserFetcher userFetcher;
    private final PermissionResolver permissionResolver;
    private final LanguageResolver languageResolver;
    private final ProjectResolver projectResolver;
    private final OnCallResolver onCallResolver;
    private final UserResponseMapper userResponseMapper;
    private final MessageUtil messageUtil;

    public ReturnObject<UserResponseDTO> getCurrentUser(Integer id) {

        Optional<User> userOptional = userFetcher.findUser(id);

        if (userOptional.isEmpty()) {
            return new ReturnObject<>(
                    messageUtil.getMessage("cant.found.user"),
                    false,
                    null
            );
        }

        User user = userOptional.get();

        Locale locale = messageUtil.getCurrentLocale();

        return new ReturnObject<>(
                messageUtil.getMessage("users.fetched.success"),
                true,
                userResponseMapper.map(
                        user,
                        permissionResolver.resolve(user),
                        languageResolver.resolve(user, locale),
                        projectResolver.resolve(user, locale),
                        onCallResolver.resolve(user)
                )
        );
    }
}


