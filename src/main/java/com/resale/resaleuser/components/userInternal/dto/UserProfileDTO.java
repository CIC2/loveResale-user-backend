package com.resale.resaleuser.components.userInternal.dto;

import com.resale.resaleuser.components.language.dto.LanguageDTO;
import lombok.*;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileDTO {
    private Integer userId;
    private String name;
    private Integer teamLeadId;
    private List<LanguageDTO> languages;
    private List<ProjectDTO> projects;
    private List<Integer> modelIds;
}


