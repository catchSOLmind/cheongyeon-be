package com.catchsolmind.cheongyeonbe.domain.user.service;

import com.catchsolmind.cheongyeonbe.global.enums.HouseworkType;

public class HouseworkTypeMapper {

    public static String labelOf(String type) {
        if (type == null) return null;
        return HouseworkType.valueOf(type).getLabel();
    }
}
