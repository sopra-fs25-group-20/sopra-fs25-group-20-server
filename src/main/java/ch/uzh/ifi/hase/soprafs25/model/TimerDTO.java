package ch.uzh.ifi.hase.soprafs25.model;

public class TimerDTO {

    private Integer remainingSeconds;

    public TimerDTO(Integer remainingSeconds) {
        this.remainingSeconds = remainingSeconds;
    }

    public Integer getRemainingSeconds() {
        return remainingSeconds;
    }

    public void setRemainingSeconds(Integer remainingSeconds) {
        this.remainingSeconds = remainingSeconds;
    }
}
