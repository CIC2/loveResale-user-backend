package com.resale.resaleuser.security;

import com.resale.resaleuser.model.Permission;
import com.resale.resaleuser.model.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class CustomUserPrincipal implements UserDetails {

    private final Integer id;
    private final String username;
    private final String password;
    private final Set<String> permissions;

    public CustomUserPrincipal(User user, Set<Permission> userPermissions) {
        this.id = user.getId();
        this.username = user.getEmail();
        this.password = user.getPassword();
        this.permissions = userPermissions.stream()
                .map(p -> p.getAction().toLowerCase() + ":" + p.getResource().toLowerCase())
                .collect(Collectors.toSet());
    }

    public boolean hasPermission(String action, String resource) {
        return permissions.contains(action.toLowerCase() + ":" + resource.toLowerCase());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return permissions.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}


