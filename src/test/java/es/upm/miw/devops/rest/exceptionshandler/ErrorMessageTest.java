package es.upm.miw.devops.rest.exceptionshandler;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ErrorMessageTest {

    @Test
    void testErrorMessageFields() {
        IllegalArgumentException exception = new IllegalArgumentException("bad request");

        ErrorMessage errorMessage = new ErrorMessage(exception, 400);

        assertAll(
                () -> assertEquals("IllegalArgumentException", errorMessage.getError()),
                () -> assertEquals("bad request", errorMessage.getMessage()),
                () -> assertEquals(400, errorMessage.getCode())
        );
    }

    @Test
    void testToString() {
        RuntimeException exception = new RuntimeException("boom");
        ErrorMessage errorMessage = new ErrorMessage(exception, 500);

        String text = errorMessage.toString();

        assertAll(
                () -> assertTrue(text.contains("error='RuntimeException'")),
                () -> assertTrue(text.contains("message='boom'")),
                () -> assertTrue(text.contains("code=500"))
        );
    }
}
