package ch.uzh.ifi.hase.soprafs25.session;

import java.util.Objects;

public class PlayerSessionKey {
    private final String nickname;
    private final String roomCode;

    public PlayerSessionKey(String nickname, String roomCode) {
        this.nickname = nickname.trim().toLowerCase();
        this.roomCode = roomCode.trim().toUpperCase();
    }

    public String getNickname() {
        return nickname;
    }

    public String getRoomCode() {
        return roomCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlayerSessionKey)) return false;
        PlayerSessionKey that = (PlayerSessionKey) o;
        return Objects.equals(nickname, that.nickname) &&
                Objects.equals(roomCode, that.roomCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nickname, roomCode);
    }
}
