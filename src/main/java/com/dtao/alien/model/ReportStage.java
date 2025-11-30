package com.dtao.alien.model;
import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;

public enum ReportStage {
    USER,
    SYSTEM,
    PRINCIPAL,
    COMPLETED,

    @JsonEnumDefaultValue
    UNKNOWN
}

