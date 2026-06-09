package com.example.demoEs.service.impl;

import com.example.demoEs.es.repository.db.PropertyRepository;
import com.example.demoEs.model.Property;
import com.example.demoEs.service.PropertyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service implementation quản lý CRUD cho {@link Property} entities trong database.
 *
 * @see PropertyService
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PropertyServiceImpl implements PropertyService {

  private final PropertyRepository propertyRepository;

  /**
   * Lưu hoặc cập nhật một Property entity.
   *
   * @param property entity cần lưu
   * @return entity đã được lưu với ID được assign
   */
  @Override
  public Property save(Property property) {
    return propertyRepository.save(property);
  }

  /**
   * Xóa nhiều Property entities theo danh sách IDs.
   *
   * <p>Sử dụng {@code findAllById} thay vì lặp {@code findById} để tránh N+1 query.
   *
   * @param propertyIds danh sách IDs cần xóa
   */
  @Override
  @Transactional
  public void deletes(List<String> propertyIds) {
    List<Property> properties = propertyRepository.findAllById(propertyIds);
    propertyRepository.deleteAll(properties);
  }
}
