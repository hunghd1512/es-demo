package com.example.demoEs.service.impl;

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

@Service
@RequiredArgsConstructor
@Slf4j
public class EsIndexServiceImpl implements EsInexService {

    private final DataPreventService dataPreventService;

    private final PropertyService propertyService;

    private final PreventESRepository preventESRepository;

    @Override
    public void indexOfDataPrevent(PreventES preventES) {
        // 1. Lưu dữ liệu gốc (DataPrevent) trước
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
    }

// --------------------- Helper Methods ---------------------

    private DataPrevent saveDataPrevent(DataPreventES dataPreventES) {
        DataPrevent dataPrevent = new DataPrevent();
        try {
            BeanUtils.copyProperties(dataPreventES, dataPrevent);
            return dataPreventService.save(dataPrevent);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lưu DataPrevent: " + e.getMessage(), e);
        }
    }

    private List<OrganizationDataEntryES> processOrganizationEntries(List<OrganizationDataEntryES> orgEntries) {
        List<OrganizationDataEntryES> resultList = new ArrayList<>();
        for (OrganizationDataEntryES orgEntry : orgEntries) {
            String dataString = DataPreventBuildStr.buildOrgStr(orgEntry);
            Property property = createAndSaveProperty(dataString, "ORGANIZATION");
            if (property != null && property.getUuid() != null) {
                orgEntry.setUuid(property.getUuid());
                resultList.add(orgEntry);
            }
        }
        return resultList;
    }

    private List<PersonalDataEntryES> processPersonalEntries(List<PersonalDataEntryES> personalEntries) {
        List<PersonalDataEntryES> resultList = new ArrayList<>();
        for (PersonalDataEntryES personalEntry : personalEntries) {
            String dataString = DataPreventBuildStr.buildPersonStr(personalEntry);
            Property property = createAndSaveProperty(dataString, "PERSONAL");
            if (property != null && property.getUuid() != null) {
                personalEntry.setUuid(property.getUuid());
                resultList.add(personalEntry);
            }
        }
        return resultList;
    }

    private Property createAndSaveProperty(String data, String type) {
        Property property = new Property();
        property.setData(data);
        property.setFrom("PREVENT");
        property.setType(type);

        try {
            return propertyService.save(property);
        } catch (Exception e) {
            log.error("Không thể lưu Property (type={}): {}", type, e.getMessage());
            return null;
        }
    }

}
