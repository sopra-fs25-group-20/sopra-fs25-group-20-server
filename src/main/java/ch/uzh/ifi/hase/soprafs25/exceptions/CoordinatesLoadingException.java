package ch.uzh.ifi.hase.soprafs25.exceptions;

public class CoordinatesLoadingException extends RuntimeException {
    public CoordinatesLoadingException(Throwable cause) {
        super(ErrorMessages.COORDINATES_LOADING_FAILED.getMessage(), cause);
    }

    public String getErrorMessage() {
        if (getCause() != null && getCause().getMessage() != null) {
            return getCause().getMessage();
        }
        return ErrorMessages.COORDINATES_LOADING_FAILED.getMessage();
    }
}
