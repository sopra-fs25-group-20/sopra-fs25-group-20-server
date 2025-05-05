package ch.uzh.ifi.hase.soprafs25.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ImageLoadingExceptionTest {

    @Test
    @DisplayName("should return cause message when cause has message")
    void shouldReturnCauseMessageWhenPresent() {
        Throwable cause = new IllegalStateException("bad image format");
        ImageLoadingException ex = new ImageLoadingException(cause);

        assertEquals("bad image format", ex.getErrorMessage());
    }

    @Test
    @DisplayName("should fallback to default when cause message is null")
    void shouldFallbackToDefaultWhenCauseMessageNull() {
        Throwable cause = new IllegalArgumentException();
        ImageLoadingException ex = new ImageLoadingException(cause);

        String expected = ErrorMessages.IMAGE_LOADING_FAILED.getMessage();
        assertEquals(expected, ex.getErrorMessage());
    }
}
