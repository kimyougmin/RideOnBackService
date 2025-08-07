# Riding API 문서

모바일 환경에서 실시간 위치 추적과 라이딩 정보를 저장하는 API입니다. 네트워크 불안정 상황에 대응하는 기능도 포함되어 있습니다.

## 인증 요구사항

**모든 라이딩 API는 인증이 필요합니다.** 로그인하지 않은 사용자는 라이딩 관련 기능을 사용할 수 없습니다.

### 인증이 필요한 기능
- 라이딩 세션 생성 (`POST /api/riding/sessions`)
- 라이딩 세션 종료 (`PUT /api/riding/sessions/{sessionId}/end`)
- 라이딩 세션 일시정지 (`PUT /api/riding/sessions/{sessionId}/pause`)
- 라이딩 세션 재개 (`PUT /api/riding/sessions/{sessionId}/resume`)
- 라이딩 세션 조회 (`GET /api/riding/sessions/{sessionId}`)
- 사용자 라이딩 세션 목록 (`GET /api/riding/sessions`)
- 활성 라이딩 세션 조회 (`GET /api/riding/sessions/active`)
- 위치 정보 업데이트 (`POST /api/riding/sessions/{sessionId}/location`)
- 라이딩 경로 조회 (`GET /api/riding/sessions/{sessionId}/locations`)
- 네트워크 상태 업데이트 (`POST /api/riding/sessions/{sessionId}/network`)
- 네트워크 권장사항 조회 (`GET /api/riding/sessions/{sessionId}/network-recommendation`)
- 오프라인 데이터 동기화 (`POST /api/riding/sessions/{sessionId}/sync-offline`)

## 주요 기능

### 1. 실시간 위치 추적
- GPS 위치 정보 실시간 수집
- 속도, 고도, 방향 정보 포함
- 네트워크 상태에 따른 오프라인 동기화

### 2. 네트워크 모니터링
- 연결 상태 실시간 모니터링
- 신호 강도, 패킷 손실률 추적
- 네트워크 품질에 따른 권장사항 제공

### 3. 오프라인 지원
- 네트워크 연결 끊김 시 로컬 저장
- 연결 복구 시 자동 동기화
- 데이터 손실 방지

## API 엔드포인트

### 1. 라이딩 세션 관리

#### 라이딩 세션 생성
- **URL**: `POST /api/riding/sessions`
- **Description**: 새로운 라이딩 세션을 생성합니다.
- **Request Body**:
```json
{}
```
- **Response**: `201 Created`
```json
{
    "id": 1,
    "userId": 1,
    "startedAt": "2024-01-01T12:00:00",
    "endedAt": null,
    "totalDistanceKm": null,
    "avgSpeedKmh": null,
    "maxSpeedKmh": null,
    "caloriesBurned": null,
    "status": "ACTIVE",
    "lastLocationLat": null,
    "lastLocationLng": null,
    "lastLocationTime": null,
    "networkQuality": null,
    "connectionLostCount": 0,
    "createdAt": "2024-01-01T12:00:00"
}
```

#### 라이딩 세션 종료
- **URL**: `PUT /api/riding/sessions/{sessionId}/end`
- **Description**: 라이딩 세션을 종료합니다.
- **Response**: `200 OK`

#### 라이딩 세션 일시정지
- **URL**: `PUT /api/riding/sessions/{sessionId}/pause`
- **Description**: 라이딩 세션을 일시정지합니다.
- **Response**: `200 OK`

#### 라이딩 세션 재개
- **URL**: `PUT /api/riding/sessions/{sessionId}/resume`
- **Description**: 일시정지된 라이딩 세션을 재개합니다.
- **Response**: `200 OK`

#### 라이딩 세션 조회
- **URL**: `GET /api/riding/sessions/{sessionId}`
- **Description**: 특정 라이딩 세션 정보를 조회합니다.
- **Response**: `200 OK` (라이딩 세션 생성 응답과 동일)

#### 사용자 라이딩 세션 목록
- **URL**: `GET /api/riding/sessions`
- **Description**: 사용자의 모든 라이딩 세션을 조회합니다.
- **Query Parameters**:
  - `page`: 페이지 번호 (기본값: 0)
  - `size`: 페이지 크기 (기본값: 10)
- **Response**: `200 OK`
```json
{
    "content": [
        {
            "id": 1,
            "userId": 1,
            "startedAt": "2024-01-01T12:00:00",
            "endedAt": "2024-01-01T14:00:00",
            "totalDistanceKm": 25.5,
            "avgSpeedKmh": 12.75,
            "maxSpeedKmh": 25.0,
            "caloriesBurned": 450.0,
            "status": "COMPLETED",
            "lastLocationLat": 37.5665,
            "lastLocationLng": 126.9780,
            "lastLocationTime": "2024-01-01T14:00:00",
            "networkQuality": "GOOD",
            "connectionLostCount": 2,
            "createdAt": "2024-01-01T12:00:00"
        }
    ],
    "totalPages": 1,
    "totalElements": 1,
    "pageable": {
        "pageNumber": 0,
        "pageSize": 10,
        "sort": [],
        "offset": 0,
        "paged": true,
        "unpaged": false
    },
    "first": true,
    "last": true,
    "numberOfElements": 1,
    "size": 10,
    "number": 0,
    "sort": [],
    "empty": false
}
```

#### 활성 라이딩 세션 조회
- **URL**: `GET /api/riding/sessions/active`
- **Description**: 현재 진행 중인 라이딩 세션을 조회합니다.
- **Response**: `200 OK` (라이딩 세션 생성 응답과 동일)

