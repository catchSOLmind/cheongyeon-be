-- ============================================================
-- TASK_TYPE (업무)
-- ============================================================

-- [BATHROOM] 1~18
INSERT
IGNORE INTO task_type (task_type_id, category, name, point)
VALUES (1, 'BATHROOM', '손 세정 비누 리필', 5),
       (2, 'BATHROOM', '칫솔 교체', 5),
       (3, 'BATHROOM', '쓰레기통 비우기', 10),
       (4, 'BATHROOM', '욕실 슬리퍼 세정', 10),
       (5, 'BATHROOM', '거울 닦기', 10),
       (6, 'BATHROOM', '샤워기 필터 교체', 10),
       (7, 'BATHROOM', '세면대 청소', 20),
       (8, 'BATHROOM', '샤워기 청소', 20),
       (9, 'BATHROOM', '샤워부스 유리 닦기', 30),
       (10, 'BATHROOM', '욕실 수납장, 선반 청소', 40),
       (11, 'BATHROOM', '바닥 하수구 청소', 50),
       (12, 'BATHROOM', '변기 청소', 50),
       (13, 'BATHROOM', '욕조 청소', 50),
       (14, 'BATHROOM', '변면 청소', 60),
       (15, 'BATHROOM', '물때/곰팡이 제거', 50),
       (16, 'BATHROOM', '환풍기 청소', 60),
       (17, 'BATHROOM', '욕실 커튼 교체', 40),
       (18, 'BATHROOM', '화장실 전체 청소', 100);

-- [KITCHEN] 19 ~ 42
INSERT
IGNORE INTO task_type (task_type_id, category, name, point)
VALUES (19, 'KITCHEN', '얼음 채우기', 5),
       (20, 'KITCHEN', '수세미 교체', 5),
       (21, 'KITCHEN', '음식물 쓰레기 처리', 20),
       (22, 'KITCHEN', '조리대 닦기', 10),
       (23, 'KITCHEN', '상한 음식 버리기', 20),
       (24, 'KITCHEN', '그릇 정리', 10),
       (25, 'KITCHEN', '세제 소분', 5),
       (26, 'KITCHEN', '설거지', 20),
       (27, 'KITCHEN', '싱크대 청소', 10),
       (28, 'KITCHEN', '커피포트 청소', 10),
       (29, 'KITCHEN', '음식물 처리기 닦기', 20),
       (30, 'KITCHEN', '싱크대 하수구 정리', 30),
       (31, 'KITCHEN', '조리기구 소독', 40),
       (32, 'KITCHEN', '반찬통 패킹 소독', 30),
       (33, 'KITCHEN', '행주 삶기', 20),
       (34, 'KITCHEN', '에어프라이어 청소', 30),
       (35, 'KITCHEN', '식세기 청소', 30),
       (36, 'KITCHEN', '가스레인지/인덕션 청소', 20),
       (37, 'KITCHEN', '후드 닦기', 50),
       (38, 'KITCHEN', '냉장고 정리', 60),
       (39, 'KITCHEN', '벽 기름때 제거', 30),
       (40, 'KITCHEN', '오븐 청소', 30),
       (41, 'KITCHEN', '김치 냉장고 정리', 50),
       (42, 'KITCHEN', '주방 전체 청소', 100);

-- [LAUNDRY] 43 ~ 54
INSERT
IGNORE INTO task_type (task_type_id, category, name, point)
VALUES (43, 'LAUNDRY', '세탁기 돌리기', 10),
       (44, 'LAUNDRY', '제습제 교체', 10),
       (45, 'LAUNDRY', '빨래 널기', 10),
       (46, 'LAUNDRY', '수건 세탁', 10),
       (47, 'LAUNDRY', '빨래 개기', 10),
       (48, 'LAUNDRY', '스타일러 관리', 5),
       (49, 'LAUNDRY', '손 빨래', 40),
       (50, 'LAUNDRY', '세탁실 정리', 30),
       (51, 'LAUNDRY', '다림질', 30),
       (52, 'LAUNDRY', '세탁조 클린', 10),
       (53, 'LAUNDRY', '침구 세탁', 60),
       (54, 'LAUNDRY', '세탁소 가기', 20);

