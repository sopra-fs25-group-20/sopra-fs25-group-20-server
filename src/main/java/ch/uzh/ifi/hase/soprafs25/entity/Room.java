package ch.uzh.ifi.hase.soprafs25.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Room")
public class Room implements Serializable {

    @Id
    @GeneratedValue
    private Long roomId;

    @Column(nullable = false, unique = true)
    private String code;

    @Column
    private Long adminPlayerId;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Player> players = new ArrayList<>();

    public void addPlayer(Player player) {
        this.players.add(player);
        player.setRoom(this);
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getAdminPlayerId() {
        return adminPlayerId;
    }

    public void setAdminPlayerId(Long adminPlayerID) {
        this.adminPlayerId = adminPlayerID;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }
}