# 장애물 신고 API 문서

라이딩 중 발견한 장애물을 신고하고 조회하는 API입니다.

## 📋 API 목록

### 1. 장애물 신고 생성
**POST** `/api/v1/obstacles/report`

라이딩 중 발견한 장애물을 신고합니다.

#### 요청 헤더
```
Authorization: Bearer {access_token}
Content-Type: application/json
```

#### 요청 본문
```json
{
  "latitude": 37.5665,
  "longitude": 126.9780,
  "reportType": "OBSTACLE",
  "description": "도로 중앙에 큰 바위가 있습니다",
  "image": "https://example.com/image.jpg"
}
```

#### 응답 (201 Created)
```json
{
  "id": 1,
  "userId": 1,
  "latitude": 37.5665,
  "longitude": 126.9780,
  "reportType": "OBSTACLE",
  "reportTypeDescription": "장애물",
  "description": "도로 중앙에 큰 바위가 있습니다",
  "status": "UNCONFIRMED",
  "statusDescription": "미확인",
  "image": "https://example.com/image.jpg",
  "createdAt": "2024-01-01T12:00:00"
}
```

### 2. 주변 장애물 조회
**GET** `/api/v1/obstacles/nearby?latitude=37.5665&longitude=126.9780&radius=5.0`

현재 위치 주변의 장애물을 조회합니다.

#### 요청 파라미터
- `latitude` (필수): 현재 위치 위도
- `longitude` (필수): 현재 위치 경도
- `radius` (필수): 조회 반경 (km)

#### 응답 (200 OK)
```json
[
  {
    "id": 1,
    "userId": 1,
    "latitude": 37.5665,
    "longitude": 126.9780,
    "reportType": "OBSTACLE",
    "reportTypeDescription": "장애물",
    "description": "도로 중앙에 큰 바위가 있습니다",
    "status": "UNCONFIRMED",
    "statusDescription": "미확인",
    "image": "https://example.com/image.jpg",
    "createdAt": "2024-01-01T12:00:00"
  }
]
```

### 3. 경로 상 장애물 조회
**GET** `/api/v1/obstacles/route?startLat=37.5665&startLng=126.9780&endLat=37.5666&endLng=126.9781`

특정 경로 상의 장애물을 조회합니다.

#### 요청 파라미터
- `startLat` (필수): 시작점 위도
- `startLng` (필수): 시작점 경도
- `endLat` (필수): 끝점 위도
- `endLng` (필수): 끝점 경도

#### 응답 (200 OK)
```json
[
  {
    "id": 1,
    "userId": 1,
    "latitude": 37.5665,
    "longitude": 126.9780,
    "reportType": "OBSTACLE",
    "reportTypeDescription": "장애물",
    "description": "도로 중앙에 큰 바위가 있습니다",
    "status": "UNCONFIRMED",
    "statusDescription": "미확인",
    "image": "https://example.com/image.jpg",
    "createdAt": "2024-01-01T12:00:00"
  }
]
```

### 4. 내 장애물 신고 목록
**GET** `/api/v1/obstacles/my-reports`

사용자가 신고한 장애물 목록을 조회합니다.

#### 요청 헤더
```
Authorization: Bearer {access_token}
```

#### 응답 (200 OK)
```json
[
  {
    "id": 1,
    "userId": 1,
    "latitude": 37.5665,
    "longitude": 126.9780,
    "reportType": "OBSTACLE",
    "reportTypeDescription": "장애물",
    "description": "도로 중앙에 큰 바위가 있습니다",
    "status": "UNCONFIRMED",
    "statusDescription": "미확인",
    "image": "https://example.com/image.jpg",
    "createdAt": "2024-01-01T12:00:00"
  }
]
```

### 5. 장애물 신고 상세 조회
**GET** `/api/v1/obstacles/{reportId}`

특정 장애물 신고의 상세 정보를 조회합니다.

#### 응답 (200 OK)
```json
{
  "id": 1,
  "userId": 1,
  "latitude": 37.5665,
  "longitude": 126.9780,
  "reportType": "OBSTACLE",
  "reportTypeDescription": "장애물",
  "description": "도로 중앙에 큰 바위가 있습니다",
  "status": "UNCONFIRMED",
  "statusDescription": "미확인",
  "image": "https://example.com/image.jpg",
  "createdAt": "2024-01-01T12:00:00"
}
```

### 6. 장애물 신고 상태 업데이트 (관리자)
**PUT** `/api/v1/obstacles/{reportId}/status?status=CONFIRMED`

장애물 신고의 상태를 업데이트합니다.

#### 요청 헤더
```
Authorization: Bearer {access_token}
```

#### 요청 파라미터
- `status` (필수): 새로운 상태 (UNCONFIRMED, CONFIRMED, RESOLVED)

#### 응답 (200 OK)
```json
{
  "id": 1,
  "userId": 1,
  "latitude": 37.5665,
  "longitude": 126.9780,
  "reportType": "OBSTACLE",
  "reportTypeDescription": "장애물",
  "description": "도로 중앙에 큰 바위가 있습니다",
  "status": "CONFIRMED",
  "statusDescription": "확인됨",
  "image": "https://example.com/image.jpg",
  "createdAt": "2024-01-01T12:00:00"
}
```

### 7. 최근 장애물 신고 조회
**GET** `/api/v1/obstacles/recent`

최근 30일간의 장애물 신고를 조회합니다.

