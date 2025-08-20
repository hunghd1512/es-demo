package com.example.demoEs.service.impl;

import com.example.demoEs.es.repository.db.DataPreventRepository;
import com.example.demoEs.model.DataPrevent;
import com.example.demoEs.service.DataPreventService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DataPreventServiceImpl implements DataPreventService {
    private final DataPreventRepository dataPreventRepository;
    @Override
    @Transactional
    public DataPrevent save(DataPrevent dataPrevent) {
       return dataPreventRepository.save(dataPrevent);
    }

    @Override
    public void deletes(List<String> dataPreventIds) {
        List<DataPrevent> dataPrevents = new ArrayList<>();
        for (String dataPreventId : dataPreventIds) {
            Optional<DataPrevent> dataPrevent = dataPreventRepository.findById(dataPreventId);
            dataPrevent.ifPresent(dataPrevents::add);
        }
        dataPreventRepository.deleteAll(dataPrevents);
    }
}
