package ch.uzh.ifi.hase.soprafs25.integration;

import ch.uzh.ifi.hase.soprafs25.entity.Player;
import ch.uzh.ifi.hase.soprafs25.entity.Room;
import ch.uzh.ifi.hase.soprafs25.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs25.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PlayerListIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @BeforeEach
    void setupRoomWithPlayers() {
        Room testRoom = new Room();
        testRoom.setCode("ROOM123");
        roomRepository.save(testRoom);

        Player player1 = new Player();
        player1.setNickname("Alice");
        player1.setColor("red");
        player1.setRoom(testRoom);
        playerRepository.save(player1);

        Player player2 = new Player();
        player2.setNickname("John");
        player2.setColor("blue");
        player2.setRoom(testRoom);
        playerRepository.save(player2);
    }

    @Test
    void getPlayers_validRoomCode_returnsCorrectPlayerList() throws Exception {
        assertEquals(2, playerRepository.findAll().size());

        mockMvc.perform(get("/players/ROOM123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[?(@.nickname=='Alice')].color").value(hasItem("red")))
                .andExpect(jsonPath("$[?(@.nickname=='John')].color").value(hasItem("blue")));
    }
}
