package com.example.geek_denki_development.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.geek_denki_development.entity.Admin;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByUsername(String username);
}