### 2. 위치 추적

#### 위치 정보 업데이트
- **URL**: `POST /api/riding/sessions/{sessionId}/location`
- **Description**: 실시간 위치 정보를 업데이트합니다.
- **Request Body**:
```json
{
    "latitude": 37.5665,
    "longitude": 126.9780,
    "speedKmh": 15.5,
    "altitude": 45.2,
    "accuracy": 5.0,
    "heading": 180.0,
    "recordedAt": "2024-01-01T12:30:00",
    "networkQuality": "GOOD",
    "batteryLevel": 85,
    "isOfflineSync": false
}
```
- **Response**: `200 OK`

#### 라이딩 경로 조회
- **URL**: `GET /api/riding/sessions/{sessionId}/locations`
- **Description**: 라이딩 세션의 모든 위치 정보를 조회합니다.
- **Response**: `200 OK`
```json
[
    {
        "id": 1,
        "rideSessionId": 1,
        "latitude": 37.5665,
        "longitude": 126.9780,
        "speedKmh": 15.5,
        "altitude": 45.2,
        "accuracy": 5.0,
        "heading": 180.0,
        "recordedAt": "2024-01-01T12:30:00",
        "networkQuality": "GOOD",
        "batteryLevel": 85,
        "isOfflineSync": false,
        "createdAt": "2024-01-01T12:30:00"
    }
]
```

### 3. 네트워크 모니터링

#### 네트워크 상태 업데이트
- **URL**: `POST /api/riding/sessions/{sessionId}/network`
- **Description**: 네트워크 상태 정보를 업데이트합니다.
- **Request Body**:
```json
{
    "connectionType": "4G",
    "signalStrength": 75,
    "isConnected": true,
    "latencyMs": 45,
    "uploadSpeedMbps": 2.5,
    "downloadSpeedMbps": 15.2,
    "packetLossPercentage": 0.5,
    "recordedAt": "2024-01-01T12:30:00"
}
```
- **Response**: `200 OK`

#### 네트워크 권장사항 조회
- **URL**: `GET /api/riding/sessions/{sessionId}/network-recommendation`
- **Description**: 현재 네트워크 상태에 따른 권장사항을 조회합니다.
- **Response**: `200 OK`
```json
{
    "action": "NORMAL",
    "message": "네트워크 상태가 양호합니다.",
    "priority": "LOW"
}
```

### 4. 오프라인 동기화

#### 오프라인 데이터 동기화
- **URL**: `POST /api/riding/sessions/{sessionId}/sync-offline`
- **Description**: 오프라인으로 저장된 데이터를 서버와 동기화합니다.
- **Response**: `200 OK`

## 네트워크 상태 분류

### NetworkQuality
- `DISCONNECTED`: 연결 끊김
- `POOR`: 신호 약함 (20% 미만) 또는 패킷 손실 높음 (10% 초과)
- `FAIR`: 신호 보통 (20-50%) 또는 패킷 손실 보통 (5-10%)
- `GOOD`: 신호 양호 (50-80%)
- `EXCELLENT`: 신호 우수 (80% 이상)
- `UNKNOWN`: 상태 불명

### NetworkPriority
- `LOW`: 낮은 우선순위
- `MEDIUM`: 중간 우선순위
- `HIGH`: 높은 우선순위

### 권장사항 (Action)
- `OFFLINE_MODE`: 오프라인 모드 전환
- `REDUCE_FREQUENCY`: 데이터 전송 빈도 감소
- `MONITOR`: 네트워크 상태 모니터링
- `NORMAL`: 정상 모드
- `UNKNOWN`: 상태 불명

## 오프라인 동기화 전략

### 1. 데이터 저장
- 네트워크 연결이 끊어지면 위치 데이터를 로컬에 저장
- `isOfflineSync` 플래그를 `true`로 설정

### 2. 연결 복구 감지
- 네트워크 상태가 `GOOD` 이상으로 개선되면 동기화 시작
- 저장된 데이터를 순차적으로 서버에 전송

### 3. 데이터 무결성
- 각 위치 데이터에 타임스탬프 포함
- 중복 전송 방지를 위한 동기화 상태 관리

## 에러 응답

### 401 Unauthorized (인증 실패)
```json
{
    "error": "Unauthorized",
    "message": "Full authentication is required to access this resource"
}
```

### 400 Bad Request
```json
{
    "status": 400,
    "message": "이미 진행 중인 라이딩 세션이 있습니다."
}
```

### 401 Unauthorized
```json
{
    "error": "Unauthorized",
    "message": "Full authentication is required to access this resource"
}
```

### 404 Not Found
```json
{
    "status": 404,
    "message": "라이딩 세션을 찾을 수 없습니다."
}
```

## 모바일 클라이언트 권장사항

### 1. 위치 업데이트 주기
- 네트워크 상태가 `GOOD` 이상: 5초마다
- 네트워크 상태가 `FAIR`: 10초마다
- 네트워크 상태가 `POOR`: 30초마다
- 네트워크 상태가 `DISCONNECTED`: 로컬 저장만

### 2. 배터리 최적화
- 배터리 레벨이 20% 미만일 때 업데이트 주기 증가
- 백그라운드 모드에서 위치 추적 최적화

### 3. 네트워크 대응
- 연결 끊김 시 즉시 오프라인 모드 전환
- 연결 복구 시 저장된 데이터 우선 동기화
- 네트워크 권장사항에 따른 동적 조정

## Swagger UI

API 문서는 Swagger UI를 통해 확인할 수 있습니다:
- **URL**: `http://localhost:8080/swagger-ui/index.html` 