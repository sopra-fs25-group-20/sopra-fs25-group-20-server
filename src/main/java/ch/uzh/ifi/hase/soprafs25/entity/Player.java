package ch.uzh.ifi.hase.soprafs25.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "Player", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"nickname", "roomId"})
})
public class Player implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String nickname;

    @Column
    private String role;

    @Column
    private String color;

    @ManyToOne
    @JoinColumn(name = "roomId", nullable = false)
    private Room room;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }
}
