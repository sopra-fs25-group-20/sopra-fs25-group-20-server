package ch.uzh.ifi.hase.soprafs25.exceptions;

public class TokenNotFoundException extends RuntimeException {
    public TokenNotFoundException() {
        super(ErrorMessages.TOKEN_NOT_FOUND.getMessage());
    }
}