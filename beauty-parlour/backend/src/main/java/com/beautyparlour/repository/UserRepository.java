package com.beautyparlour.repository;

import com.beautyparlour.model.entity.User;
import com.beautyparlour.model.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findByRoleAndIsActiveTrue(Role role);
}
