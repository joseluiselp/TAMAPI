package org.agile.monkeys.java.api.services;

import org.agile.monkeys.java.api.models.entity.Customer;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface CustomerService {
    List<Customer> findAll();
    Optional<Customer> findById(Long id);
    Customer save(Customer customer);
    void delete(Long id);
    void nullifyUser(Long id);
    String savePhoto(MultipartFile photo);
}
