package ch.uzh.ifi.hase.soprafs25.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserNotAuthenticatedExceptionTest {

    @Test
    @DisplayName("should format USER_NOT_AUTHENTICATED with detail")
    void shouldFormatUserNotAuthenticatedMessage() {
        String detail = "missing token";
        UserNotAuthenticatedException ex = new UserNotAuthenticatedException(detail);

        String expected = ErrorMessages.USER_NOT_AUTHENTICATED.format(detail);
        assertEquals(expected, ex.getMessage());
    }
}
