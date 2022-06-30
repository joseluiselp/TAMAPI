package org.agile.monkeys.msvc.java.api.customers.msvcjavaapicustomers.repositories;

import org.agile.monkeys.msvc.java.api.customers.msvcjavaapicustomers.models.entity.Customer;
import org.springframework.data.repository.CrudRepository;

public interface CustomerRepository extends CrudRepository<Customer, Long> {
}