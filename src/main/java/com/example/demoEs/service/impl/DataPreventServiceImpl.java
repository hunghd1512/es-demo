package com.example.demoEs.service.impl;

import com.example.demoEs.es.repository.db.DataPreventRepository;
import com.example.demoEs.model.DataPrevent;
import com.example.demoEs.service.DataPreventService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service implementation quản lý CRUD cho {@link DataPrevent} entities trong database.
 *
 * @see DataPreventService
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DataPreventServiceImpl implements DataPreventService {

  private final DataPreventRepository dataPreventRepository;

  /**
   * Lưu hoặc cập nhật một DataPrevent entity.
   *
   * @param dataPrevent entity cần lưu
   * @return entity đã được lưu với ID được assign
   */
  @Override
  @Transactional
  public DataPrevent save(DataPrevent dataPrevent) {
    return dataPreventRepository.save(dataPrevent);
  }

  /**
   * Xóa nhiều DataPrevent entities theo danh sách IDs.
   *
   * <p>Sử dụng {@code findAllById} thay vì lặp {@code findById} để tránh N+1 query.
   *
   * @param dataPreventIds danh sách IDs cần xóa
   */
  @Override
  @Transactional
  public void deletes(List<String> dataPreventIds) {
    List<DataPrevent> dataPrevents = dataPreventRepository.findAllById(dataPreventIds);
    dataPreventRepository.deleteAll(dataPrevents);
  }
}
