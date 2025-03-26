package ch.uzh.ifi.hase.soprafs25.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "Room")
public class Room implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long roomId;

    @Column(nullable = false, unique = true)
    private String code;

    @Column
    private Long adminPlayerId;

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
}