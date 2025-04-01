package ch.uzh.ifi.hase.soprafs25.model;

public class VoteStartDTO {

    private String initiator;
    private String target;

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
}