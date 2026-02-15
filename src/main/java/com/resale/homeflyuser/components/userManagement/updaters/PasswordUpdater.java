package com.resale.homeflyuser.components.userManagement.updaters;

import com.resale.homeflyuser.components.userManagement.UserSaver;
import com.resale.homeflyuser.model.User;
import com.resale.homeflyuser.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PasswordUpdater {

    private final PasswordEncoder passwordEncoder;
    private final UserSaver userSaver;

    public void updatePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userSaver.save(user);
    }
}


