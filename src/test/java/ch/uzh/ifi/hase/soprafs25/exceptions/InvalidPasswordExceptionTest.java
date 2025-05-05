package ch.uzh.ifi.hase.soprafs25.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class InvalidPasswordExceptionTest {

    @Test
    @DisplayName("should format INVALID_PASSWORD with username")
    void shouldFormatInvalidPasswordMessage() {
        String username = "charlie";
        InvalidPasswordException ex = new InvalidPasswordException(username);

        String expected = ErrorMessages.INVALID_PASSWORD.format(username); // :contentReference[oaicite:8]{index=8}:contentReference[oaicite:9]{index=9}
        assertEquals(expected, ex.getMessage());
    }
}
