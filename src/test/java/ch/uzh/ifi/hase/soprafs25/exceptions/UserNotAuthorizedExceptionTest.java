package ch.uzh.ifi.hase.soprafs25.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserNotAuthorizedExceptionTest {

    @Test
    @DisplayName("should format USER_NOT_AUTHORIZED with both usernames")
    void shouldFormatUserNotAuthorizedMessage() {
        String actor = "alice";
        String target = "bob";
        UserNotAuthorizedException ex = new UserNotAuthorizedException(actor, target);

        String expected = ErrorMessages.USER_NOT_AUTHORIZED.format(actor, target);
        assertEquals(expected, ex.getMessage());
    }
}