-- [BEDROOM] 55 ~ 72
INSERT
IGNORE INTO task_type (task_type_id, category, name, point)
VALUES (55, 'BEDROOM', '환기', 5),
       (56, 'BEDROOM', '이불 개기', 5),
       (57, 'BEDROOM', '머리카락 치우기', 5),
       (58, 'BEDROOM', '쓰레기통 비우기', 5),
       (59, 'BEDROOM', '화초 물 주기', 5),
       (60, 'BEDROOM', '거울 닦기', 10),
       (61, 'BEDROOM', '먼지 털기', 20),
       (62, 'BEDROOM', '베개 커버 교체', 20),
       (63, 'BEDROOM', '이불 교체', 30),
       (64, 'BEDROOM', '침대 프레임 닦기', 5),
       (65, 'BEDROOM', '책상 정리', 10),
       (66, 'BEDROOM', '화장대 정리', 10),
       (67, 'BEDROOM', '침실 바닥 청소', 40),
       (68, 'BEDROOM', '창틀 닦기', 60),
       (69, 'BEDROOM', '화장도구 세척', 10),
       (70, 'BEDROOM', '잡다한 물건 정리', 30),
       (71, 'BEDROOM', '조명 청소', 20),
       (72, 'BEDROOM', '방충망 세척', 60);

-- [LIVING] 73 ~ 92
INSERT
IGNORE INTO task_type (task_type_id, category, name, point)
VALUES (73, 'LIVING', 'TV 먼지 닦기', 10),
       (74, 'LIVING', '중문 닦기', 20),
       (75, 'LIVING', '화초 물 주기', 5),
       (76, 'LIVING', '먼지 닦기', 20),
       (77, 'LIVING', '책장 먼지 닦기', 20),
       (78, 'LIVING', '베란다 문 청소', 20),
       (79, 'LIVING', '청소기 돌리기', 30),
       (80, 'LIVING', '물건 정리', 30),
       (81, 'LIVING', '옷장 정리', 100),
       (82, 'LIVING', '소파 관리', 30),
       (83, 'LIVING', '거실 바닥 닦기', 40),
       (84, 'LIVING', '바닥 걸레질', 50),
       (85, 'LIVING', '장식장 정리', 30),
       (86, 'LIVING', '커튼 교체', 70),
       (87, 'LIVING', '선풍기 청소', 70),
       (88, 'LIVING', '카펫,러그 빨래', 80),
       (89, 'LIVING', '베란다 바닥 청소', 90),
       (90, 'LIVING', '가습기 관리', 40),
       (91, 'LIVING', '공기청정기 관리', 40),
       (92, 'LIVING', '에어컨 청소', 60);

-- [TRASH] 93 ~ 99
INSERT
IGNORE INTO task_type (task_type_id, category, name, point)
VALUES (93, 'TRASH', '쓰레기 봉투 채우기', 5),
       (94, 'TRASH', '화장실 쓰레기 비우기', 10),
       (95, 'TRASH', '일반 쓰레기 버리기', 10),
       (96, 'TRASH', '음식물 쓰레기 버리기', 20),
       (97, 'TRASH', '택배 박스 정리', 20),
       (98, 'TRASH', '쓰레기통 청소', 20),
       (99, 'TRASH', '분리수거', 20);

