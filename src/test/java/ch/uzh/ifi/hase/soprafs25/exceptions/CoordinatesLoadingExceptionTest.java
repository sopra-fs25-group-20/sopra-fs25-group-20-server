package ch.uzh.ifi.hase.soprafs25.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CoordinatesLoadingExceptionTest {

    @Test
    @DisplayName("should return cause message when cause has message")
    void shouldReturnCauseMessageWhenPresent() {
        Throwable cause = new RuntimeException("underlying I/O error");
        CoordinatesLoadingException ex = new CoordinatesLoadingException(cause);

        assertEquals("underlying I/O error", ex.getErrorMessage());
    }

    @Test
    @DisplayName("should fallback to default when cause message is null")
    void shouldFallbackToDefaultWhenCauseMessageNull() {
        Throwable cause = new RuntimeException();
        CoordinatesLoadingException ex = new CoordinatesLoadingException(cause);

        String expected = ErrorMessages.COORDINATES_LOADING_FAILED.getMessage(); // :contentReference[oaicite:0]{index=0}:contentReference[oaicite:1]{index=1}
        assertEquals(expected, ex.getErrorMessage());
    }
}
