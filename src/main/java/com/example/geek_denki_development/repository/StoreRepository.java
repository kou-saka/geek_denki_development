// StoreRepository.java
package com.example.geek_denki_development.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.geek_denki_development.entity.Store;

public interface StoreRepository extends JpaRepository<Store, Long> {
}
