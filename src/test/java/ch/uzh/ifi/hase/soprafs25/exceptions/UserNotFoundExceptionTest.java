package ch.uzh.ifi.hase.soprafs25.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserNotFoundExceptionTest {

    @Test
    @DisplayName("should format USER_NOT_FOUND with attribute")
    void shouldFormatUserNotFoundMessage() {
        String attribute = "id=123";
        UserNotFoundException ex = new UserNotFoundException(attribute);

        String expected = ErrorMessages.USER_NOT_FOUND.format(attribute);
        assertEquals(expected, ex.getMessage());
    }
}
