package com.resale.resaleuser.components.auth;

import com.resale.resaleuser.repository.PermissionRepository;
import com.resale.resaleuser.repository.UserRepository;
import com.resale.resaleuser.model.Permission;
import com.resale.resaleuser.model.User;
import com.resale.resaleuser.security.CustomUserPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;

    public CustomUserDetailsService(UserRepository userRepository,
                                    PermissionRepository permissionRepository) {
        this.userRepository = userRepository;
        this.permissionRepository = permissionRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Set<Permission> userPermissions = permissionRepository.findPermissionsByUserId(user.getId());

        return new CustomUserPrincipal(user, userPermissions);
    }
}