-- [ETC] 100 ~ 116 (SubCategory 포함)
INSERT
IGNORE INTO task_type (task_type_id, category, sub_category, name, point)
VALUES (100, 'ETC', 'PET', '밥 주기', 5),
       (101, 'ETC', 'PET', '사료 채우기', 5),
       (102, 'ETC', 'PET', '물통 세척', 10),
       (103, 'ETC', 'PET', '밥그릇 세척', 10),
       (104, 'ETC', 'PET', '배변 패드 교체', 20),
       (105, 'ETC', 'PET', '화장실 청소', 30),
       (106, 'ETC', 'PET', '집 청소', 40),
       (107, 'ETC', 'PET', '산책', 30),
       (108, 'ETC', 'PET', '목욕', 40),
       (109, 'ETC', 'BABY', '약 먹이기', 20),
       (110, 'ETC', 'BABY', '젖병 소독', 30),
       (111, 'ETC', 'BABY', '매트 소독', 30),
       (112, 'ETC', 'BABY', '장난감 소독', 40),
       (113, 'ETC', 'BABY', '침대 시트 교체', 40),
       (114, 'ETC', 'BABY', '손수건 삶기', 10),
       (115, 'ETC', 'BABY', '인형 빨기', 20),
       (116, 'ETC', 'BABY', '이유식 만들기', 30);

-- ============================================================
-- Suggestion_Task (옵션 상품)
-- ============================================================

-- [1] 화장실 전체 청소 (ID: 18) - 주기성(7일)
INSERT
IGNORE INTO suggestion_task (suggestion_task_id, task_type_id, title, img_url,
                             default_estimated_minutes, reward_point, recommendation_cycle_days, season_months,
                             desc_delayed, desc_no_assignee, desc_general, desc_repeat)
VALUES (1, 18,
        '화장실 청소',
        'options/restroom.png',
        120, 120, 7, NULL,
        '{delay_period} 동안 미뤄졌어요. 물때와 곰팡이, 더 방치하면 지우기 힘들어요. 청연에게 맡겨주세요.',
        '{task_name} 담당자가 없네요. 꿉꿉한 화장실, 청연과 함께 상쾌하게 바꿔보세요.',
        '습기 가득한 화장실, 물때 제거가 시급해요!',
        '매주 반복되는 힘든 화장실 청소, 이제 전문가에게 맡기고 쉬세요. ');

-- [2] 냉장고 정리 (ID: 38) - 주기성(30일)
INSERT
IGNORE INTO suggestion_task (suggestion_task_id, task_type_id, title, img_url,
                             default_estimated_minutes, reward_point, recommendation_cycle_days, season_months,
                             desc_delayed, desc_no_assignee, desc_general, desc_repeat)
VALUES (2, 38,
        '냉장실 청소',
        'options/refrigerator.png',
        120, 80, 30, NULL,
        '{delay_period} 넘게 정리를 미루셨네요. 유통기한 지난 음식과 묵은 때, 한 번에 해결해 드려요.',
        '{task_name} 담당자가 없네요. 식중독 예방을 위해 냉장고 속 세균을 잡아보세요.',
        '꽉 찬 냉장고, {season}에는 냉장고 정리가 필요해요.',
        '항상 깨끗한 냉장고를 유지해보세요.');

-- [3] 창틀 청소 (ID: 68) - 주기성(90일) & 시즌(3~6월)
INSERT
IGNORE INTO suggestion_task (suggestion_task_id, task_type_id, title, img_url,
                             default_estimated_minutes, reward_point, recommendation_cycle_days, season_months,
                             desc_delayed, desc_no_assignee, desc_general, desc_repeat)
VALUES (3, 68,
        '창틀 청소',
        'options/windowframe.png',
        60, 80, 90, '3, 4, 5, 6',
        '{delay_period} 동안 쌓인 먼지, 창문을 열 때마다 들어오고 있어요. {time}만에 깨끗하게 닦아드릴게요.',
        '{task_name} 담당자가 없네요. 손 닿기 힘든 틈새 먼지, 청연이 말끔히 제거해 드려요.',
        '미세먼지가 심한 {season}, 호흡기 건강을 위해 창틀 먼지부터 제거해야 해요. ',
        '환기할 때마다 상쾌하도록, 창틀을 항상 깨끗하게 유지하세요.');

