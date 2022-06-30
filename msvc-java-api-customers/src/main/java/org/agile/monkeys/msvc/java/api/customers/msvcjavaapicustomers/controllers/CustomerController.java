package org.agile.monkeys.msvc.java.api.customers.msvcjavaapicustomers.controllers;

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
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file){
        logger.error("file",file.getOriginalFilename());
        try{
            file.transferTo(new File("C:\\Users\\elbeb\\Documents\\Among.Us.v2020.9.1s\\test.JPG"));
        }
        catch (Exception e){
            logger.warn("Photo not saved "+e.getCause());
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping
    public ResponseEntity<?> createCustomer(@RequestParam("file") MultipartFile file,
                                            @Valid @ModelAttribute Customer customer, BindingResult result){
        if(result.hasErrors()){
            return validateRequest(result);
        }
        try{

            service.savePhoto(file);
        }
        catch (Exception e){
            logger.warn("Photo not saved "+e.getCause());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(customer));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@Valid @ModelAttribute Customer customer, BindingResult result, @PathVariable Long id){
        if(result.hasErrors()){
            return validateRequest(result);
        }
        Optional<Customer> c = service.findById(id);
        if(c.isPresent()){
            Customer dbCustomer = c.get();
            dbCustomer.setName(customer.getName());
            dbCustomer.setSurname(customer.getSurname());
            dbCustomer.setPhotoUrl(customer.getPhotoUrl());
            return ResponseEntity.status(HttpStatus.CREATED).body(service.save(dbCustomer));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCustomer(@PathVariable Long id){
        Optional<Customer> c = service.findById(id);
        if(c.isPresent()){
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
