package org.agile.monkeys.msvc.java.api.users.msvcjavaapiusers.controllers;

import org.agile.monkeys.msvc.java.api.users.msvcjavaapiusers.models.dto.ApiResponse;
import org.agile.monkeys.msvc.java.api.users.msvcjavaapiusers.models.dto.AuthenticationResponse;
import org.agile.monkeys.msvc.java.api.users.msvcjavaapiusers.models.dto.LoginRequest;
import org.agile.monkeys.msvc.java.api.users.msvcjavaapiusers.models.dto.SignUpRequest;
import org.agile.monkeys.msvc.java.api.users.msvcjavaapiusers.models.entity.User;
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
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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
    @Autowired
    JwtUtil jwtUtil;

    @GetMapping("/")
    public List<User> getAll(){
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id){
        Optional<User> user = service.findById(id);
        if(!user.isEmpty()){
            return ResponseEntity.ok(user.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("")
    public ResponseEntity<?> createUser(@Valid @RequestBody User user, BindingResult result) {
        if(service.findByEmail(user.getEmail()).isPresent()) {
            result.rejectValue("email", "error.user", "An account already exists for this email.");
        }
        if(result.hasErrors()){
            return validateRequest(result);
        }
        User jwtUser = new User();
        jwtUser.setEmail(user.getEmail());
        jwtUser.setPassword(passwordEncoder.encode(user.getPassword()));
        jwtUser.setIsAdmin(jwtUser.getIsAdmin());
        return ResponseEntity.ok(service.save(jwtUser));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@Valid @RequestBody User user, BindingResult result, @PathVariable Long id){
        if(result.hasErrors()){
            return validateRequest(result);
        }
        Optional<User> u = service.findById(id);
        if(u.isPresent()){
            User dbUser = u.get();
            dbUser.setPassword(user.getPassword());
            dbUser.setEmail(user.getEmail());
            dbUser.setIsAdmin(user.getIsAdmin());
            return ResponseEntity.status(HttpStatus.CREATED).body(service.save(dbUser));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id){
        Optional<User> u = service.findById(id);
        if(u.isPresent()){
            service.delete(id);
            return ResponseEntity.noContent().build();
        }
        return  ResponseEntity.notFound().build();
    }

    private ResponseEntity<Map<String, String>> validateRequest(BindingResult result) {
        Map<String, String> errors = new HashMap<>();
        result.getFieldErrors().forEach(err -> {
            errors.put(err.getField(), "Field "+err.getField()+" "+err.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errors);
    }
}
