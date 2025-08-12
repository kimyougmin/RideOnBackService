-- Insert test data in correct order to avoid foreign key violations

-- 1. Insert users first (let BIGSERIAL auto-generate IDs)
INSERT INTO users (email, password, name, gender, birth_date, age, phone, profile_image, user_id, provider) VALUES
('testuser1@example.com', '$2a$10$test', '김라이더', 'M', '1990-01-15', 33, '010-1234-5678', 'https://example.com/profile1.jpg', 'testuser1', 'original'),
('testuser2@example.com', '$2a$10$test', '이사이클', 'F', '1985-03-22', 38, '010-2345-6789', 'https://example.com/profile2.jpg', 'testuser2', 'original'),
('testuser3@example.com', '$2a$10$test', '박바이크', 'M', '1992-07-10', 31, '010-3456-7890', 'https://example.com/profile3.jpg', 'testuser3', 'original'),
('admin@rideon.com', '$2a$10$test', '관리자', 'M', '1980-12-01', 43, '010-9999-9999', 'https://example.com/admin.jpg', 'admin', 'original');

-- 2. Insert ride sessions and get their IDs
DO $$
DECLARE
    user1_id BIGINT;
    user2_id BIGINT;
    user3_id BIGINT;
    session1_id BIGINT;
    session2_id BIGINT;
    session3_id BIGINT;
