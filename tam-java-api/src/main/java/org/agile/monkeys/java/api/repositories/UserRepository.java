package org.agile.monkeys.java.api.repositories;

import org.agile.monkeys.java.api.models.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
