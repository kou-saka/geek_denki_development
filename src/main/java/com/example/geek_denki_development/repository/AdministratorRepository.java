package com.example.geek_denki_development.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.geek_denki_development.entity.Administrator;

public interface AdministratorRepository extends JpaRepository<Administrator, Long> {
    Optional<Administrator> findByEmail(String email);
}