BEGIN
    -- Get user IDs
    SELECT id INTO user1_id FROM users WHERE email = 'testuser1@example.com';
    SELECT id INTO user2_id FROM users WHERE email = 'testuser2@example.com';
    SELECT id INTO user3_id FROM users WHERE email = 'testuser3@example.com';
    
    -- Insert ride sessions and get their IDs
    INSERT INTO ride_session (user_id, started_at, ended_at, total_distance_km, avg_speed_kmh, max_speed_kmh, calories_burned, status, last_location_lat, last_location_lng, last_location_time, network_quality, connection_lost_count) VALUES
    (user1_id, NOW() - INTERVAL '3 hours', NOW() - INTERVAL '1 hour', 25.50, 18.20, 35.00, 450.00, 'COMPLETED', 37.5665, 126.9780, NOW() - INTERVAL '1 hour', 'GOOD', 0)
    RETURNING id INTO session1_id;
    
    INSERT INTO ride_session (user_id, started_at, ended_at, total_distance_km, avg_speed_kmh, max_speed_kmh, calories_burned, status, last_location_lat, last_location_lng, last_location_time, network_quality, connection_lost_count) VALUES
    (user2_id, NOW() - INTERVAL '2 hours', NOW() - INTERVAL '30 minutes', 32.10, 20.10, 42.50, 580.00, 'COMPLETED', 37.5519, 126.9882, NOW() - INTERVAL '30 minutes', 'EXCELLENT', 1)
    RETURNING id INTO session2_id;
    
    INSERT INTO ride_session (user_id, started_at, ended_at, total_distance_km, avg_speed_kmh, max_speed_kmh, calories_burned, status, last_location_lat, last_location_lng, last_location_time, network_quality, connection_lost_count) VALUES
    (user3_id, NOW() - INTERVAL '1 hour', NULL, 15.80, 16.50, 28.00, 280.00, 'ACTIVE', 37.5796, 126.9770, NOW() - INTERVAL '5 minutes', 'FAIR', 2)
    RETURNING id INTO session3_id;
    
    -- 3. Insert ride points using actual session IDs
    INSERT INTO ride_point (ride_session_id, latitude, longitude, speed_kmh, altitude, accuracy, heading, recorded_at, network_quality, battery_level, is_offline_sync) VALUES
    -- 사용자 1의 라이딩 경로 (강남 -> 홍대)
    (session1_id, 37.5665, 126.9780, 20.00, 45.00, 5.00, 180.00, NOW() - INTERVAL '3 hours', 'GOOD', 85, false),
    (session1_id, 37.5645, 126.9760, 18.50, 42.00, 4.50, 185.00, NOW() - INTERVAL '2 hours 45 minutes', 'GOOD', 83, false),
    (session1_id, 37.5625, 126.9740, 22.00, 40.00, 3.80, 190.00, NOW() - INTERVAL '2 hours 30 minutes', 'GOOD', 81, false),
    (session1_id, 37.5605, 126.9720, 19.50, 38.00, 4.20, 195.00, NOW() - INTERVAL '2 hours 15 minutes', 'GOOD', 79, false),
    (session1_id, 37.5585, 126.9700, 21.00, 35.00, 3.50, 200.00, NOW() - INTERVAL '2 hours', 'GOOD', 77, false),

    -- 사용자 2의 라이딩 경로 (잠실 -> 강남)
    (session2_id, 37.5519, 126.9882, 18.00, 30.00, 4.00, 270.00, NOW() - INTERVAL '2 hours', 'EXCELLENT', 90, false),
    (session2_id, 37.5539, 126.9862, 19.50, 32.00, 3.80, 275.00, NOW() - INTERVAL '1 hour 45 minutes', 'EXCELLENT', 88, false),
    (session2_id, 37.5559, 126.9842, 21.00, 35.00, 4.20, 280.00, NOW() - INTERVAL '1 hour 30 minutes', 'EXCELLENT', 86, false),
    (session2_id, 37.5579, 126.9822, 20.50, 38.00, 3.50, 285.00, NOW() - INTERVAL '1 hour 15 minutes', 'EXCELLENT', 84, false),
    (session2_id, 37.5599, 126.9802, 22.50, 40.00, 4.00, 290.00, NOW() - INTERVAL '1 hour', 'EXCELLENT', 82, false),

    -- 사용자 3의 현재 라이딩 경로 (서울역 -> 명동)
    (session3_id, 37.5796, 126.9770, 16.00, 25.00, 5.00, 90.00, NOW() - INTERVAL '1 hour', 'FAIR', 75, false),
    (session3_id, 37.5776, 126.9790, 17.50, 28.00, 4.50, 95.00, NOW() - INTERVAL '45 minutes', 'FAIR', 73, false),
    (session3_id, 37.5756, 126.9810, 18.00, 30.00, 4.00, 100.00, NOW() - INTERVAL '30 minutes', 'FAIR', 71, false),
    (session3_id, 37.5736, 126.9830, 16.50, 32.00, 4.20, 105.00, NOW() - INTERVAL '15 minutes', 'FAIR', 69, false),
    (session3_id, 37.5716, 126.9850, 19.00, 35.00, 3.80, 110.00, NOW() - INTERVAL '5 minutes', 'FAIR', 67, false);

    -- 4. Insert network status
    INSERT INTO network_status (ride_session_id, connection_type, signal_strength, is_connected, latency_ms, upload_speed_mbps, download_speed_mbps, packet_loss_percentage, recorded_at) VALUES
    (session1_id, '4G', 85, true, 45, 12.5, 25.8, 0.1, NOW() - INTERVAL '3 hours'),
    (session1_id, '4G', 82, true, 48, 11.8, 24.2, 0.2, NOW() - INTERVAL '2 hours 30 minutes'),
    (session2_id, '5G', 95, true, 25, 45.2, 78.5, 0.0, NOW() - INTERVAL '2 hours'),
    (session2_id, '5G', 92, true, 28, 42.1, 75.3, 0.1, NOW() - INTERVAL '1 hour 30 minutes'),
    (session3_id, '4G', 65, true, 85, 8.5, 15.2, 1.2, NOW() - INTERVAL '1 hour'),
    (session3_id, '4G', 62, true, 90, 7.8, 14.1, 1.5, NOW() - INTERVAL '30 minutes');
END $$;

