package org.agile.monkeys.msvc.java.api.users.msvcjavaapiusers.controllers;


import org.agile.monkeys.msvc.java.api.users.msvcjavaapiusers.models.dto.*;
import org.agile.monkeys.msvc.java.api.users.msvcjavaapiusers.models.entity.User;
import org.agile.monkeys.msvc.java.api.users.msvcjavaapiusers.repositories.UserRepository;
import org.agile.monkeys.msvc.java.api.users.msvcjavaapiusers.services.CustomUserDetailsService;
import org.agile.monkeys.msvc.java.api.users.msvcjavaapiusers.services.UserService;
import org.agile.monkeys.msvc.java.api.users.msvcjavaapiusers.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.Collections;

@RestController
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    CustomUserDetailsService userDetailsService;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    private UserService service;


    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody UserRequest user) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getEmail(),
                        user.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());

        final String jwt = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> createUser() {
        if(service.findByEmail("admin@email.com").isPresent()) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Admin admin@email.com already exists!"));
        }
        User newUser = new User();
        newUser.setEmail("admin@email.com");
        newUser.setPassword(passwordEncoder.encode("password"));
        newUser.setIsAdmin(true);
        return ResponseEntity.ok(service.save(newUser));
    }
}