package com.resale.homeflyuser.components.userManagement.resolvers;

import com.resale.homeflyuser.components.userInternal.dto.ProjectDTO;
import com.resale.homeflyuser.model.User;
import com.resale.homeflyuser.model.UserProject;
import com.resale.homeflyuser.repository.ProjectRepository;
import com.resale.homeflyuser.repository.UserProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProjectResolver {

    private final UserProjectRepository userProjectRepository;
    private final ProjectRepository projectRepository;

    public List<ProjectDTO> resolve(User user, Locale locale) {

        Set<Integer> projectIds =
                userProjectRepository.findByUserId(user.getId())
                        .stream()
                        .map(UserProject::getProjectId)
                        .collect(Collectors.toSet());

        return projectRepository.findAllById(projectIds)
                .stream()
                .map(project -> {
                    String name = locale.getLanguage().equalsIgnoreCase("ar")
                            ? project.getNameAr()
                            : project.getNameEn();

                    return new ProjectDTO(
                            project.getId(),
                            name,
                            project.getNameAr()
                    );
                })
                .toList();
    }
}


