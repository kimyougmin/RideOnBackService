# 🗄️ 테스트 데이터 쿼리 가이드

이 문서는 RideOn 애플리케이션의 테스트 데이터를 확인하고 분석할 수 있는 SQL 쿼리들을 제공합니다.

## 📊 데이터베이스 접속

### H2 콘솔 접속 (개발/테스트 환경)
- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:testdb` (테스트) / `jdbc:h2:mem:devdb` (개발)
- Username: `sa`
- Password: (비어있음)

## 👥 사용자 데이터 확인

### 모든 사용자 조회
```sql
SELECT id, email, name, gender, age, phone, created_at 
FROM users 
ORDER BY id;
```

### 특정 사용자 상세 정보
```sql
SELECT * FROM users WHERE id = 1;
```

## 🚴 라이딩 세션 데이터 확인

### 모든 라이딩 세션 조회
```sql
SELECT 
    rs.id,
    u.name as user_name,
    rs.started_at,
    rs.ended_at,
    rs.total_distance_km,
    rs.avg_speed_kmh,
    rs.max_speed_kmh,
    rs.calories_burned,
    rs.status,
    rs.last_location_lat,
    rs.last_location_lng
FROM ride_session rs
JOIN users u ON rs.user_id = u.id
ORDER BY rs.started_at DESC;
```

### 현재 활성 라이딩 세션
```sql
SELECT 
    rs.id,
    u.name as user_name,
    rs.started_at,
    rs.total_distance_km,
    rs.avg_speed_kmh,
    rs.last_location_lat,
    rs.last_location_lng
FROM ride_session rs
JOIN users u ON rs.user_id = u.id
WHERE rs.status = 'ACTIVE';
```

## 🗺️ 라이딩 경로 데이터 확인

### 특정 라이딩 세션의 경로 포인트
```sql
SELECT 
    latitude,
    longitude,
    speed_kmh,
    altitude,
    recorded_at,
    network_quality,
    battery_level
FROM ride_point 
WHERE ride_session_id = 1
ORDER BY recorded_at;
```

### 모든 라이딩 경로 시각화용
```sql
SELECT 
    rp.ride_session_id,
    u.name as user_name,
    rp.latitude,
    rp.longitude,
    rp.speed_kmh,
    rp.recorded_at
FROM ride_point rp
JOIN ride_session rs ON rp.ride_session_id = rs.id
JOIN users u ON rs.user_id = u.id
ORDER BY rp.ride_session_id, rp.recorded_at;
```

## 🚧 장애물 신고 데이터 확인

### 모든 장애물 신고 조회
```sql
SELECT 
    or.id,
    u.name as reporter_name,
    or.latitude,
    or.longitude,
    or.report_type,
    or.description,
    or.status,
    or.created_at
FROM obstacle_report or
JOIN users u ON or.user_id = u.id
ORDER BY or.created_at DESC;
```

### 장애물 타입별 통계
```sql
SELECT 
    report_type,
    COUNT(*) as count,
    AVG(CASE WHEN status = 'RESOLVED' THEN 1 ELSE 0 END) * 100 as resolved_percentage
FROM obstacle_report 
GROUP BY report_type
ORDER BY count DESC;
```

### 상태별 장애물 신고
```sql
SELECT 
    status,
    COUNT(*) as count
FROM obstacle_report 
GROUP BY status
ORDER BY count DESC;
```

### 특정 위치 주변 장애물 (반경 1km)
```sql
SELECT 
    or.id,
    u.name as reporter_name,
    or.latitude,
    or.longitude,
    or.report_type,
    or.description,
    or.status,
    or.created_at,
    SQRT(POWER(or.latitude - 37.5665, 2) + POWER(or.longitude - 126.9780, 2)) as distance_km
