-- 1. 사용자 데이터 삽입
INSERT INTO members (email, password, name, height, weight, gender, birth_date, age, phone, profile_image, provider) VALUES
('testuser4@example.com', '$2a$10$test', '김라이더', 177, 77, 'M', '1990-01-15', 33, '010-1234-5678', 'https://example.com/profile1.jpg', 'original'),
('testuser5@example.com', '$2a$10$test', '이사이클', 162, 51, 'F', '1985-03-22', 38, '010-2345-6789', 'https://example.com/profile2.jpg', 'original'),
('testuser6@example.com', '$2a$10$test', '박바이크', 171, 67, 'M', '1992-07-10', 31, '010-3456-7890', 'https://example.com/profile3.jpg', 'original');

-- 2. 태그 데이터 삽입
INSERT INTO tags (name) VALUES
('초보자'),
('고수'),
('강남'),
('홍대'),
('잠실'),
('안전'),
('수리'),
('추천');

-- 4. 라이딩 세션 생성
DO $$
DECLARE
    user1 BIGINT;
    user2 BIGINT;
    user3 BIGINT;
    session1 BIGINT;
    session2 BIGINT;
    session3 BIGINT;
BEGIN
    SELECT id INTO user1 FROM members WHERE email='testuser4@example.com';
    SELECT id INTO user2 FROM members WHERE email='testuser5@example.com';
    SELECT id INTO user3 FROM members WHERE email='testuser6@example.com';

    INSERT INTO ride_session (member_id, started_at, ended_at, total_distance_km, avg_speed_kmh, calories_burned, status)
    VALUES (user1, NOW() - INTERVAL '3 hours', NOW() - INTERVAL '1 hour', 25.50, 18.20, 450.00, 'COMPLETED')
    RETURNING id INTO session1;

    INSERT INTO ride_session (member_id, started_at, ended_at, total_distance_km, avg_speed_kmh, calories_burned, status)
    VALUES (user2, NOW() - INTERVAL '2 hours', NOW() - INTERVAL '30 minutes', 32.10, 20.10, 580.00, 'COMPLETED')
    RETURNING id INTO session2;

    INSERT INTO ride_session (member_id, started_at, ended_at, total_distance_km, avg_speed_kmh, calories_burned, status)
    VALUES (user3, NOW() - INTERVAL '1 hour', NULL, 15.80, 16.50, 280.00, 'ACTIVE')
    RETURNING id INTO session3;

    -- 5. 라이딩 포인트 데이터 삽입
    INSERT INTO ride_point (ride_session_id, latitude, longitude, speed_kmh, recorded_at) VALUES
    (session1, 37.5665, 126.9780, 20.0, NOW() - INTERVAL '3 hours'),
    (session1, 37.5625, 126.9740, 22.0, NOW() - INTERVAL '2 hours 30 minutes'),
    (session2, 37.5519, 126.9882, 18.0, NOW() - INTERVAL '2 hours'),
    (session2, 37.5559, 126.9842, 21.0, NOW() - INTERVAL '1 hour 30 minutes'),
    (session3, 37.5796, 126.9770, 16.0, NOW() - INTERVAL '1 hour'),
    (session3, 37.5756, 126.9810, 18.0, NOW() - INTERVAL '30 minutes');
END $$;

-- 6. 장애물 신고
INSERT INTO obstacle_report (member_id, latitude, longitude, report_type, description, status, image, created_at) VALUES
((SELECT id FROM members WHERE email='testuser4@example.com'), 37.5665, 126.9780, 'OBSTACLE', '강남역 앞 큰 바위', 'UNCONFIRMED', 'https://example.com/obstacle1.jpg', NOW() - INTERVAL '2 hours'),
((SELECT id FROM members WHERE email='testuser5@example.com'), 37.5572, 126.9254, 'ROAD_DAMAGE', '홍대 앞 도로 구멍', 'CONFIRMED', 'https://example.com/road_damage.jpg', NOW() - INTERVAL '1 hour'),
((SELECT id FROM members WHERE email='testuser6@example.com'), 37.5139, 127.1006, 'CONSTRUCTION', '잠실역 공사', 'CONFIRMED', 'https://example.com/construction.jpg', NOW() - INTERVAL '30 minutes');

