package com.catchsolmind.cheongyeonbe.global.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public enum TestResultType {

    PERFECTIONIST(
            "뽀득이",
            "완벽주의 청소왕",
            "청소는 선택이 아니라 그냥 기본이지",
            List.of("# 청소왕", "# 매일반짝반짝", "# 먼지제로"),
            "보이는 먼지, 어질러진 물건을 그냥 지나치기 어려워요. 한 번 시작한 청소는 끝을 봐야 마음이 편하고, 깔끔한 공간에서 가장 안정감을 느끼는 타입이에요.",
            List.of("1. 오늘도 완벽하게!", "2. 청소는 매일 매일이 중요해요", "3. 먼지 한 톨도 용납 못해!"),
            "기준이 너무 높아 스스로 피곤해질 수 있어요. 가끔은 “이정도면 충분해”라고 생각해봐요!"
    ),

    RELAXED(
            "느긋이",
            "여유로운 청소가",
            "지금은 아니어도 언젠가는 할거야",
            List.of("# 여유만점", "# 스트레스NO", "# 내페이스대로"),
            "완벽하진 않아도 기본은 지켜요. 청소를 스트레스로 만들기보다는 내 생활 리듬 안에서 자연스럽게 하는 편이에요.",
            List.of("1. 서두르지 않아도 깨끗해", "2. 천천히 제대로", "3. 급할 것 없어요~"),
            "미루다 보면 한 번에 몰릴 수 있어요. 작은 정리는 바로 하면 훨씬 편해요!"
    ),

    EFFICIENT(
            "효율이",
            "합리적인 미니멀리스트",
            "안 어지르면, 안 치워도 되잖아?",
            List.of("# 합리적인사고", "# 최소노력", "# 최대효과"),
            "무조건 깨끗함보다는 유지 가능한 구조와 효율을 더 중요하게 여겨요. 덜 치우기 위해 덜 어지르는 타입이에요.",
            List.of("1. 딱 필요한 만큼만!", "2. 이 정도면 충분해~", "3. 20분 안에 끝내자"),
            "다른 사람과 기준이 다를 수 있어요. “이 정도면 괜찮아”가 갈등 포인트가 될수도..."
    ),

    PROCRASTINATOR(
            "내일이",
            "미래 위임 전문가",
            "이건... 내일의 내가 알아서 해줄거야",
            List.of("# 습관적내일", "# 만성미루기", "# 손님오면"),
            "지금 중요한 게 너무 많아요. 청소는 급할 때 한 번에 몰아서 하는 스타일이고 어질러진 환경에도 비교적 잘 적응해요.",
            List.of("1. 내일 할래...", "2. 아직 괜찮은데?", "3. 조금 더러워도 살 수 있어"),
            "쌓이면 시작하기가 더 힘들어져요. 작은 것부터 하나만 시작해도 달라져요!"
    ),
    ;

    private final String title;
    private final String subTitle;
    private final String mainQuote;
    private final List<String> tags;
    private final String description;
    private final List<String> representativeLines;
    private final String cautionPoint;
}
