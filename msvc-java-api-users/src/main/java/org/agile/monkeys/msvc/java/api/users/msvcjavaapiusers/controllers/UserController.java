package org.agile.monkeys.msvc.java.api.users.msvcjavaapiusers.controllers;

import org.agile.monkeys.msvc.java.api.users.msvcjavaapiusers.models.dto.ApiResponse;
import org.agile.monkeys.msvc.java.api.users.msvcjavaapiusers.models.dto.UserRequest;
import org.agile.monkeys.msvc.java.api.users.msvcjavaapiusers.models.entity.User;
import org.agile.monkeys.msvc.java.api.users.msvcjavaapiusers.services.CustomUserDetailsService;
import org.agile.monkeys.msvc.java.api.users.msvcjavaapiusers.services.UserService;
import org.agile.monkeys.msvc.java.api.users.msvcjavaapiusers.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class UserController {

    @Autowired
    private UserService service;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private CustomUserDetailsService userDetailsService;
    @Autowired
    private AuthenticationManager authenticationManager;

    @GetMapping("/")
    public ResponseEntity<?> getAll(Principal principal){
        if(!service.findByEmail(principal.getName()).get().getIsAdmin()){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponse(false, "Missing admin credentials"));
        }
        return ResponseEntity.ok(service.findAll());
    }


    @PostMapping("")
    public ResponseEntity<?> createUser(@Valid @RequestBody UserRequest user, BindingResult result, Principal principal) {
        if(!service.findByEmail(principal.getName()).get().getIsAdmin()){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponse(false, "Missing admin credentials"));
        }
        if(service.findByEmail(user.getEmail()).isPresent()) {
            result.rejectValue("email", "error.user", "An account already exists for this email.");
        }
        if(result.hasErrors()){
            return validateRequest(result);
        }
        User newUser = new User();
        newUser.setEmail(user.getEmail());
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));
        newUser.setIsAdmin(user.getIsAdmin() == null ? false : user.getIsAdmin());
        return ResponseEntity.ok(service.save(newUser));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@Valid @RequestBody UserRequest user, BindingResult result, @PathVariable Long id, Principal principal){
        if(!service.findByEmail(principal.getName()).get().getIsAdmin()){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponse(false, "Missing admin credentials"));
        }
        Optional<User> found = service.findByEmail(user.getEmail());
        if(found.isPresent() && id != found.get().getId()) {
            result.rejectValue("email", "error.user", "An account already exists for this email.");
        }
        if(result.hasErrors()){
            return validateRequest(result);
        }
        Optional<User> u = service.findById(id);
        if(u.isPresent()){
            User dbUser = u.get();
            dbUser.setPassword(passwordEncoder.encode(user.getPassword()));
            dbUser.setEmail(user.getEmail());
            dbUser.setIsAdmin(user.getIsAdmin() == null ? false : user.getIsAdmin());
            return ResponseEntity.status(HttpStatus.CREATED).body(service.save(dbUser));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(false, "User not found!"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id, Principal principal){
        if(!service.findByEmail(principal.getName()).get().getIsAdmin()){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponse(false, "Missing admin credentials"));
        }
        Optional<User> u = service.findById(id);
        if(u.isPresent()){
            service.delete(id);
            return ResponseEntity.ok(new ApiResponse(true, "User deleted!"));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(false, "User not found!"));
    }

    private ResponseEntity<Map<String, String>> validateRequest(BindingResult result) {
        Map<String, String> errors = new HashMap<>();
        result.getFieldErrors().forEach(err -> {
            errors.put(err.getField(), "Field "+err.getField()+" "+err.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errors);
    }
}
