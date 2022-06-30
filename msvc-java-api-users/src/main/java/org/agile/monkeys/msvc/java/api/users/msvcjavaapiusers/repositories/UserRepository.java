package org.agile.monkeys.msvc.java.api.users.msvcjavaapiusers.repositories;

import org.agile.monkeys.msvc.java.api.users.msvcjavaapiusers.models.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
