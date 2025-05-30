package ch.uzh.ifi.hase.soprafs25.exceptions;

public enum ErrorMessages {
    ROOM_NOT_FOUND("Room with room code: %s was not found"),
    NICKNAME_ALREADY_IN_ROOM("Nickname %s is already in room %s"),
    IMAGE_LOADING_FAILED("Failed to load image"),
    VOTE_ALREADY_IN_PROGRESS("A voting session is already in progress for room: %s"),
    COORDINATES_LOADING_FAILED("Failed to load coordinates"),
    USER_ALREADY_EXISTS("User with %s already exists"),
    USER_NOT_FOUND("User with %s not found"),
    INVALID_PASSWORD("Invalid password for the user %s"),
    TOKEN_NOT_FOUND("No token was provided"),
    USER_NOT_AUTHENTICATED("Not logged in: %s"),
    USER_NOT_AUTHORIZED("User with username %s is not authorized to change the user with username %s");

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
