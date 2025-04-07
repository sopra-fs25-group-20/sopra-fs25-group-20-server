package ch.uzh.ifi.hase.soprafs25.exceptions;

public class VoteAlreadyInProgressException extends RuntimeException {
    public VoteAlreadyInProgressException(String roomCode) {
        super(ErrorMessages.VOTE_ALREADY_IN_PROGRESS.format(roomCode));
    }
}