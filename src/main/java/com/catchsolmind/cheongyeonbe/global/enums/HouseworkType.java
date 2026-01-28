package com.catchsolmind.cheongyeonbe.global.enums;

import lombok.Getter;

@Getter
public enum HouseworkType {

    TOMORROWER("내일이"),
    EFFICIENT("효율이"),
    CLEANER("뽀득이"),
    RELAXER("느긋이");

    private final String label;

    HouseworkType(String label) {
        this.label = label;
    }
}
