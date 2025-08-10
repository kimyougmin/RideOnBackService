package com.ll.rideon;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.flyway.enabled=true",
    "spring.jpa.hibernate.ddl-auto=validate",
    "spring.jpa.show-sql=true",
    "spring.jpa.properties.hibernate.format_sql=true"
})
class BackendApplicationTests {

    @Test
    void contextLoads() {
        // 애플리케이션 컨텍스트가 정상적으로 로드되는지 확인
    }

    @Test
    void testApplicationStartup() {
        // 애플리케이션이 정상적으로 시작되는지 확인
    }
}
