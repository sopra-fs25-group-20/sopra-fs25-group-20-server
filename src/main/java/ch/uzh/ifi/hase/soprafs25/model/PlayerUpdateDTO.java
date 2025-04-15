package ch.uzh.ifi.hase.soprafs25.model;

public class PlayerUpdateDTO {

    private String nickname;
    private String color;

    public PlayerUpdateDTO() {}

    public PlayerUpdateDTO(String nickname, String color) {
        this.nickname = nickname;
        this.color = color;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
