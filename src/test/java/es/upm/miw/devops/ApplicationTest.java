package es.upm.miw.devops;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class ApplicationTest {

    @Test
    void testMain() {
        assertDoesNotThrow(() -> Application.main(
                new String[]{"--spring.profiles.active=test", "--server.port=0"}));
    }
}
