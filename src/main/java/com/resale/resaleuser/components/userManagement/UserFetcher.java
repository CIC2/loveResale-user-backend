package com.resale.resaleuser.components.userManagement;

import com.resale.resaleuser.components.userManagement.dto.UserItemsDTO;
import com.resale.resaleuser.model.User;
import com.resale.resaleuser.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserFetcher {

    private final UserRepository userRepository;
    public Optional<User> findUser(Integer id) {
        return userRepository.findById(id);
    }

    public Set<UserItemsDTO> findTeamMembersByLeaderId(Integer leaderId) {
        return userRepository.findAllByUserId(leaderId).stream()
                .map(u -> new UserItemsDTO(u.getId(), u.getFullName()))
                .collect(Collectors.toSet());
    }
    public List<User> findAllUsers(){
     return userRepository.findAll();
    }
    public Optional<User> findUserByEmail(String email){
     return userRepository.findByEmail(email);
    }
}