FROM obstacle_report or
JOIN users u ON or.user_id = u.id
WHERE SQRT(POWER(or.latitude - 37.5665, 2) + POWER(or.longitude - 126.9780, 2)) <= 1
ORDER BY distance_km;
```

### 최근 30일간 장애물 신고
```sql
SELECT 
    or.id,
    u.name as reporter_name,
    or.report_type,
    or.status,
    or.created_at
FROM obstacle_report or
JOIN users u ON or.user_id = u.id
WHERE or.created_at >= DATEADD('DAY', -30, CURRENT_TIMESTAMP)
ORDER BY or.created_at DESC;
```

### 사용자별 장애물 신고 통계
```sql
SELECT 
    u.name,
    COUNT(*) as total_reports,
    COUNT(CASE WHEN or.status = 'RESOLVED' THEN 1 END) as resolved_reports,
    COUNT(CASE WHEN or.status = 'CONFIRMED' THEN 1 END) as confirmed_reports
FROM obstacle_report or
JOIN users u ON or.user_id = u.id
GROUP BY u.id, u.name
ORDER BY total_reports DESC;
```

## 📡 네트워크 상태 데이터 확인

### 라이딩 세션별 네트워크 품질
```sql
SELECT 
    rs.id,
    u.name as user_name,
    ns.connection_type,
    ns.signal_strength,
    ns.latency_ms,
    ns.upload_speed_mbps,
    ns.download_speed_mbps,
    ns.recorded_at
FROM network_status ns
JOIN ride_session rs ON ns.ride_session_id = rs.id
JOIN users u ON rs.user_id = u.id
ORDER BY ns.recorded_at DESC;
```

### 평균 네트워크 품질 통계
```sql
SELECT 
    connection_type,
    AVG(signal_strength) as avg_signal_strength,
    AVG(latency_ms) as avg_latency_ms,
    AVG(upload_speed_mbps) as avg_upload_speed,
    AVG(download_speed_mbps) as avg_download_speed
FROM network_status 
GROUP BY connection_type;
```

## 🚲 자전거 대여소 데이터 확인

### 모든 자전거 대여소
```sql
SELECT 
    rental_station_name,
    address_street,
    latitude,
    longitude,
    bicycle_count,
    parking_spots,
    air_pump_available,
    repair_station_available
FROM bike_rental_station
ORDER BY rental_station_name;
```

### 특정 지역 주변 대여소
```sql
SELECT 
    rental_station_name,
    address_street,
    latitude,
    longitude,
    bicycle_count,
    SQRT(POWER(latitude - 37.5665, 2) + POWER(longitude - 126.9780, 2)) as distance_km
FROM bike_rental_station
WHERE SQRT(POWER(latitude - 37.5665, 2) + POWER(longitude - 126.9780, 2)) <= 5
ORDER BY distance_km;
```

## 📝 게시글 및 질문 데이터 확인

### 모든 게시글
```sql
SELECT 
    p.id,
    u.name as author_name,
    p.title,
    p.view_count,
    p.like_count,
    p.created_at
FROM posts p
JOIN users u ON p.user_id = u.id
ORDER BY p.created_at DESC;
```

### 모든 질문
```sql
SELECT 
    q.id,
    u.name as author_name,
    q.title,
    q.status,
    q.view_count,
    q.like_count,
    q.created_at
FROM question q
JOIN users u ON q.user_id = u.id
ORDER BY q.created_at DESC;
```

### 해결된 질문
```sql
SELECT 
    q.id,
    u.name as author_name,
    q.title,
    q.view_count,
    q.like_count,
    q.created_at
FROM question q
JOIN users u ON q.user_id = u.id
WHERE q.status = 'SOLVED'
ORDER BY q.created_at DESC;
```

## 🔍 고급 분석 쿼리

### 일별 장애물 신고 통계
```sql
SELECT 
    DATE(created_at) as report_date,
    COUNT(*) as total_reports,
    COUNT(CASE WHEN report_type = 'OBSTACLE' THEN 1 END) as obstacle_count,
    COUNT(CASE WHEN report_type = 'ROAD_DAMAGE' THEN 1 END) as road_damage_count,
    COUNT(CASE WHEN report_type = 'CONSTRUCTION' THEN 1 END) as construction_count,
    COUNT(CASE WHEN report_type = 'SLIPPERY' THEN 1 END) as slippery_count,
    COUNT(CASE WHEN report_type = 'ETC' THEN 1 END) as etc_count