-- 5. Insert obstacle reports
INSERT INTO obstacle_report (user_id, latitude, longitude, report_type, description, status, image, created_at) VALUES
-- 1. 장애물 타입 - 강남역 근처
((SELECT id FROM users WHERE email = 'testuser1@example.com'), 37.5665, 126.9780, 'OBSTACLE', '강남역 2번 출구 앞에 큰 바위가 길을 막고 있습니다. 야간에 위험할 수 있어요.', 'UNCONFIRMED', 'https://example.com/obstacle1.jpg', NOW() - INTERVAL '2 hours'),
-- 2. 도로 손상 타입 - 홍대입구역 근처
((SELECT id FROM users WHERE email = 'testuser2@example.com'), 37.5572, 126.9254, 'ROAD_DAMAGE', '홍대입구역 9번 출구 앞 도로에 큰 구멍이 생겼습니다. 자전거 타이어가 빠질 수 있어요.', 'CONFIRMED', 'https://example.com/road_damage1.jpg', NOW() - INTERVAL '1 hour'),
-- 3. 공사 타입 - 잠실역 근처
((SELECT id FROM users WHERE email = 'testuser3@example.com'), 37.5139, 127.1006, 'CONSTRUCTION', '잠실역 1번 출구 앞에서 도로 공사가 진행 중입니다. 자전거 도로가 일부 차단되어 있어요.', 'CONFIRMED', 'https://example.com/construction1.jpg', NOW() - INTERVAL '30 minutes'),
-- 4. 미끄러운 도로 타입 - 명동 근처
((SELECT id FROM users WHERE email = 'testuser1@example.com'), 37.5636, 126.9834, 'SLIPPERY', '명동역 6번 출구 앞 도로가 비로 인해 미끄러워졌습니다. 주의가 필요해요.', 'UNCONFIRMED', 'https://example.com/slippery1.jpg', NOW() - INTERVAL '15 minutes'),
-- 5. 기타 타입 - 서울역 근처
((SELECT id FROM users WHERE email = 'testuser2@example.com'), 37.5796, 126.9770, 'ETC', '서울역 광장 앞에 자전거 주차대가 넘어져 있습니다. 정리해주시면 감사하겠습니다.', 'RESOLVED', 'https://example.com/etc1.jpg', NOW() - INTERVAL '1 day'),
-- 6. 장애물 타입 - 강남대로
((SELECT id FROM users WHERE email = 'testuser3@example.com'), 37.5645, 126.9760, 'OBSTACLE', '강남대로 자전거도로에 쓰러진 나무가 있습니다. 통행에 지장이 있어요.', 'CONFIRMED', 'https://example.com/obstacle2.jpg', NOW() - INTERVAL '3 hours'),
-- 7. 도로 손상 타입 - 잠실대로
((SELECT id FROM users WHERE email = 'testuser1@example.com'), 37.5139, 127.1006, 'ROAD_DAMAGE', '잠실대로 자전거도로에 균열이 생겼습니다. 안전에 위험할 수 있어요.', 'UNCONFIRMED', 'https://example.com/road_damage2.jpg', NOW() - INTERVAL '45 minutes'),
-- 8. 공사 타입 - 홍대거리
((SELECT id FROM users WHERE email = 'testuser2@example.com'), 37.5572, 126.9254, 'CONSTRUCTION', '홍대거리에서 상수도 공사가 진행 중입니다. 우회로를 이용해주세요.', 'CONFIRMED', 'https://example.com/construction2.jpg', NOW() - INTERVAL '2 hours'),
-- 9. 미끄러운 도로 타입 - 강남역
((SELECT id FROM users WHERE email = 'testuser3@example.com'), 37.5665, 126.9780, 'SLIPPERY', '강남역 3번 출구 앞 도로가 얼어서 미끄러워졌습니다. 매우 위험해요!', 'UNCONFIRMED', 'https://example.com/slippery2.jpg', NOW() - INTERVAL '10 minutes'),
-- 10. 기타 타입 - 명동거리
((SELECT id FROM users WHERE email = 'testuser1@example.com'), 37.5636, 126.9834, 'ETC', '명동거리에 자전거 택배상자가 길을 막고 있습니다. 정리 부탁드려요.', 'RESOLVED', 'https://example.com/etc2.jpg', NOW() - INTERVAL '6 hours'),
-- 11. 장애물 타입 - 서울역 (최근)
((SELECT id FROM users WHERE email = 'testuser2@example.com'), 37.5796, 126.9770, 'OBSTACLE', '서울역 2번 출구 앞에 공사용 철판이 자전거도로를 막고 있습니다.', 'UNCONFIRMED', 'https://example.com/obstacle3.jpg', NOW() - INTERVAL '5 minutes'),
-- 12. 도로 손상 타입 - 강남역 (최근)
((SELECT id FROM users WHERE email = 'testuser3@example.com'), 37.5665, 126.9780, 'ROAD_DAMAGE', '강남역 4번 출구 앞 자전거도로에 큰 구멍이 생겼습니다.', 'UNCONFIRMED', 'https://example.com/road_damage3.jpg', NOW() - INTERVAL '3 minutes'),
-- 13. 공사 타입 - 홍대역 (최근)
((SELECT id FROM users WHERE email = 'testuser1@example.com'), 37.5572, 126.9254, 'CONSTRUCTION', '홍대역 2번 출구 앞에서 전기공사가 진행 중입니다.', 'CONFIRMED', 'https://example.com/construction3.jpg', NOW() - INTERVAL '1 minute'),
-- 14. 미끄러운 도로 타입 - 잠실역 (최근)
((SELECT id FROM users WHERE email = 'testuser2@example.com'), 37.5139, 127.1006, 'SLIPPERY', '잠실역 3번 출구 앞 도로가 기름으로 미끄러워졌습니다.', 'UNCONFIRMED', 'https://example.com/slippery3.jpg', NOW() - INTERVAL '2 minutes'),
-- 15. 기타 타입 - 명동역 (최근)
((SELECT id FROM users WHERE email = 'testuser3@example.com'), 37.5636, 126.9834, 'ETC', '명동역 1번 출구 앞에 자전거 수리점이 임시로 설치되어 통행이 어렵습니다.', 'CONFIRMED', 'https://example.com/etc3.jpg', NOW() - INTERVAL '30 seconds');

