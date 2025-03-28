package ch.uzh.ifi.hase.soprafs25.model;

public class CreateRoomDTO {

    private String nickname;
    private String roomCode;

    public CreateRoomDTO(String nickname, String roomCode) {
        this.nickname = nickname;
        this.roomCode = roomCode;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }
}
