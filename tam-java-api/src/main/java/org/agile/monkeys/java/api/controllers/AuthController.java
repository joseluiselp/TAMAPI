package org.agile.monkeys.java.api.controllers;


import org.agile.monkeys.java.api.models.dto.ApiResponse;
import org.agile.monkeys.java.api.models.dto.AuthenticationResponse;
import org.agile.monkeys.java.api.models.dto.UserRequest;
import org.agile.monkeys.java.api.models.entity.User;
import org.agile.monkeys.java.api.services.CustomUserDetailsService;
import org.agile.monkeys.java.api.services.UserService;
import org.agile.monkeys.java.api.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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