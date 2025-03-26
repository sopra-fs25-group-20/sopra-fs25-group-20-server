package ch.uzh.ifi.hase.soprafs25.exceptions;

public enum ErrorMessages {
    ROOM_NOT_FOUND("Room with room code: %s was not found"),
    NICKNAME_ALREADY_IN_ROOM("Nickname %s is already in room %s");

    private final String message;

    private ErrorMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    // Using Object... to make it flexible with other future messages
    public String format(Object... args) {
        return String.format(message, args);
    }
}
