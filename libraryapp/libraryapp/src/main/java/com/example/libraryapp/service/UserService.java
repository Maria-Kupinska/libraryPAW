package com.example.libraryapp.service;

import com.example.libraryapp.entity.Role;
import com.example.libraryapp.entity.User;
import com.example.libraryapp.repository.RoleRepository;
import com.example.libraryapp.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final BCryptPasswordEncoder encoder;

    public UserService(UserRepository userRepo,
                       RoleRepository roleRepo,
                       BCryptPasswordEncoder encoder) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.encoder = encoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        User user = userRepo.findByUsername(username)
            .orElseThrow(() ->
                new UsernameNotFoundException("User not found: " + username));
        var authorities = user.getRoles().stream()
            .map(r -> new SimpleGrantedAuthority(r.getName()))
            .collect(Collectors.toList());
        return new org.springframework.security.core.userdetails.User(
            user.getUsername(),
            user.getPassword(),
            user.isEnabled(),
            true, true, true,
            authorities);
    }

    @PostConstruct
    private void init() {
        if (roleRepo.count() == 0) {
            Role roleUser = roleRepo.save(new Role("ROLE_USER"));
            Role roleAdmin = roleRepo.save(new Role("ROLE_ADMIN"));

            userRepo.save(new User(
                "admin",
                encoder.encode("password"),
                true,
                Set.of(roleAdmin, roleUser)
            ));
            userRepo.save(new User(
                "user",
                encoder.encode("password"),
                true,
                Set.of(roleUser)
            ));
        }
    }
}
