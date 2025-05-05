package ch.uzh.ifi.hase.soprafs25.exceptions;

public class InvalidPasswordException extends RuntimeException {
    public InvalidPasswordException(String username) {
        super(ErrorMessages.INVALID_PASSWORD.format(username));
    }
}

