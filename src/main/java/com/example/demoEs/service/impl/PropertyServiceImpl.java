package com.example.demoEs.service.impl;

import com.example.demoEs.es.repository.db.PropertyRepository;
import com.example.demoEs.model.Property;
import com.example.demoEs.service.PropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PropertyServiceImpl implements PropertyService {
    private final PropertyRepository propertyRepository;

    @Override
    public Property save(Property property) {
        return propertyRepository.save(property);
    }

    @Override
    public void deletes(List<String> propertyIds) {
        List<Property> properties = new ArrayList<>();
        for (String propertyId : propertyIds) {
            Optional<Property> property = propertyRepository.findById(propertyId);
            property.ifPresent(properties::add);
        }
        propertyRepository.deleteAll(properties);
    }
}
