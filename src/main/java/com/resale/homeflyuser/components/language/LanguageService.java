package com.resale.homeflyuser.components.language;

import com.resale.homeflyuser.repository.LanguageRepository;
import com.resale.homeflyuser.components.language.dto.LanguageDTO;
import com.resale.homeflyuser.model.Language;
import com.resale.homeflyuser.utils.MessageUtil;
import com.resale.homeflyuser.utils.ReturnObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
@Service
public class LanguageService {

    @Autowired
    LanguageRepository languageRepository;

    @Autowired
    MessageUtil messageUtil;

    public ReturnObject<List<LanguageDTO>> getAllLanguages() {
        ReturnObject<List<LanguageDTO>> response = new ReturnObject<>();
        Locale locale = messageUtil.getCurrentLocale();
        boolean isArabic = "ar".equalsIgnoreCase(locale.getLanguage());
        try {
            List<Language> languages = languageRepository.findAll();

            if (languages.isEmpty()) {
                response.setStatus(false);
                response.setMessage(messageUtil.getMessage("no.languages"));
                response.setData(null);
                return response;
            }

            List<LanguageDTO> dtoList = languages.stream()
                    .map(lang -> new LanguageDTO(lang.getId(), lang.getNameEn(), lang.getNameAr()))
                    .collect(Collectors.toList());

            dtoList.stream().forEach(dto -> {
                dto.setName(isArabic ? dto.getNameAr() : dto.getName());
                dto.setNameAr(null);
            });
            response.setStatus(true);
            response.setMessage(messageUtil.getMessage("get.languages.success"));
            response.setData(dtoList);

        } catch (Exception e) {
            response.setStatus(false);
            response.setMessage(messageUtil.getMessage("language.fetch.error") + e.getMessage());
            response.setData(null);
        }
        return response;
    }

}


