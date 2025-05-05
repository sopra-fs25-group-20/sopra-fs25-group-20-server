package ch.uzh.ifi.hase.soprafs25.model;

public class UserRegisterDTO {

    private String token;

    public UserRegisterDTO(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
