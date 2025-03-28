package ch.uzh.ifi.hase.soprafs25.model;

public class RoomPostDTO {

    private String nickname;
    private String code;

    public RoomPostDTO() {

    }
    
    public RoomPostDTO(String nickname, String code) {
        this.nickname = nickname;
        this.code = code;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
