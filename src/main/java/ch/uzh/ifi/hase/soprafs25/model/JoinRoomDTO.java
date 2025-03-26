package ch.uzh.ifi.hase.soprafs25.model;

public class JoinRoomDTO {

    private String nickname;
    private String color;
    private String roomCode;

    public JoinRoomDTO(String nickname, String color, String roomCode) {
        this.nickname = nickname;
        this.color = color;
        this.roomCode = roomCode;
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

    public String getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }
}