-- [4] 옷장 정리 (ID: 81) - 주기성(90일) & 시즌(3,6,9,12월)
INSERT
IGNORE INTO suggestion_task (suggestion_task_id, task_type_id, title, img_url,
                             default_estimated_minutes, reward_point, recommendation_cycle_days, season_months,
                             desc_delayed, desc_no_assignee, desc_general, desc_repeat)
VALUES (4, 81,
        '옷장 정리',
        'options/wardrobe.png',
        60, 120, 90, "3, 6, 9, 12",
        '{delay_period} 지났어요. 입지 않는 옷과 계절 지난 옷, 전문가의 노하우로 공간을 넓혀드려요. ',
        '{task_name} 담당자가 없네요. 다가오는 {season} 옷 정리, 엄두가 안 난다면 청연을 불러주세요.'' ',
        '계절이 바뀌는 {season}, 묵은 옷은 넣고 새 계절 옷을 꺼낼 타이밍이에요. ',
        '매일 아침 코디가 쉬워지도록, 깔끔한 옷장을 유지해보세요. ');

-- [5] 후드 청소 (ID: 37) - 주기성(60일)
INSERT
IGNORE INTO suggestion_task (suggestion_task_id, task_type_id, title, img_url,
                             default_estimated_minutes, reward_point, recommendation_cycle_days, season_months,
                             desc_delayed, desc_no_assignee, desc_general, desc_repeat)
VALUES (5, 37,
        '후드 청소',
        'options/hood.png',
        60, 70, 60, NULL,
        '{delay_period} 넘게 방치된 기름때, 화재 위험이 있어요. {time}만 투자해서 안전을 지키세요. ',
        '{task_name} 담당자가 없네요. 끈적이는 기름때 제거, 혼자서 힘들다면 청연이 도와드릴게요.',
        '요리할 때마다 떨어지는 기름방울, {season} 위생을 위해 후드 청소는 필수예요. ',
        '항상 쾌적한 주방 공기를 위해 정기적인 후드 관리가 필요해요. ');

-- [6] 주방 청소 (ID: 42) - 주기성(30일)
INSERT
IGNORE INTO suggestion_task (suggestion_task_id, task_type_id, title, img_url,
                             default_estimated_minutes, reward_point, recommendation_cycle_days, season_months,
                             desc_delayed, desc_no_assignee, desc_general, desc_repeat)
VALUES (6, 42,
        '주방 청소',
        'options/kitchen.png',
        120, 120, 15, NULL,
        '{delay_period} 동안 쌓인 묵은 때와 기름기, 전문가의 손길로 새 주방처럼 만들어드려요.',
        '{task_name} 담당자가 없네요. 손이 많이 가는 주방 청소, 청연에게 맡기고 여유를 찾으세요.',
        '위생이 중요한 {season}, 우리 가족 먹거리를 책임지는 주방부터 챙기세요. ',
        '항상 깨끗한 주방을 유지해보세요.');

-- [7] 이유식 구독 (ID: 116) - 주기성(30일)
INSERT
IGNORE INTO suggestion_task (suggestion_task_id, task_type_id, title, img_url,
                             default_estimated_minutes, reward_point, recommendation_cycle_days, season_months,
                             desc_delayed, desc_no_assignee, desc_general, desc_repeat)
VALUES (7, 42,
        '이유식 구독',
        'options/babyfood.png',
        0, 50, 7, NULL,
        '{delay_period} 동안 식단 고민 많으셨죠? 영양 설계된 이유식을 집 앞으로 배송해 드려요.',
        '{task_name} 담당자가 없네요. 육아에 지친 당신을 위해 청연이 준비했어요.',
        '우리 아이 면역력이 중요한 {season}, 믿을 수 있는 재료로 만든 이유식을 시작해보세요. ',
        '아이 입맛에 딱 맞는 이유식을 받아보세요.');

