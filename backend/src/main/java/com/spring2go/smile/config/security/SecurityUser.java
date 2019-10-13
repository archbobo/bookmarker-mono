package com.spring2go.bookmarker.config.security;

import com.spring2go.bookmarker.model.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.stream.Collectors;

public class SecurityUser extends org.springframework.security.core.userdetails.User {

    @Getter
    private User user;

    public SecurityUser(User user) {
        super(user.getEmail(), user.getPassword(), user.getRoleNames().stream().map(roleName -> {
            GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(roleName);
            return grantedAuthority;
        }).collect(Collectors.toList()));
        this.user = user;
    }
}
