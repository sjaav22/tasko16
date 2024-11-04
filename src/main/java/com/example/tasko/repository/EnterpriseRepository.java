package com.example.tasko.repository;

import com.example.tasko.model.Enterprise;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnterpriseRepository extends JpaRepository<Enterprise, Long> {
    boolean existsByName(String name);
}