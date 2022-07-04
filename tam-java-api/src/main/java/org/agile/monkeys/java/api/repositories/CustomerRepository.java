package org.agile.monkeys.java.api.repositories;

import org.agile.monkeys.java.api.models.entity.Customer;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface CustomerRepository extends CrudRepository<Customer, Long> {
    @Modifying
    @Query("UPDATE Customer SET created_by = null WHERE created_by = ?1")
    void nullifyCreatedUser(Long id);

    @Modifying
    @Query("UPDATE Customer SET updated_by = null WHERE updated_by = ?1")
    void nullifyUpdatedUser(Long id);
}