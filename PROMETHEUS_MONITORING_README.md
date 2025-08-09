# 🚴‍♂️ RideOn 프로메테우스 모니터링 가이드

## 📋 개요

RideOn 백엔드 애플리케이션은 Spring Boot Actuator와 Micrometer를 사용하여 프로메테우스 메트릭을 수집하고 노출합니다.

## 🔧 설정

### 1. 의존성

`build.gradle`에 다음 의존성이 포함되어 있습니다:

```gradle
implementation 'io.micrometer:micrometer-registry-prometheus'
implementation 'io.prometheus:simpleclient:0.16.0'
implementation 'io.prometheus:simpleclient_pushgateway:0.16.0'
implementation 'io.prometheus:simpleclient_hotspot:0.16.0'
```

### 2. Actuator 설정

`application.yml`에서 다음 설정이 활성화되어 있습니다:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    prometheus:
      enabled: true
  metrics:
    export:
      prometheus:
        enabled: true
```

## 📊 수집되는 메트릭

### 게시글 관련 메트릭

- `rideon.posts.created` - 생성된 게시글 수
- `rideon.posts.updated` - 수정된 게시글 수
- `rideon.posts.deleted` - 삭제된 게시글 수
- `rideon.posts.viewed` - 조회된 게시글 수
- `rideon.posts.creation.time` - 게시글 생성 소요 시간

### 댓글 관련 메트릭

- `rideon.comments.created` - 생성된 댓글 수
- `rideon.comments.updated` - 수정된 댓글 수
- `rideon.comments.deleted` - 삭제된 댓글 수
- `rideon.comments.creation.time` - 댓글 생성 소요 시간

### 라이딩 관련 메트릭

- `rideon.riding.sessions.created` - 생성된 라이딩 세션 수
- `rideon.riding.sessions.completed` - 완료된 라이딩 세션 수
- `rideon.riding.location.updates` - 위치 업데이트 수
- `rideon.riding.session.duration` - 라이딩 세션 지속 시간
- `rideon.riding.location.update.time` - 위치 업데이트 소요 시간

### 네트워크 관련 메트릭

- `rideon.network.disconnections` - 네트워크 연결 끊김 횟수
- `rideon.network.quality.changes` - 네트워크 품질 변경 횟수

### 사용자 관련 메트릭

- `rideon.users.login` - 사용자 로그인 횟수
- `rideon.users.registration` - 사용자 가입 횟수
- `rideon.authentication.failures` - 인증 실패 횟수

### API 관련 메트릭

- `rideon.api.response.time` - API 응답 시간

### 게이지 메트릭

- `rideon.users.active` - 활성 사용자 수
- `rideon.riding.sessions.active` - 활성 라이딩 세션 수
- `rideon.database.connections` - 데이터베이스 연결 수

## 🔍 메트릭 확인 방법

### 1. Actuator 엔드포인트

- **프로메테우스 메트릭**: `http://localhost:8080/actuator/prometheus`
- **일반 메트릭**: `http://localhost:8080/actuator/metrics`
- **헬스 체크**: `http://localhost:8080/actuator/health`

### 2. 프로메테우스 설정

`prometheus.yml` 파일을 사용하여 프로메테우스를 설정할 수 있습니다:

```bash
# 프로메테우스 다운로드 (Linux)
wget https://github.com/prometheus/prometheus/releases/download/v2.45.0/prometheus-2.45.0.linux-amd64.tar.gz
tar xvf prometheus-2.45.0.linux-amd64.tar.gz
cd prometheus-2.45.0.linux-amd64

# 프로메테우스 실행
./prometheus --config.file=prometheus.yml
```

### 3. Docker를 사용한 프로메테우스 실행

```bash
# 프로메테우스 컨테이너 실행
docker run -d \
  --name prometheus \
  -p 9090:9090 \
  -v $(pwd)/prometheus.yml:/etc/prometheus/prometheus.yml \
  prom/prometheus:latest
```

