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
}
