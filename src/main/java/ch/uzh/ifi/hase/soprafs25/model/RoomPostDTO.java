package ch.uzh.ifi.hase.soprafs25.model;

public class RoomPostDTO {

    private String nickname;

    public RoomPostDTO(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