-- 7. 게시글 삽입 (image 컬럼 제거 또는 image_assets 참조 필요)
INSERT INTO posts (member_id, title, content, view_count, like_count, created_at, updated_at) VALUES
((SELECT id FROM members WHERE email='testuser4@example.com'), '강남역 라이딩 후기', '강남에서 홍대까지 달렸어요.', 100, 10, NOW(), NOW()),
((SELECT id FROM members WHERE email='testuser5@example.com'), '잠실에서 강남까지', '경치가 정말 좋았습니다.', 150, 15, NOW(), NOW()),
((SELECT id FROM members WHERE email='testuser6@example.com'), '서울역에서 명동까지', '복잡하지만 재밌었어요.', 200, 20, NOW(), NOW());

-- 8. 질문(Q&A) 삽입
INSERT INTO question (member_id, title, content, status, view_count, like_count, created_at, updated_at) VALUES
((SELECT id FROM members WHERE email='testuser4@example.com'), '자전거 도로 이용법', '주의사항이 있나요?', 'SOLVED', 100, 10, NOW(), NOW()),
((SELECT id FROM members WHERE email='testuser5@example.com'), '안전한 라이딩 팁', '팁을 알려주세요.', 'UNSOLVED', 150, 15, NOW(), NOW()),
((SELECT id FROM members WHERE email='testuser6@example.com'), '수리점 추천', '강남 근처 수리점 추천 부탁드립니다.', 'SOLVED', 200, 20, NOW(), NOW());

-- 9. 1:1 문의 삽입
INSERT INTO inquiry (member_id, title, content, created_at) VALUES
((SELECT id FROM members WHERE email='testuser4@example.com'), '앱 사용법 문의', 'RideOn 앱 사용법이 궁금합니다.', NOW()),
((SELECT id FROM members WHERE email='testuser5@example.com'), '기능 제안', '새로운 기능을 추가하면 좋겠어요.', NOW()),
((SELECT id FROM members WHERE email='testuser6@example.com'), '버그 신고', '버그를 발견했습니다.', NOW());

-- 10. 자전거 대여소 데이터 삽입
INSERT INTO bike_rental_station (rental_station_name, rental_station_type, address_street, latitude, longitude, bicycle_count, parking_spots, province) VALUES
('강남역 대여소', '공영', '서울특별시 강남구 강남대로 396', 37.5665, 126.9780, 50, 30, '서울특별시'),
('홍대입구역 대여소', '민영', '서울특별시 마포구 양화로 188', 37.5572, 126.9254, 30, 20, '서울특별시'),
('잠실역 대여소', '공영', '서울특별시 송파구 올림픽로 240', 37.5139, 127.1006, 40, 25, '서울특별시');

-- 11. 라이딩 그룹 데이터 삽입
INSERT INTO riding_group (group_name, region, image, latitude, longitude, limit_members, description, deadline, created_at) VALUES
('강남 라이딩 모임', '강남구', 'https://example.com/group1.jpg', 37.5665, 126.9780, 10, '강남 지역 라이딩 모임입니다.', '2024-12-31', NOW()),
('홍대 자전거 동호회', '마포구', 'https://example.com/group2.jpg', 37.5572, 126.9254, 15, '홍대 지역 자전거 동호회입니다.', '2024-12-31', NOW()),
('잠실 사이클링', '송파구', 'https://example.com/group3.jpg', 37.5139, 127.1006, 8, '잠실 지역 사이클링 모임입니다.', '2024-12-31', NOW());

INSERT INTO question_tag (question_id, tag_id) VALUES
((SELECT id FROM question WHERE title = '자전거 도로 이용법'), (SELECT id FROM tags WHERE name = '초보자')),
((SELECT id FROM question WHERE title = '자전거 도로 이용법'), (SELECT id FROM tags WHERE name = '안전')),
((SELECT id FROM question WHERE title = '안전한 라이딩 팁'), (SELECT id FROM tags WHERE name = '안전')),
((SELECT id FROM question WHERE title = '수리점 추천'), (SELECT id FROM tags WHERE name = '강남')),
((SELECT id FROM question WHERE title = '수리점 추천'), (SELECT id FROM tags WHERE name = '수리'));