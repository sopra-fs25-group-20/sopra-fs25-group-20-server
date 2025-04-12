package ch.uzh.ifi.hase.soprafs25.exceptions;

public class ImageLoadingException extends RuntimeException {
    public ImageLoadingException(Throwable cause) {
        super(ErrorMessages.IMAGE_LOADING_FAILED.getMessage(), cause);
    }

    public String getErrorMessage() {
        if (getCause() != null && getCause().getMessage() != null) {
            return getCause().getMessage();
        }
        return ErrorMessages.IMAGE_LOADING_FAILED.getMessage();
    }
}
