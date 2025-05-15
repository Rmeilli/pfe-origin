package org.sid.creapp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=password",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:8080/test",
    "eureka.client.enabled=false"
})
class CrEappApplicationTests {

    @Test
    void contextLoads() {
        // Ce test vérifie simplement que le contexte Spring se charge correctement
    }
}
