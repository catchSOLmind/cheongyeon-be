package com.catchsolmind.cheongyeonbe.global.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PraiseType {
    DETAIL_KING(
            "꼼꼼왕",
            "꼼꼼하게 잘 해요"
    ),

    TIME_KEEPER(
            "시간 엄수",
            "시간을 잘 지켜요"
    ),

    DUST_KILLER(
            "먼지 킬러",
            "먼지 하나 없어요"
    ),

    SCENT_KING(
            "향기왕",
            "향기까지 신경써요"
    ),

    POINT_KING(
            "포인트왕",
            "업무를 많이 했어요"
    ),

    ORGANIZING_KING(
            "정리왕",
            "정리정돈 완벽"
    );

    private final String title;
    private final String description;
}