FROM obstacle_report 
GROUP BY DATE(created_at)
ORDER BY report_date DESC;
```

### 시간대별 장애물 신고 패턴
```sql
SELECT 
    HOUR(created_at) as hour_of_day,
    COUNT(*) as report_count
FROM obstacle_report 
GROUP BY HOUR(created_at)
ORDER BY hour_of_day;
```

### 사용자별 라이딩 통계
```sql
SELECT 
    u.name,
    COUNT(rs.id) as total_sessions,
    AVG(rs.total_distance_km) as avg_distance,
    AVG(rs.avg_speed_kmh) as avg_speed,
    SUM(rs.calories_burned) as total_calories,
    COUNT(or.id) as total_obstacle_reports
FROM users u
LEFT JOIN ride_session rs ON u.id = rs.user_id
LEFT JOIN obstacle_report or ON u.id = or.user_id
GROUP BY u.id, u.name
ORDER BY total_sessions DESC;
```

### 지역별 장애물 신고 밀도
```sql
SELECT 
    ROUND(latitude, 2) as lat_rounded,
    ROUND(longitude, 2) as lng_rounded,
    COUNT(*) as report_count
FROM obstacle_report 
GROUP BY ROUND(latitude, 2), ROUND(longitude, 2)
HAVING COUNT(*) > 1
ORDER BY report_count DESC;
```

## 🎯 API 테스트용 쿼리

### 장애물 신고 API 테스트용 데이터
```sql
-- 새로운 장애물 신고 생성 테스트
INSERT INTO obstacle_report (user_id, latitude, longitude, report_type, description, status, created_at) 
VALUES (1, 37.5665, 126.9780, 'OBSTACLE', '테스트 장애물 신고', 'UNCONFIRMED', CURRENT_TIMESTAMP);

-- 주변 장애물 조회 테스트 (강남역 기준 5km 반경)
SELECT * FROM obstacle_report 
WHERE SQRT(POWER(latitude - 37.5665, 2) + POWER(longitude - 126.9780, 2)) <= 5;

-- 경로 상 장애물 조회 테스트 (강남역에서 홍대까지)
SELECT * FROM obstacle_report 
WHERE latitude BETWEEN 37.5572 AND 37.5665 
AND longitude BETWEEN 126.9254 AND 126.9780;
```

### 라이딩 세션 API 테스트용 데이터
```sql
-- 새로운 라이딩 세션 생성 테스트
INSERT INTO ride_session (user_id, started_at, status, last_location_lat, last_location_lng) 
VALUES (1, CURRENT_TIMESTAMP, 'ACTIVE', 37.5665, 126.9780);

-- 라이딩 경로 포인트 추가 테스트
INSERT INTO ride_point (ride_session_id, latitude, longitude, speed_kmh, recorded_at) 
VALUES (1, 37.5665, 126.9780, 20.0, CURRENT_TIMESTAMP);
```

## 📈 모니터링용 쿼리

### 실시간 통계
```sql
-- 오늘 생성된 장애물 신고 수
SELECT COUNT(*) as today_reports 
FROM obstacle_report 
WHERE DATE(created_at) = CURRENT_DATE;

-- 현재 활성 라이딩 세션 수
SELECT COUNT(*) as active_sessions 
FROM ride_session 
WHERE status = 'ACTIVE';

-- 평균 라이딩 거리
SELECT AVG(total_distance_km) as avg_distance 
FROM ride_session 
WHERE status = 'COMPLETED';
```

이 쿼리들을 사용하여 애플리케이션의 다양한 기능을 테스트하고 데이터를 분석할 수 있습니다! 🚀
