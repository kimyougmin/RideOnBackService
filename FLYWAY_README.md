# Flyway Database Migration

이 프로젝트는 Flyway를 사용하여 데이터베이스 스키마를 관리합니다.

## 설정

### 1. 의존성 추가
`build.gradle`에 Flyway 의존성이 이미 추가되어 있습니다:
```gradle
implementation 'org.flywaydb:flyway-core'
```

### 2. application.yml 설정
Flyway 설정이 `application.yml`에 추가되어 있습니다:
```yaml
spring:
  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true
    validate-on-migrate: true
```

### 3. Hibernate DDL 자동 생성 비활성화
JPA의 `ddl-auto`를 `validate`로 변경하여 Flyway가 스키마를 관리하도록 설정했습니다.

## 마이그레이션 파일

### V1__Create_initial_schema.sql
초기 데이터베이스 스키마를 생성하는 마이그레이션 파일입니다.

포함된 테이블들:
- `users` - 사용자 정보
- `posts` - 게시글
- `post_like` - 게시글 좋아요
- `category` - 카테고리
- `post_category` - 게시글-카테고리 연결
- `post_comment` - 게시글 댓글
- `riding_group` - 라이딩 그룹
- `group_member` - 그룹 멤버
- `group_message` - 그룹 메시지
- `question` - 질문
- `tags` - 태그
- `question_tag` - 질문-태그 연결
- `question_like` - 질문 좋아요
- `question_comment` - 질문 댓글
- `bike_rental_station` - 자전거 대여소
- `ride_session` - 라이딩 세션
- `ride_point` - 라이딩 경로 포인트
- `obstacle_report` - 장애물 신고
- `inquiry` - 문의사항

포함된 ENUM 타입들:
- `question_status` - 질문 상태 (SOLVED, UNSOLVED)
- `report_type` - 신고 타입 (OBSTACLE, ROAD_DAMAGE, CONSTRUCTION, SLIPPERY, ETC)
- `report_status` - 신고 상태 (UNCONFIRMED, CONFIRMED, RESOLVED)

## 사용 방법

### 1. 애플리케이션 실행
애플리케이션을 실행하면 Flyway가 자동으로 마이그레이션을 실행합니다:
```bash
./gradlew bootRun
```

### 2. 수동 마이그레이션 실행
수동으로 마이그레이션을 실행하려면:
```bash
./gradlew flywayMigrate
```

### 3. 마이그레이션 상태 확인
마이그레이션 상태를 확인하려면:
```bash
./gradlew flywayInfo
```

### 4. 새로운 마이그레이션 추가
새로운 마이그레이션을 추가하려면 `src/main/resources/db/migration/` 디렉토리에 
`V2__Description.sql` 형식의 파일을 생성하세요.

예시:
- `V2__Add_user_profile_fields.sql`
- `V3__Create_notification_table.sql`

## 주의사항

1. **기존 데이터베이스**: 기존 데이터베이스가 있다면 `baseline-on-migrate: true` 설정으로 인해 
   Flyway가 현재 상태를 기준점으로 설정합니다.

2. **PostGIS 확장**: `ride_point`와 `obstacle_report` 테이블에서 PostGIS 확장을 사용합니다.
   PostgreSQL에 PostGIS 확장이 설치되어 있어야 합니다.

3. **인덱스**: 성능을 위해 다음 인덱스들이 자동으로 생성됩니다:
   - `idx_question_status`
   - `idx_question_created_at`
   - `idx_obstacle_report_status`
   - `idx_obstacle_report_location` (GIST 인덱스)

4. **외래 키 제약 조건**: 모든 테이블 간의 관계가 적절한 외래 키 제약 조건으로 정의되어 있습니다. 