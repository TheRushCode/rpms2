package com.kiranacademy.rpms.controller;

import com.kiranacademy.rpms.entity.User;
import com.kiranacademy.rpms.repository.UserRepository;
import com.kiranacademy.rpms.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public abstract class BaseController {

    @Autowired
    protected UserRepository userRepository;

    protected User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserPrincipal principal) {
            return userRepository.findById(principal.getUserId()).orElse(null);
        }
        return null;
    }
}