-- [8] 에어컨 청소 (ID: 92) - 시즌성(5,6,7,8월)
INSERT
IGNORE INTO suggestion_task (suggestion_task_id, task_type_id, title, img_url,
                             default_estimated_minutes, reward_point, recommendation_cycle_days, season_months,
                             desc_delayed, desc_no_assignee, desc_general, desc_repeat)
VALUES (8, 92,
        '에어컨 청소',
        'options/aircon.png',
        90, 80, NULL, '5,6,7,8',
        '{delay_period} 지났어요. 묵은 먼지와 곰팡이를 {time}만에 싹 씻어내세요. ',
        '{task_name} 담당자가 없네요. {season} 필수 코스, 잊지 마세요.',
        '다가오는 {season}, 에어컨 필터 청소로 호흡기 건강을 챙기세요.',
        ''
       );

-- -- [3] 침구 세탁 (ID: 53) - 주기성(14일)
-- INSERT INTO suggestion_task (suggestion_task_id, task_type_id, title, img_url,
--                              default_estimated_minutes, reward_point, recommendation_cycle_days, season_months,
--                              desc_delayed, desc_no_assignee, desc_general, desc_repeat)
-- VALUES (3, 53,
--         '뽀송뽀송 침구 살균 세탁',
--         'options/bedding_wash.png',
--         180, 100, 14, NULL,
--         '{delay_period}째 미룬 침구 세탁, 진드기가 걱정되지 않나요?',
--         '{task_name} 담당자가 없네요. {season} 침구 교체로 기분 전환 해보세요.',
--         '피부에 닿는 이불, {season} 맞이 세탁이 필요해요.',
--         '매일 덮는 이불, 깨끗하게 관리하세요.');


-- ============================================================
-- SUGGESTION_TASK_OPTION (옵션 상품)
-- ============================================================

-- [1] 화장실 청소 옵션 (ID: 1)
INSERT
IGNORE INTO suggestion_task_option (suggestion_task_id, count, estimated_minutes, price)
VALUES (1, '1개', 120, 45000),
       (1, '2개', 120, 45000),
       (1, '3개', 180, 65000),
       (1, '4개', 240, 85000);

-- [2] 냉장실 청소 옵션 (ID: 2)
INSERT
IGNORE INTO suggestion_task_option (suggestion_task_id, count, estimated_minutes, price)
VALUES (2, '1대', 180, 65000),
       (2, '2대', 300, 110000);

-- [3] 창틀 청소 옵션 (ID: 3)
INSERT
IGNORE INTO suggestion_task_option (suggestion_task_id, count, estimated_minutes, price)
VALUES (3, '2개', 60, 30000),
       (3, '3개', 60, 40000),
       (3, '4개', 120, 50000),
       (3, '5개', 120, 60000);

-- [4] 옷장 정리 옵션 (ID: 4)
INSERT
IGNORE INTO suggestion_task_option (suggestion_task_id, count, estimated_minutes, price)
VALUES (4, '2개', 60, 20000),
       (4, '3개', 120, 30000),
       (4, '4개', 180, 40000);

-- [5] 후드 청소 옵션 (ID: 5)
INSERT
IGNORE INTO suggestion_task_option (suggestion_task_id, count, estimated_minutes, price)
VALUES (5, '1개', 30, 10000),
       (5, '2개', 60, 20000);

-- [6] 주방 청소 옵션 (ID: 6)
INSERT
IGNORE INTO suggestion_task_option (suggestion_task_id, count, estimated_minutes, price)
VALUES (6, '기본', 120, 45000);

-- [7] 이유식 구독 옵션 (ID: 7)
INSERT
IGNORE INTO suggestion_task_option (suggestion_task_id, count, estimated_minutes, price)
VALUES (7, '초기', 175200, 30000),
       (7, '중기', 306600, 35000),
       (7, '후기', 438000, 40000),
       (7, '유아식', 569401, 45000);

