package ch.uzh.ifi.hase.soprafs25.model;

public class PlayerUpdateDTO {

    private String nickname;
    private String color;
    private boolean isAdmin;

    public PlayerUpdateDTO() {}

    public PlayerUpdateDTO(String nickname, String color, boolean isAdmin) {
        this.nickname = nickname;
        this.color = color;
        this.isAdmin = isAdmin;
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

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
}
