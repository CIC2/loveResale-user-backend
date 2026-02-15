package com.resale.homeflyuser.components.userManagement.updaters;

import com.resale.homeflyuser.components.userManagement.dto.userProfile.CreateUserDTO;
import com.resale.homeflyuser.components.userManagement.dto.userProfile.UpdateUserDTO;
import com.resale.homeflyuser.model.Role;
import com.resale.homeflyuser.model.User;
import com.resale.homeflyuser.model.UserLanguage;
import com.resale.homeflyuser.repository.UserLanguageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AdminUserLanguageUpdater {

    private final UserLanguageRepository userLanguageRepository;

    public Set<Integer> update(User user, UpdateUserDTO dto) {
        return updateLanguages(user, dto.getLanguageIds());
    }

    /**
     * Update user languages from CreateUserDTO and return updated language IDs
     */
    public Set<Integer> update(User user, CreateUserDTO dto) {
        return updateLanguages(user, dto.getLanguageIds());
    }

    /**
     * Internal method to handle both DTOs
     */
    private Set<Integer> updateLanguages(User user, Set<Integer> languageIds) {
        if (languageIds == null || languageIds.isEmpty()) {
            return Collections.emptySet();
        }

        if (user.getRole() != Role.SALESMAN && user.getRole() != Role.TEAM_LEAD) {
            return Collections.emptySet();
        }

        // Delete old languages
        userLanguageRepository.deleteByUserId(user.getId());

        // Save new languages
        List<UserLanguage> userLanguages = languageIds.stream()
                .map(langId -> {
                    UserLanguage ul = new UserLanguage();
                    ul.setUserId(user.getId());
                    ul.setLanguageId(langId);
                    return ul;
                })
                .toList();
        userLanguageRepository.saveAll(userLanguages);

        // Return updated language IDs
        return userLanguages.stream()
                .map(UserLanguage::getLanguageId)
                .collect(Collectors.toSet());
    }
}


