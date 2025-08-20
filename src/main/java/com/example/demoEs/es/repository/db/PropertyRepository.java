package com.example.demoEs.es.repository.db;

import com.example.demoEs.model.Property;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PropertyRepository extends JpaRepository<Property, String> {
}
