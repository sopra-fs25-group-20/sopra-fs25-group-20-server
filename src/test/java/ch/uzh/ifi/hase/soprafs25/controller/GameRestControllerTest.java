package ch.uzh.ifi.hase.soprafs25.controller;

import ch.uzh.ifi.hase.soprafs25.constant.GamePhase;
import ch.uzh.ifi.hase.soprafs25.constant.PlayerRole;
import ch.uzh.ifi.hase.soprafs25.entity.GameResult;
import ch.uzh.ifi.hase.soprafs25.model.*;
import ch.uzh.ifi.hase.soprafs25.service.GameReadService;
import ch.uzh.ifi.hase.soprafs25.service.PlayerConnectionService;
import ch.uzh.ifi.hase.soprafs25.service.VotingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GameRestController.class)
class GameRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PlayerConnectionService playerConnectionService;

    @MockBean
    private GameReadService gameReadService;

    @MockBean
    private VotingService votingService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void getPlayerList_ReturnsPlayerListDTOs() throws Exception {
        List<PlayerUpdateDTO> players = List.of(
                new PlayerUpdateDTO("Alice", "Red", true, null),
                new PlayerUpdateDTO("Bob", "Blue", false, null)
        );
        when(playerConnectionService.getPlayerListDTO("room123")).thenReturn(players);

        mockMvc.perform(get("/players/room123"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(players)));
    }

    @Test
    void getGamePhase_ReturnsLowerCasePhase() throws Exception {
        GamePhaseDTO dto = new GamePhaseDTO("game");
        when(gameReadService.getGamePhase("room123")).thenReturn(dto);

        mockMvc.perform(get("/phase/room123"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));
    }

    @Test
    void getGameSettings_ReturnsSettingsDTO() throws Exception {
        GameSettingsDTO dto = new GameSettingsDTO(30, 60, 5, "europe");
        when(gameReadService.getGameSettings("room123")).thenReturn(dto);

        mockMvc.perform(get("/settings/room123"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));
    }

    @Test
    void getResults_ReturnsGameResultDTO() throws Exception {
        Map<String, PlayerRole> roles = Map.of("Alice", PlayerRole.INNOCENT, "Bob", PlayerRole.SPY);
        GameResult fakeResult = new GameResult(2, null, PlayerRole.SPY);
        GameResultDTO dto = new GameResultDTO(roles, 1, fakeResult);
        when(gameReadService.getGameResult("room123")).thenReturn(dto);

        mockMvc.perform(get("/game/result/room123"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));
    }

    @Test
    void getVoteTarget_ReturnsTargetDTO() throws Exception {
        VoteStartDTO dto = new VoteStartDTO("Charlie");
        when(votingService.getVoteTarget("room123")).thenReturn(dto);

        mockMvc.perform(get("/game/vote/target/room123"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));
    }

    @Test
    void getVoteState_ReturnsStateDTO() throws Exception {
        VoteStateDTO dto = new VoteStateDTO(2, 1);
        when(votingService.getVoteState("room123")).thenReturn(dto);

        mockMvc.perform(get("/game/vote/state/room123"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));
    }

    @Test
    void getTimer_ReturnsTimerDTO() throws Exception {
        TimerDTO dto = new TimerDTO(25);
        when(gameReadService.getTimer("room123", GamePhase.GAME)).thenReturn(dto);

        mockMvc.perform(get("/game/timer/room123?phase=GAME"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));
    }
}