-- [8] 이유식 구독 옵션 (ID: 8)
INSERT
IGNORE INTO suggestion_task_option (suggestion_task_id, count, estimated_minutes, price)
VALUES (8, '벽걸이', 60, 70000),
       (8, '스탠드', 90, 110000);

-- ============================================================
-- 가사 성향 테스트 (Housework_Test_Question)
-- ============================================================
-- 질문지
INSERT
IGNORE INTO housework_test_question (question_id, question_order, content)
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
INSERT IGNORE INTO housework_test_choice (question_id, choice_type, content, active_score, clean_score, routine_score, sloppy_score)
VALUES (1, 'A', '바로 정리부터 시작', 5, 2, 0, -2),
       (1, 'B', '일단 커피부터, 천천히 나중에', -5, 0, 0, 3);

-- 2번 질문
INSERT IGNORE INTO housework_test_choice (question_id, choice_type, content, active_score, clean_score, routine_score, sloppy_score)
VALUES (2, 'A', '밥 먹자마자 바로 설거지', 5, 0, 3, -3),
       (2, 'B', '싱크대에 쌓아뒀다가 한 번에', -5, 0, -2, 5);

-- 3번 질문
INSERT IGNORE INTO housework_test_choice (question_id, choice_type, content, active_score, clean_score, routine_score, sloppy_score)
VALUES (3, 'A', '즉시 휴지로 집어서 버리기', 2, 6, 0, -4),
       (3, 'B', '청소할 때 한 번에 쓸어담기', 0, -6, 2, 4);

-- 4번 질문
INSERT IGNORE INTO housework_test_choice (question_id, choice_type, content, active_score, clean_score, routine_score, sloppy_score)
VALUES (4, 'A', '주 1회 정기적으로 유통기한 체크', 0, 4, 5, -2),
       (4, 'B', '상할 날짜가 다가오면 그때 정리', 0, -4, -4, 4);

-- 5번 질문
INSERT IGNORE INTO housework_test_choice (question_id, choice_type, content, active_score, clean_score, routine_score, sloppy_score)
VALUES (5, 'A', '아무 말 없이 내가 먼저 알아서', 6, 0, 0, -2),
       (5, 'B', '청소 알람이 울리거나 누가 얘기 해줄 때', -6, 0, 2, 4);

-- 6번 질문
INSERT IGNORE INTO housework_test_choice (question_id, choice_type, content, active_score, clean_score, routine_score, sloppy_score)
VALUES (6, 'A', '정해진 요일과 시간에 따라 규칙적으로', 0, 2, 7, -3),
       (6, 'B', '기분 내킬 때 또는 필요할 때', 3, 0, -7, 5);

-- 7번 질문
INSERT IGNORE INTO housework_test_choice (question_id, choice_type, content, active_score, clean_score, routine_score, sloppy_score)
VALUES (7, 'A', '계절과 종류별로 미리 완벽하게 분류', 0, 6, 4, -4),
       (7, 'B', '필요한 옷을 그때 그때 꺼내서', 0, -4, -6, 6);

-- 8번 질문
INSERT IGNORE INTO housework_test_choice (question_id, choice_type, content, active_score, clean_score, routine_score, sloppy_score)
VALUES (8, 'A', '미리 계획을 세우고 체계적으로 진행', 0, 2, 7, -2),
       (8, 'B', '눈에 띄는 것부터 하나씩 처리', 5, 0, -7, 2);

-- 9번 질문
INSERT IGNORE INTO housework_test_choice (question_id, choice_type, content, active_score, clean_score, routine_score, sloppy_score)
VALUES (9, 'A', '웬만하면 끝까지 청소를 마무리', 2, 5, 2, -6),
       (9, 'B', '적당히 된 것 같으면 그만', 0, -3, 0, 6);
