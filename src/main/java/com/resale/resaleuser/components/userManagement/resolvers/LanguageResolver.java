package com.resale.resaleuser.components.userManagement.resolvers;

import com.resale.resaleuser.components.language.dto.LanguageDTO;
import com.resale.resaleuser.model.User;
import com.resale.resaleuser.model.UserLanguage;
import com.resale.resaleuser.repository.LanguageRepository;
import com.resale.resaleuser.repository.UserLanguageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class LanguageResolver {

    private final UserLanguageRepository userLanguageRepository;
    private final LanguageRepository languageRepository;

    public Set<LanguageDTO> resolve(User user, Locale locale) {

        Set<Integer> languageIds =
                userLanguageRepository.findByUserId(user.getId())
                        .stream()
                        .map(UserLanguage::getLanguageId)
                        .collect(Collectors.toSet());

        return languageRepository.findAllById(languageIds)
                .stream()
                .map(lang -> {
                    String name = locale.getLanguage().equalsIgnoreCase("ar")
                            ? lang.getNameAr()
                            : lang.getNameEn();

                    return new LanguageDTO(
                            lang.getId(),
                            name,
                            lang.getNameAr()
                    );
                })
                .collect(Collectors.toSet());
    }
}


