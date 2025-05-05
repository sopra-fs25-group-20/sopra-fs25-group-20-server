package ch.uzh.ifi.hase.soprafs25.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String attribute) {
        super(ErrorMessages.USER_NOT_FOUND.format(attribute));
    }
}