package ch.uzh.ifi.hase.soprafs25.model;

public class ChatMessageDTO {

    private String nickname;
    private String message;
    private String color;

    public ChatMessageDTO() {
        // No args constructor is needed for JSON deserialization
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
