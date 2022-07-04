package org.agile.monkeys.java.api.controllers;

import org.agile.monkeys.java.api.models.dto.CustomerCreateRequest;
import org.agile.monkeys.java.api.services.CustomerService;
import org.agile.monkeys.java.api.models.dto.ApiResponse;
import org.agile.monkeys.java.api.models.dto.CustomerUpdateRequest;
import org.agile.monkeys.java.api.models.entity.Customer;
import org.agile.monkeys.java.api.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class CustomerController {

    @Autowired
    private CustomerService service;

    @Autowired
    private UserService userService;

    @GetMapping("/customers")
    public List<Customer> getAll(){
        return service.findAll();
    }

    @GetMapping("/customer/{id}")
    public ResponseEntity<?> getCustomer(@PathVariable Long id){
        Optional<Customer> customer = service.findById(id);
        if(!customer.isEmpty()){
            return ResponseEntity.ok(customer.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(false, "Customer not found!"));
    }

    @PostMapping("/customer")
    public ResponseEntity<?> createCustomer(@RequestParam("file") Optional<MultipartFile> file,
                                            @Valid @ModelAttribute CustomerCreateRequest customer, BindingResult result,
                                            Principal principal){
        if(result.hasErrors()){
            return validateRequest(result);
        }
        try{
            Long userId = userService.findByEmail(principal.getName()).get().getId();
            Customer newCustomer = new Customer();
            if(file.isPresent())
                newCustomer.setPhotoUrl(service.savePhoto(file.get()));
            newCustomer.setName(customer.getName());
            newCustomer.setSurname(customer.getSurname());
            newCustomer.setCreatedBy(userId);
            newCustomer.setUpdatedBy(userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(service.save(newCustomer));
        }
        catch (Exception e){
            // maybe put sentry here
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Exception "+e.getMessage()));
        }
    }

    @PutMapping("/customer/{id}")
    public ResponseEntity<?> updateUser(Principal principal, @RequestParam("file") Optional<MultipartFile> file,
                                        @Valid @ModelAttribute CustomerUpdateRequest customer, BindingResult result,
                                        @PathVariable Long id){
        if(result.hasErrors()){
            return validateRequest(result);
        }
        try{
            Optional<Customer> c = service.findById(id);
            Long userId = userService.findByEmail(principal.getName()).get().getId();
            if(c.isPresent()){
                Customer dbCustomer = c.get();
                dbCustomer.setName(customer.getName() != null ? customer.getName() : dbCustomer.getName());
                dbCustomer.setSurname(customer.getSurname() != null ? customer.getSurname() : dbCustomer.getSurname());
                if(file.isPresent())
                    dbCustomer.setPhotoUrl(service.savePhoto(file.get()));
                dbCustomer.setUpdatedBy(userId);
                return ResponseEntity.status(HttpStatus.CREATED).body(service.save(dbCustomer));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(false, "User not found!"));
        }
        catch (Exception e){
            // maybe put sentry here
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Exception "+e.getMessage()));
        }
    }

    @DeleteMapping("/customer/{id}")
    public ResponseEntity<?> deleteCustomer(@PathVariable Long id){
        Optional<Customer> c = service.findById(id);
        if(c.isPresent()){
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

