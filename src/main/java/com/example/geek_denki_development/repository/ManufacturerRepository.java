package com.example.geek_denki_development.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.geek_denki_development.entity.Manufacturer;

public interface ManufacturerRepository extends JpaRepository<Manufacturer, Long> {
}
