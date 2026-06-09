package com.example.demoEs.constant;

public enum QueryType {
    AND("and"),
    OR("or");

    private final String value;

    QueryType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static QueryType fromValue(String value) {
        for (QueryType type : QueryType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        return AND;
    }
}
