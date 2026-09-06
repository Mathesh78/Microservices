package com.example.auth_service.repository;

import com.example.auth_service.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<AppUser, Integer> {
    Optional<AppUser> findByUsername(String username);
    boolean existsByUsername(String username);
}
