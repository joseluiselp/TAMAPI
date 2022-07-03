package org.agile.monkeys.msvc.java.api.customers.msvcjavaapicustomers.controllers;

import org.agile.monkeys.msvc.java.api.customers.msvcjavaapicustomers.models.dto.ApiResponse;
import org.agile.monkeys.msvc.java.api.customers.msvcjavaapicustomers.models.dto.CustomerUpdateRequest;
import org.agile.monkeys.msvc.java.api.customers.msvcjavaapicustomers.models.entity.Customer;
import org.agile.monkeys.msvc.java.api.customers.msvcjavaapicustomers.services.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class CustomerController {

    @Autowired
    private CustomerService service;

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    @GetMapping("/")
    public List<Customer> getAll(){
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCustomer(@PathVariable Long id){
        Optional<Customer> customer = service.findById(id);
        if(!customer.isEmpty()){
            return ResponseEntity.ok(customer.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(false, "Customer not found!"));
    }

    @PostMapping
    public ResponseEntity<?> createCustomer(@RequestParam("file") Optional<MultipartFile> file,
                                            @Valid @ModelAttribute Customer customer, BindingResult result){
        if(result.hasErrors()){
            return validateRequest(result);
        }
        try{
            Customer newCustomer = new Customer();
            if(file.isPresent())
                newCustomer.setPhotoUrl(service.savePhoto(file.get()));
            newCustomer.setName(customer.getName());
            newCustomer.setSurname(customer.getSurname());
            newCustomer.setCreatedBy(customer.getCreatedBy());
            newCustomer.setUpdatedBy(customer.getUpdatedBy());
            return ResponseEntity.status(HttpStatus.CREATED).body(service.save(newCustomer));
        }
        catch (Exception e){
            // maybe put sentry here
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Exception "+e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@RequestParam("file") Optional<MultipartFile> file,
                                        @Valid @ModelAttribute CustomerUpdateRequest customer, BindingResult result,
                                        @PathVariable Long id){
        if(result.hasErrors()){
            return validateRequest(result);
        }
        try{
            Optional<Customer> c = service.findById(id);
            if(c.isPresent()){
                Customer dbCustomer = c.get();
                dbCustomer.setName(customer.getName() != null ? customer.getName() : dbCustomer.getName());
                dbCustomer.setSurname(customer.getSurname() != null ? customer.getSurname() : dbCustomer.getSurname());
                if(file.isPresent())
                    dbCustomer.setPhotoUrl(service.savePhoto(file.get()));
                dbCustomer.setUpdatedBy(customer.getUpdatedBy());
                return ResponseEntity.status(HttpStatus.CREATED).body(service.save(dbCustomer));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(false, "User not found!"));
        }
        catch (Exception e){
            // maybe put sentry here
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Exception "+e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
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
