package ch.uzh.ifi.hase.soprafs25.model;

public class VoteStartDTO {

    private String target;

    public VoteStartDTO() {}

    public VoteStartDTO(String target) {
        this.target = target;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }
}