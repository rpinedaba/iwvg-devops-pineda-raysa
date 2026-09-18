package es.upm.miw.devops.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NotFoundExceptionTest {

    @Test
    void testNotFoundExceptionMessage() {
        NotFoundException exception = new NotFoundException("User id: 99");

        assertEquals("Not Found Exception. User id: 99", exception.getMessage());
        assertTrue(exception.getMessage().contains("User id: 99"));
    }
}
