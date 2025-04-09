package ch.uzh.ifi.hase.soprafs25.exceptions;

public class CoordinatesLoadingException extends RuntimeException {
    public CoordinatesLoadingException(Throwable cause) {
        super(ErrorMessages.COORDINATES_LOADING_FAILED.getMessage(), cause);
    }

    @SuppressWarnings("unused")
    public String getErrorMessage() {
        return ErrorMessages.COORDINATES_LOADING_FAILED.getMessage();
    }
}
