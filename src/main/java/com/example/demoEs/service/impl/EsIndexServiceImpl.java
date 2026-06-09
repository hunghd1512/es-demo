package com.example.demoEs.service.impl;

import com.example.demoEs.constant.PropertyType;
import com.example.demoEs.exception.BusinessException;
import com.example.demoEs.es.buildStr.DataPreventBuildStr;
import com.example.demoEs.es.document.DataPreventES;
import com.example.demoEs.es.document.OrganizationDataEntryES;
import com.example.demoEs.es.document.PersonalDataEntryES;
import com.example.demoEs.es.document.PreventES;
import com.example.demoEs.es.repository.es.PreventESRepository;
import com.example.demoEs.model.DataPrevent;
import com.example.demoEs.model.Property;
import com.example.demoEs.service.DataPreventService;
import com.example.demoEs.service.EsInexService;
import com.example.demoEs.service.PropertyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service implementation xử lý đồng bộ dữ liệu Prevent lên Elasticsearch.
 *
 * <p>Flow xử lý index một document Prevent:
 *
 * <ol>
 *   <li>Lưu {@link DataPrevent} vào database trước
 *   <li>Xử lý danh sách tổ chức: tạo {@link Property} + index lên ES
 *   <li>Xử lý danh sách cá nhân: tạo {@link Property} + index lên ES
 *   <li>Cập nhật UUIDs đã được assign vào document gốc
 *   <li>Lưu document {@link PreventES} hoàn chỉnh vào Elasticsearch
 * </ol>
 *
 * @see EsInexService
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EsIndexServiceImpl implements EsInexService {

  private final DataPreventService dataPreventService;
  private final PropertyService propertyService;
  private final PreventESRepository preventESRepository;

  /**
   * Index một document Prevent đầy đủ lên Elasticsearch.
   *
   * <p>Phương thức này đảm bảo dữ liệu gốc được lưu vào DB trước, sau đó mới index lên ES.
   * Nếu có lỗi ở bất kỳ bước nào, transaction không đảm bảo atomicity (cần cải thiện thêm).
   *
   * @param preventES document cần index
   */
  @Override
  public void indexOfDataPrevent(PreventES preventES) {
    log.info("Bắt đầu index Prevent document");

    // 1. Lưu dữ liệu gốc (DataPrevent) vào DB trước
    DataPrevent dataPrevent = saveDataPrevent(preventES.getDataPreventES());
    preventES.getDataPreventES().setUuid(dataPrevent.getUuid());

    // 2. Xử lý danh sách tổ chức
    List<OrganizationDataEntryES> savedOrganizations = processOrganizationEntries(preventES.getOrganizationsES());

    // 3. Xử lý danh sách cá nhân
    List<PersonalDataEntryES> savedPersonals = processPersonalEntries(preventES.getPersonalsES());

    // 4. Cập nhật lại vào preventES
    preventES.setOrganizationsES(savedOrganizations);
    preventES.setPersonalsES(savedPersonals);

    // 5. Lưu vào Elasticsearch
    preventESRepository.save(preventES);
    log.info("Index Prevent document hoàn tất");
  }

  /**
   * Lưu DataPrevent vào database.
   *
   * @param dataPreventES DTO từ ES document
   * @return entity đã được lưu
   */
  private DataPrevent saveDataPrevent(DataPreventES dataPreventES) {
    DataPrevent dataPrevent = new DataPrevent();
    try {
      BeanUtils.copyProperties(dataPreventES, dataPrevent);
      return dataPreventService.save(dataPrevent);
    } catch (Exception e) {
      throw new BusinessException("Lỗi khi lưu DataPrevent: " + e.getMessage(), e);
    }
  }

  /**
   * Xử lý danh sách tổ chức: tạo Property record + assign UUID.
   *
   * @param orgEntries danh sách tổ chức
   * @return danh sách tổ chức đã được assign UUID từ Property
   */
  private List<OrganizationDataEntryES> processOrganizationEntries(List<OrganizationDataEntryES> orgEntries) {
    if (orgEntries == null) {
      return new ArrayList<>();
    }
    List<OrganizationDataEntryES> resultList = new ArrayList<>();
    for (OrganizationDataEntryES orgEntry : orgEntries) {
      String dataString = DataPreventBuildStr.buildOrgStr(orgEntry);
      Property property = createAndSaveProperty(dataString, PropertyType.ORGANIZATION);
      if (property != null && property.getUuid() != null) {
        orgEntry.setUuid(property.getUuid());
        resultList.add(orgEntry);
      }
    }
    return resultList;
  }

  /**
   * Xử lý danh sách cá nhân: tạo Property record + assign UUID.
   *
   * @param personalEntries danh sách cá nhân
   * @return danh sách cá nhân đã được assign UUID từ Property
   */
  private List<PersonalDataEntryES> processPersonalEntries(List<PersonalDataEntryES> personalEntries) {
    if (personalEntries == null) {
      return new ArrayList<>();
    }
    List<PersonalDataEntryES> resultList = new ArrayList<>();
    for (PersonalDataEntryES personalEntry : personalEntries) {
      String dataString = DataPreventBuildStr.buildPersonStr(personalEntry);
      Property property = createAndSaveProperty(dataString, PropertyType.PERSONAL);
      if (property != null && property.getUuid() != null) {
        personalEntry.setUuid(property.getUuid());
        resultList.add(personalEntry);
      }
    }
    return resultList;
  }

  /**
   * Tạo và lưu Property record.
   *
   * @param data nội dung text của property
   * @param type loại property (ORGANIZATION/PERSONAL)
   * @return Property đã lưu, hoặc null nếu lỗi
   */
  private Property createAndSaveProperty(String data, PropertyType type) {
    Property property = new Property();
    property.setData(data);
    property.setFrom(PropertyType.PREVENT.getValue());
    property.setType(type.getValue());

    try {
      return propertyService.save(property);
    } catch (Exception e) {
      log.error("Không thể lưu Property (type={}): {}", type.getValue(), e.getMessage());
      return null;
    }
  }
}
