package com.example.demoEs.service;

import com.example.demoEs.model.DataPrevent;

import java.util.List;

public interface DataPreventService {
    DataPrevent save(DataPrevent dataPrevent);
    void deletes(List<String> dataPreventIds);
}
