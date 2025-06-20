// RoleRepository.java
package com.example.geek_denki_development.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.geek_denki_development.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
