package ch.uzh.ifi.hase.soprafs25.exceptions;

public class UserNotAuthorizedException extends RuntimeException {
    public UserNotAuthorizedException(String authenticatedUsername, String targetUsername) {
        super(ErrorMessages.USER_NOT_AUTHORIZED.format(authenticatedUsername, targetUsername));
    }
}