## 📈 대시보드 예시

### 주요 쿼리

1. **게시글 생성률**
   ```
   rate(rideon_posts_created_total[5m])
   ```

2. **라이딩 세션 생성률**
   ```
   rate(rideon_riding_sessions_created_total[5m])
   ```

3. **API 응답 시간 (95th percentile)**
   ```
   histogram_quantile(0.95, rate(rideon_api_response_time_seconds_bucket[5m]))
   ```

4. **네트워크 연결 끊김 횟수**
   ```
   rate(rideon_network_disconnections_total[5m])
   ```

5. **활성 라이딩 세션 수**
   ```
   rideon_riding_sessions_active
   ```

## 🚨 알림 설정 (선택사항)

### 알림 규칙 예시

`alerts.yml` 파일을 생성하여 알림 규칙을 설정할 수 있습니다:

```yaml
groups:
  - name: rideon_alerts
    rules:
      - alert: HighErrorRate
        expr: rate(rideon_authentication_failures_total[5m]) > 0.1
        for: 1m
        labels:
          severity: warning
        annotations:
          summary: "높은 인증 실패율"
          description: "인증 실패율이 10%를 초과했습니다."

      - alert: NetworkDisconnection
        expr: rate(rideon_network_disconnections_total[5m]) > 0.5
        for: 30s
        labels:
          severity: critical
        annotations:
          summary: "네트워크 연결 끊김 빈발"
          description: "네트워크 연결 끊김이 빈번하게 발생하고 있습니다."

      - alert: HighResponseTime
        expr: histogram_quantile(0.95, rate(rideon_api_response_time_seconds_bucket[5m])) > 2
        for: 2m
        labels:
          severity: warning
        annotations:
          summary: "높은 API 응답 시간"
          description: "API 응답 시간이 2초를 초과했습니다."
```

## 🔧 커스텀 메트릭 추가

새로운 메트릭을 추가하려면 `MetricsService`에 메서드를 추가하세요:

```java
// 카운터 추가
private Counter customCounter;

public void initializeMetrics() {
    customCounter = Counter.builder("rideon.custom.metric")
            .description("커스텀 메트릭 설명")
            .register(meterRegistry);
}

public void incrementCustomMetric() {
    customCounter.increment();
}
```

## 📊 Grafana 대시보드

Grafana를 사용하여 대시보드를 만들 수 있습니다:

1. **Grafana 설치 및 실행**
   ```bash
   docker run -d \
     --name grafana \
     -p 3000:3000 \
     grafana/grafana:latest
   ```

2. **프로메테우스 데이터 소스 추가**
   - URL: `http://prometheus:9090`

3. **대시보드 패널 예시**
   - 게시글 생성률 차트
   - 라이딩 세션 활성 상태
   - API 응답 시간 히스토그램
   - 네트워크 상태 모니터링

## 🛠️ 문제 해결

### 일반적인 문제

1. **메트릭이 수집되지 않는 경우**
   - Actuator 엔드포인트가 활성화되어 있는지 확인
   - 애플리케이션이 정상적으로 실행되고 있는지 확인

2. **프로메테우스에서 타겟을 찾을 수 없는 경우**
   - 애플리케이션 포트(8080)가 열려있는지 확인
   - 방화벽 설정 확인

3. **메트릭 이름이 표시되지 않는 경우**
   - 정규식 필터 확인
   - 메트릭 이름이 올바른지 확인

## 📝 로그 확인

메트릭 수집과 관련된 로그를 확인하려면:

```bash
# 애플리케이션 로그 확인
tail -f logs/application.log | grep -i metric

# 프로메테우스 로그 확인
docker logs prometheus
```

## 🔗 관련 링크

- [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)
- [Micrometer](https://micrometer.io/)
- [Prometheus](https://prometheus.io/docs/)
- [Grafana](https://grafana.com/docs/) 