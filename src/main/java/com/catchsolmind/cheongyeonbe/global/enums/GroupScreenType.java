package com.catchsolmind.cheongyeonbe.global.enums;

public enum GroupScreenType {
    PENDING_APPROVAL,  // 화면1: 멤버 초대됨 + 협약서 초안 존재 (미확정)
    NO_AGREEMENT,      // 화면2: 협약서 없음 (만료/미생성)
    SOLO_OWNER,        // 화면3: 1인 오너, 멤버 없음, 협약서 없음
    NORMAL             // 정상: 협약서 확정 완료
}
