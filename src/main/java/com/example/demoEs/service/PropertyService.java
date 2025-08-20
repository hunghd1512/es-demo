package com.example.demoEs.service;

import com.example.demoEs.model.Property;

import java.util.List;

public interface PropertyService {
    Property save(Property property);

    void deletes(List<String> propertyIds);
}
