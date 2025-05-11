package ch.uzh.ifi.hase.soprafs25.model;

import ch.uzh.ifi.hase.soprafs25.entity.User;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PlayerUpdateDTO {

    private String nickname;
    private String color;
    private boolean isAdmin;

    @JsonProperty("account")
    private UserGetDTO user;

    public PlayerUpdateDTO() {}

    public PlayerUpdateDTO(String nickname, String color, boolean isAdmin, User user) {
        this.nickname = nickname;
        this.color = color;
        this.isAdmin = isAdmin;

        if (user != null) {
            this.user = new UserGetDTO(
                    user.getUsername(),
                    user.getWins(),
                    user.getDefeats(),
                    user.getGames(),
                    user.getCurrentStreak(),
                    user.getHighestStreak()
            );
        }
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

    public UserGetDTO getUser() {
        return user;
    }

    public void setUser(UserGetDTO user) {
        this.user = user;
    }
}
