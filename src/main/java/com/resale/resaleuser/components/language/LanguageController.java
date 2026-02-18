package com.resale.resaleuser.components.language;

import com.resale.resaleuser.components.language.dto.LanguageDTO;
import com.resale.resaleuser.logging.LogActivity;
import com.resale.resaleuser.model.ActionType;
import com.resale.resaleuser.utils.ReturnObject;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/language")
@RequiredArgsConstructor
public class LanguageController {

    @Autowired
    LanguageService languageService;

    @GetMapping
    @LogActivity(ActionType.GET_LANGUAGES)
    public ResponseEntity<ReturnObject<List<LanguageDTO>>> getAllLanguages() {
        ReturnObject<List<LanguageDTO>>result = languageService.getAllLanguages();

        if(!result.getStatus() || result.getData().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }
        return ResponseEntity.ok(result);
    }
}


