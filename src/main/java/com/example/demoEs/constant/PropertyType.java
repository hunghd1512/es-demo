package com.example.demoEs.constant;

public enum PropertyType {
    PREVENT("PREVENT"),
    ORGANIZATION("ORGANIZATION"),
    PERSONAL("PERSONAL");

    private final String value;

    PropertyType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
