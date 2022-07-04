package org.agile.monkeys.java.api.controllers;

import org.agile.monkeys.java.api.models.entity.User;
import org.agile.monkeys.java.api.services.CustomUserDetailsService;
import org.agile.monkeys.java.api.models.dto.ApiResponse;
import org.agile.monkeys.java.api.models.dto.UserRequest;
import org.agile.monkeys.java.api.services.CustomerService;
import org.agile.monkeys.java.api.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
public class UserController {

    @Autowired
    private UserService service;
    @Autowired
    private CustomerService customerService;
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
        Optional<User> u = service.findById(id);
        if(!u.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(false, "User not found!"));
        }
        Optional<User> found = service.findByEmail(user.getEmail());
        if(found.isPresent() && id != found.get().getId()) {
            result.rejectValue("email", "error.user", "An account already exists for this email.");
        }
        if(result.hasErrors()){
            return validateRequest(result);
        }
        User dbUser = u.get();
        dbUser.setPassword(passwordEncoder.encode(user.getPassword()));
        dbUser.setEmail(user.getEmail());
        dbUser.setIsAdmin(user.getIsAdmin() == null ? false : user.getIsAdmin());
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(dbUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id, Principal principal){
        if(!service.findByEmail(principal.getName()).get().getIsAdmin()){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponse(false, "Missing admin credentials"));
        }
        Optional<User> u = service.findById(id);
        if(!u.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(false, "User not found!"));
        }
        service.delete(id);
        //Don't want to reference a user that doesn't exist
        customerService.nullifyUser(id);
        return ResponseEntity.ok(new ApiResponse(true, "User deleted!"));
    }

    private ResponseEntity<Map<String, String>> validateRequest(BindingResult result) {
        Map<String, String> errors = new HashMap<>();
        result.getFieldErrors().forEach(err -> {
            errors.put(err.getField(), "Field "+err.getField()+" "+err.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errors);
    }
}
