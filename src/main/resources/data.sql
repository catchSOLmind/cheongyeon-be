-- 가사 성향 테스트
-- 질문지
INSERT IGNORE INTO housework_test_question (question_id, question_order, content)
VALUES (1, 1, '주말 아침, 집이 어질러져 있다면?'),
       (2, 2, '내가 설거지를 하는 타이밍은?'),
       (3, 3, '바닥에 머리카락이 보인다면?'),
       (4, 4, '냉장고 정리를 하는 나만의 방식은?'),
       (5, 5, '나는 어떨 때 청소를 시작할까?'),
       (6, 6, '집안일을 할 때 나의 스타일은?'),
       (7, 7, '옷장 정리는 어떻게 하는 편인가?'),
       (8, 8, '집안일 우선순위를 정할 때는?'),
       (9, 9, '평소 청소할 때 나는?');

-- 1번 질문
INSERT IGNORE INTO housework_test_choice (question_id, choice_type, content, active_score, clean_score, routine_score,
                                   sloppy_score)
VALUES (1, 'A', '바로 정리부터 시작', 5, 2, 0, -2),
       (1, 'B', '일단 커피부터, 천천히 나중에', -5, 0, 0, 3);

-- 2번 질문
INSERT IGNORE INTO housework_test_choice (question_id, choice_type, content, active_score, clean_score, routine_score,
                                   sloppy_score)
VALUES (2, 'A', '밥 먹자마자 바로 설거지', 5, 0, 3, -3),
       (2, 'B', '싱크대에 쌓아뒀다가 한 번에', -5, 0, -2, 5);

-- 3번 질문
INSERT IGNORE INTO housework_test_choice (question_id, choice_type, content, active_score, clean_score, routine_score,
                                   sloppy_score)
VALUES (3, 'A', '즉시 휴지로 집어서 버리기', 2, 6, 0, -4),
       (3, 'B', '청소할 때 한 번에 쓸어담기', 0, -6, 2, 4);

-- 4번 질문
INSERT IGNORE INTO housework_test_choice (question_id, choice_type, content, active_score, clean_score, routine_score,
                                   sloppy_score)
VALUES (4, 'A', '주 1회 정기적으로 유통기한 체크', 0, 4, 5, -2),
       (4, 'B', '상할 날짜가 다가오면 그때 정리', 0, -4, -4, 4);

-- 5번 질문
INSERT IGNORE INTO housework_test_choice (question_id, choice_type, content, active_score, clean_score, routine_score,
                                   sloppy_score)
VALUES (5, 'A', '아무 말 없이 내가 먼저 알아서', 6, 0, 0, -2),
       (5, 'B', '청소 알람이 울리거나 누가 얘기 해줄 때', -6, 0, 2, 4);

-- 6번 질문
INSERT IGNORE INTO housework_test_choice (question_id, choice_type, content, active_score, clean_score, routine_score,
                                   sloppy_score)
VALUES (6, 'A', '정해진 요일과 시간에 따라 규칙적으로', 0, 2, 7, -3),
       (6, 'B', '기분 내킬 때 또는 필요할 때', 3, 0, -7, 5);

-- 7번 질문
INSERT IGNORE INTO housework_test_choice (question_id, choice_type, content, active_score, clean_score, routine_score,
                                   sloppy_score)
VALUES (7, 'A', '계절과 종류별로 미리 완벽하게 분류', 0, 6, 4, -4),
       (7, 'B', '필요한 옷을 그때 그때 꺼내서', 0, -4, -6, 6);

-- 8번 질문
INSERT IGNORE INTO housework_test_choice (question_id, choice_type, content, active_score, clean_score, routine_score,
                                   sloppy_score)
VALUES (8, 'A', '미리 계획을 세우고 체계적으로 진행', 0, 2, 7, -2),
       (8, 'B', '눈에 띄는 것부터 하나씩 처리', 5, 0, -7, 2);

-- 9번 질문
INSERT IGNORE INTO housework_test_choice (question_id, choice_type, content, active_score, clean_score, routine_score,
                                   sloppy_score)
VALUES (9, 'A', '웬만하면 끝까지 청소를 마무리', 2, 5, 2, -6),
       (9, 'B', '적당히 된 것 같으면 그만', 0, -3, 0, 6);