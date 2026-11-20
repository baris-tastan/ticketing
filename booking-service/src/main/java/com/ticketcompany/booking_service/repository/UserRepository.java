package com.ticketcompany.booking_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ticketcompany.booking_service.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}