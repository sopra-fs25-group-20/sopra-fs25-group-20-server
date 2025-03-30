package ch.uzh.ifi.hase.soprafs25.model;


public class VoteCastDTO {

    private String voter;
    private boolean voteYes;
    private String roomCode;

    public String getVoter() {
        return voter;
    }

    public void setVoter(String voter) {
        this.voter = voter;
    }

    public boolean isVoteYes() {
        return voteYes;
    }

    public void setVoteYes(boolean voteYes) {
        this.voteYes = voteYes;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }
}