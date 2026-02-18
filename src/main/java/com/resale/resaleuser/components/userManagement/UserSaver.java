package com.resale.resaleuser.components.userManagement;

import com.resale.resaleuser.model.User;
import com.resale.resaleuser.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserSaver {

    private final UserRepository userRepository;

    /**
     * Save a user to the database.
     * Always returns the saved instance.
     */
    public User save(User user) {
        return userRepository.save(user);
    }
}

