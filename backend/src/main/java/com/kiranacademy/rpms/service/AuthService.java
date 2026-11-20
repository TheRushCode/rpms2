package com.kiranacademy.rpms.service;

import com.kiranacademy.rpms.dto.auth.LoginRequest;
import com.kiranacademy.rpms.dto.auth.LoginResponse;
import com.kiranacademy.rpms.dto.auth.RegisterStaffRequest;
import com.kiranacademy.rpms.entity.Role;
import com.kiranacademy.rpms.entity.User;
import com.kiranacademy.rpms.exception.BadRequestException;
import com.kiranacademy.rpms.repository.RoleRepository;
import com.kiranacademy.rpms.repository.UserRepository;
import com.kiranacademy.rpms.security.JwtTokenProvider;
import com.kiranacademy.rpms.security.UserPrincipal;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final ActivityLogService logService;

    public AuthService(AuthenticationManager authenticationManager,
                       UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider tokenProvider,
                       ActivityLogService logService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.logService = logService;
    }

    @Transactional
    public LoginResponse login(LoginRequest req) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        User user = userRepository.findById(principal.getUserId()).orElse(null);
        if (user != null) {
            user.setLastLoginAt(OffsetDateTime.now());
            userRepository.save(user);
            logService.log(user, "USER", "LOGIN", String.valueOf(user.getUserId()), "User logged in: " + user.getUsername());
        }

        return new LoginResponse(
                jwt,
                principal.getUserId(),
                principal.getUsername(),
                principal.getFullName(),
                principal.getEmail(),
                principal.getRoleCode()
        );
    }

    @Transactional
    public User registerStaff(RegisterStaffRequest req, User currentUser) {
        if (userRepository.existsByUsername(req.getUsername())) {
            throw new BadRequestException("Username is already taken");
        }

        if (userRepository.existsByEmail(req.getEmail())) {
            throw new BadRequestException("Email is already registered");
        }

        Role role = roleRepository.findByRoleCode(req.getRoleCode().toUpperCase())
                .orElseThrow(() -> new BadRequestException("Invalid role code: " + req.getRoleCode()));

        User user = new User();
        user.setUsername(req.getUsername());
        user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        user.setFullName(req.getFullName());
        user.setEmail(req.getEmail());
        user.setRole(role);
        user.setStatus("ACTIVE");

        User saved = userRepository.save(user);
        logService.log(currentUser, "USER", "REGISTER", String.valueOf(saved.getUserId()),
                "Created staff account: " + saved.getUsername() + " (" + role.getRoleCode() + ")");

        return saved;
    }
}
