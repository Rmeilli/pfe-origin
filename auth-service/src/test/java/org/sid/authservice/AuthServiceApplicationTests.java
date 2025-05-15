package org.sid.authservice;

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
    "keycloak.server-url=http://localhost:8080",
    "keycloak.realm=test-realm",
    "keycloak.client-id=test-client",
    "keycloak.client-secret=test-secret",
    "eureka.client.enabled=false"
})
class AuthServiceApplicationTests {

    @Test
    void contextLoads() {
        // Ce test vérifie simplement que le contexte Spring se charge correctement
    }
}
