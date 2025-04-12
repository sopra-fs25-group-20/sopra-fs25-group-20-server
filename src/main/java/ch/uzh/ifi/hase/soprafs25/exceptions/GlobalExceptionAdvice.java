package ch.uzh.ifi.hase.soprafs25.exceptions;

import ch.uzh.ifi.hase.soprafs25.model.ErrorResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@ControllerAdvice
public class GlobalExceptionAdvice {

    private final Logger log = LoggerFactory.getLogger(GlobalExceptionAdvice.class);

    @ExceptionHandler(RoomNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND) // 404
    @ResponseBody
    public ErrorResponseDTO handleRoomNotFoundException(RoomNotFoundException ex) {
        log.error("RoomNotFoundException -> caught:", ex);
        return new ErrorResponseDTO(ex.getMessage());
    }

    @ExceptionHandler(NicknameAlreadyInRoomException.class)
    @ResponseStatus(HttpStatus.CONFLICT) // 409
    @ResponseBody
    public ErrorResponseDTO handleNicknameAlreadyInRoomException(NicknameAlreadyInRoomException ex) {
        log.error("NicknameAlreadyInRoomException -> caught:", ex);
        return new ErrorResponseDTO(ex.getMessage());
    }

    @ExceptionHandler(ImageLoadingException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // 500
    @ResponseBody
    public ErrorResponseDTO handleImageLoadingException(ImageLoadingException ex) {
        log.error("ImageLoadingException -> caught:", ex.getCause());
        return new ErrorResponseDTO(ex.getErrorMessage());
    }

    @ExceptionHandler(VoteAlreadyInProgressException.class)
    @ResponseStatus(HttpStatus.CONFLICT) // 409
    @ResponseBody
    public ErrorResponseDTO handleVoteAlreadyInProgressException(VoteAlreadyInProgressException ex) {
        log.error("VoteAlreadyInProgressException -> caught:", ex);
        return new ErrorResponseDTO(ex.getMessage());
    }

    @ExceptionHandler(CoordinatesLoadingException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // 500
    @ResponseBody
    public ErrorResponseDTO handleCoordinatesLoadingException(CoordinatesLoadingException ex) {
        log.error("CoordinatesLoadingException-> caught:", ex.getCause());
        return new ErrorResponseDTO(ex.getErrorMessage());
    }
}
