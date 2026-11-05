package com.esporteclubepelotas.api.modules.news.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum NewsReactionType {
    WOLF("wolf"),
    FIRE("fire"),
    CLAP("clap"),
    HEART("heart");

    private final String value;

    NewsReactionType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static NewsReactionType fromValue(String value) {
        if (value == null) return null;
        for (NewsReactionType type : values()) {
            if (type.value.equalsIgnoreCase(value.trim()) || type.name().equalsIgnoreCase(value.trim())) {
                return type;
            }
        }
        throw new IllegalArgumentException("Tipo de reação inválido: " + value);
    }
}

