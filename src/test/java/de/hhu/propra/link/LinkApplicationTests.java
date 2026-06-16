package de.hhu.propra.link;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = "SHORTY_ADMIN_PASSWORD=test-password")
class LinkApplicationTests {

    @Test
    void contextLoads() {
    }
}
