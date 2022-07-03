package org.agile.monkeys.java.api.services;

import org.agile.monkeys.java.api.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<org.agile.monkeys.java.api.models.entity.User> user = repository.findByEmail(email);
        if (!user.isPresent()) {
            throw new UsernameNotFoundException("email Not found" + email);
        }
        return new User(user.get().getEmail(), user.get().getPassword(), new ArrayList<>());
    }
}
