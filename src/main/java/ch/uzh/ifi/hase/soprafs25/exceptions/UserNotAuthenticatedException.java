package ch.uzh.ifi.hase.soprafs25.exceptions;

    public class UserNotAuthenticatedException extends RuntimeException {
        public UserNotAuthenticatedException(String message) {
            super(ErrorMessages.USER_NOT_AUTHENTICATED.format(message));
        }
}
