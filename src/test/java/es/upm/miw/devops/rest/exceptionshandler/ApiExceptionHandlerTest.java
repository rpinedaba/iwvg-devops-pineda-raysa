package es.upm.miw.devops.rest.exceptionshandler;

import es.upm.miw.devops.exceptions.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import static org.junit.jupiter.api.Assertions.*;

class ApiExceptionHandlerTest {

    private final ApiExceptionHandler apiExceptionHandler = new ApiExceptionHandler();

    @Test
    void testNoResourceFoundRequestWithNotFoundException() {
        ErrorMessage errorMessage = this.apiExceptionHandler.noResourceFoundRequest(new NotFoundException("User id: 99"));

        assertAll(
                () -> assertEquals("NotFoundException", errorMessage.getError()),
                () -> assertEquals("Not Found Exception. User id: 99", errorMessage.getMessage()),
                () -> assertEquals(404, errorMessage.getCode())
        );
    }

    @Test
    void testNoResourceFoundRequestWithNoResourceFoundException() {
        NoResourceFoundException exception = new NoResourceFoundException(HttpMethod.GET, "/missing");

        ErrorMessage errorMessage = this.apiExceptionHandler.noResourceFoundRequest(exception);

        assertAll(
                () -> assertEquals("NoResourceFoundException", errorMessage.getError()),
                () -> assertTrue(errorMessage.getMessage().contains("/missing")),
                () -> assertEquals(404, errorMessage.getCode())
        );
    }

    @Test
    void testNoResourceFoundRequestWithResponseStatusException() {
        ResponseStatusException exception = new ResponseStatusException(HttpStatus.NOT_FOUND, "missing user");

        ErrorMessage errorMessage = this.apiExceptionHandler.noResourceFoundRequest(exception);

        assertAll(
                () -> assertEquals("ResponseStatusException", errorMessage.getError()),
                () -> assertTrue(errorMessage.getMessage().contains("missing user")),
                () -> assertEquals(404, errorMessage.getCode())
        );
    }

    @Test
    void testException() {
        ErrorMessage errorMessage = this.apiExceptionHandler.exception(new IllegalStateException("boom"));

        assertAll(
                () -> assertEquals("RuntimeException", errorMessage.getError()),
                () -> assertEquals("ERROR", errorMessage.getMessage()),
                () -> assertEquals(500, errorMessage.getCode())
        );
    }
}
