package ch.uzh.ifi.hase.soprafs25.model;

public class VoteStartDTO {

    private String initiator;
    private String target;
    private String roomCode;

    public String getInitiator () {
        return initiator;
    }
    public void setInitiator(String initiator) {
        this.initiator = initiator;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }
    
    public String getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }
}

