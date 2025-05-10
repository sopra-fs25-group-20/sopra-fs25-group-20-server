package ch.uzh.ifi.hase.soprafs25.model;

public class RoomPostDTO {

    private String nickname;
    private String code;
    private String token;

    public RoomPostDTO() {

    }
    
    public RoomPostDTO(String nickname, String code, String token) {
        this.nickname = nickname;
        this.code = code;
        this.token = token;
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
