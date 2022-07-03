package org.agile.monkeys.java.api.repositories;

import org.agile.monkeys.java.api.models.entity.Customer;
import org.springframework.data.repository.CrudRepository;

public interface CustomerRepository extends CrudRepository<Customer, Long> {
}