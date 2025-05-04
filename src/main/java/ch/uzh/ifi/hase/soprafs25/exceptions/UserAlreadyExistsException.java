package ch.uzh.ifi.hase.soprafs25.exceptions;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String attribute) {
        super(ErrorMessages.USER_ALREADY_EXISTS.format(attribute));
    }
}