#### 응답 (200 OK)
```json
[
  {
    "id": 1,
    "userId": 1,
    "latitude": 37.5665,
    "longitude": 126.9780,
    "reportType": "OBSTACLE",
    "reportTypeDescription": "장애물",
    "description": "도로 중앙에 큰 바위가 있습니다",
    "status": "UNCONFIRMED",
    "statusDescription": "미확인",
    "image": "https://example.com/image.jpg",
    "createdAt": "2024-01-01T12:00:00"
  }
]
```

### 8. 주변 장애물 개수 조회
**GET** `/api/v1/obstacles/count/nearby?latitude=37.5665&longitude=126.9780&radius=5.0`

특정 위치 주변의 장애물 신고 개수를 조회합니다.

#### 요청 파라미터
- `latitude` (필수): 위도
- `longitude` (필수): 경도
- `radius` (필수): 반경 (km)

#### 응답 (200 OK)
```json
3
```

### 9. 장애물 신고 타입 조회
**GET** `/api/v1/obstacles/types`

사용 가능한 장애물 신고 타입을 조회합니다.

#### 응답 (200 OK)
```json
[
  "OBSTACLE",
  "ROAD_DAMAGE",
  "CONSTRUCTION",
  "SLIPPERY",
  "ETC"
]
```

### 10. 장애물 신고 상태 조회
**GET** `/api/v1/obstacles/statuses`

사용 가능한 장애물 신고 상태를 조회합니다.

#### 응답 (200 OK)
```json
[
  "UNCONFIRMED",
  "CONFIRMED",
  "RESOLVED"
]
```

## 📊 데이터 모델

### 장애물 신고 타입 (ReportType)
- `OBSTACLE`: 장애물
- `ROAD_DAMAGE`: 도로 손상
- `CONSTRUCTION`: 공사
- `SLIPPERY`: 미끄러운 도로
- `ETC`: 기타

### 장애물 신고 상태 (ReportStatus)
- `UNCONFIRMED`: 미확인
- `CONFIRMED`: 확인됨
- `RESOLVED`: 해결됨

## 🔧 사용 예시

### 1. 장애물 신고하기
```bash
curl -X POST "http://localhost:8080/api/v1/obstacles/report" \
  -H "Authorization: Bearer {access_token}" \
  -H "Content-Type: application/json" \
  -d '{
    "latitude": 37.5665,
    "longitude": 126.9780,
    "reportType": "OBSTACLE",
    "description": "도로 중앙에 큰 바위가 있습니다",
    "image": "https://example.com/image.jpg"
  }'
```

### 2. 주변 장애물 조회하기
```bash
curl -X GET "http://localhost:8080/api/v1/obstacles/nearby?latitude=37.5665&longitude=126.9780&radius=5.0"
```

### 3. 경로 상 장애물 조회하기
```bash
curl -X GET "http://localhost:8080/api/v1/obstacles/route?startLat=37.5665&startLng=126.9780&endLat=37.5666&endLng=126.9781"
```

## 🗺️ 지도 연동

### 마커 표시
지도에서 장애물 신고를 마커로 표시할 때는 다음과 같은 정보를 사용합니다:

```javascript
// 마커 생성 예시
const marker = new google.maps.Marker({
  position: { lat: obstacle.latitude, lng: obstacle.longitude },
  title: obstacle.reportTypeDescription,
  icon: getObstacleIcon(obstacle.reportType), // 장애물 타입별 아이콘
  map: map
});

// 정보창 표시
const infoWindow = new google.maps.InfoWindow({
  content: `
    <div>
      <h3>${obstacle.reportTypeDescription}</h3>
      <p>${obstacle.description || '설명 없음'}</p>
      <p>상태: ${obstacle.statusDescription}</p>
      <p>신고일: ${new Date(obstacle.createdAt).toLocaleDateString()}</p>
    </div>
  `
});

marker.addListener('click', () => {
  infoWindow.open(map, marker);
});
```

### 장애물 타입별 아이콘
- `OBSTACLE`: 빨간색 마커
- `ROAD_DAMAGE`: 주황색 마커
- `CONSTRUCTION`: 노란색 마커
- `SLIPPERY`: 파란색 마커
- `ETC`: 회색 마커

## 📈 모니터링

### 프로메테우스 메트릭
- `rideon_obstacles_reports_created_total`: 생성된 장애물 신고 수
- `rideon_obstacles_reports_status_updated_total`: 상태 업데이트된 장애물 신고 수
- `rideon_obstacles_reports_creation_time_seconds`: 장애물 신고 생성 소요 시간

### 로그
모든 API 호출은 로그로 기록되며, 다음 정보가 포함됩니다:
- 사용자 ID
- 요청 위치 (위도, 경도)
- 조회 반경
- 처리 결과

## 🔒 보안

- 장애물 신고 생성 및 내 신고 목록 조회는 인증된 사용자만 가능
- 장애물 신고 상태 업데이트는 관리자만 가능
- 주변 장애물 조회는 인증 없이도 가능 (공개 정보)

## ⚠️ 에러 처리

### 400 Bad Request
- 필수 파라미터 누락
- 잘못된 위도/경도 값
- 잘못된 신고 타입

### 401 Unauthorized
- 인증 토큰 누락 또는 만료

### 403 Forbidden
- 권한 부족 (관리자 기능)

### 404 Not Found
- 존재하지 않는 장애물 신고 ID

### 500 Internal Server Error
- 서버 내부 오류
