package com.spring2go.bookmarker.utils;

import com.spring2go.bookmarker.config.security.SecurityUser;
import com.spring2go.bookmarker.constant.Constants;
import com.spring2go.bookmarker.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {
    public static User loginUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof SecurityUser) {
            SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
            return securityUser.getUser();
        }

        return null;
    }

    public static boolean isCurrentAdmin() {
        return loginUser().getRoleNames().contains(Constants.ROLE_ADMIN);
    }
}
