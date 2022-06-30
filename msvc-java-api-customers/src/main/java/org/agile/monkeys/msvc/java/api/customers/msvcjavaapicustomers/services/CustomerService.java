package org.agile.monkeys.msvc.java.api.customers.msvcjavaapicustomers.services;

import org.agile.monkeys.msvc.java.api.customers.msvcjavaapicustomers.models.entity.Customer;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface CustomerService {
    List<Customer> findAll();
    Optional<Customer> findById(Long id);
    Customer save(Customer customer);
    void delete(Long id);
    String savePhoto(MultipartFile photo);
}