-- 6. Insert posts
INSERT INTO posts (user_id, title, content, image, view_count, like_count, created_at, update_at) VALUES
((SELECT id FROM users WHERE email = 'testuser1@example.com'), '강남역 근처 라이딩 후기', '오늘 강남역에서 홍대까지 라이딩했는데 정말 좋았습니다. 날씨도 좋고 경로도 안전했어요.', 'https://example.com/post1.jpg', 100, 10, NOW(), NOW()),
((SELECT id FROM users WHERE email = 'testuser2@example.com'), '잠실에서 강남까지 라이딩', '잠실역에서 강남역까지 라이딩했습니다. 거리는 좀 멀었지만 경치가 좋았어요.', 'https://example.com/post2.jpg', 150, 15, NOW(), NOW()),
((SELECT id FROM users WHERE email = 'testuser3@example.com'), '서울역에서 명동 라이딩', '서울역에서 명동까지 라이딩 중입니다. 교통이 좀 복잡하지만 재미있어요.', 'https://example.com/post3.jpg', 200, 20, NOW(), NOW());

-- 7. Insert questions
INSERT INTO question (user_id, title, content, status, view_count, like_count, created_at, update_at) VALUES
((SELECT id FROM users WHERE email = 'testuser1@example.com'), '자전거 도로 이용법', '자전거 도로를 이용할 때 주의사항이 있나요?', 'SOLVED', 100, 10, NOW(), NOW()),
((SELECT id FROM users WHERE email = 'testuser2@example.com'), '안전한 라이딩 팁', '안전한 라이딩을 위한 팁을 알려주세요.', 'UNSOLVED', 150, 15, NOW(), NOW()),
((SELECT id FROM users WHERE email = 'testuser3@example.com'), '자전거 수리점 추천', '강남역 근처에 좋은 자전거 수리점이 있나요?', 'SOLVED', 200, 20, NOW(), NOW());

-- 8. Insert inquiries
INSERT INTO inquiry (user_id, title, content, created_at) VALUES
((SELECT id FROM users WHERE email = 'testuser1@example.com'), '앱 사용법 문의', 'RideOn 앱 사용법에 대해 문의드립니다.', NOW()),
((SELECT id FROM users WHERE email = 'testuser2@example.com'), '기능 개선 제안', '앱에 새로운 기능을 추가하면 좋을 것 같습니다.', NOW()),
((SELECT id FROM users WHERE email = 'testuser3@example.com'), '버그 신고', '앱에서 버그를 발견했습니다.', NOW());
