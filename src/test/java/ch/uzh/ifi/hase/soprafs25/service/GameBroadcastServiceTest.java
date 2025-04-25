package ch.uzh.ifi.hase.soprafs25.service;

import ch.uzh.ifi.hase.soprafs25.constant.PlayerRole;
import ch.uzh.ifi.hase.soprafs25.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class GameBroadcastServiceTest {

    private SimpMessagingTemplate messagingTemplate;
    private GameReadService gameReadService;
    private GameBroadcastService gameBroadcastService;

    @BeforeEach
    void setup() {
        messagingTemplate = mock(SimpMessagingTemplate.class);
        gameReadService = mock(GameReadService.class);
        gameBroadcastService = new GameBroadcastService(messagingTemplate, gameReadService);
    }

    @Test
    void testBroadcastGamePhase() {
        GamePhaseDTO phaseDTO = new GamePhaseDTO("game");
        when(gameReadService.getGamePhase("ROOM123")).thenReturn(phaseDTO);
        
        gameBroadcastService.broadcastGamePhase("ROOM123");
        
        verify(messagingTemplate).convertAndSend("/topic/phase/ROOM123", phaseDTO);
}

    @Test
    void testBroadcastGameSettings() {
        GameSettingsDTO settingsDTO = new GameSettingsDTO(20, 60, 6, "europe");
        when(gameReadService.getGameSettings("ROOM123")).thenReturn(settingsDTO);

        gameBroadcastService.broadcastGameSettings("ROOM123");

        verify(messagingTemplate).convertAndSend("/topic/game/settings/ROOM123", settingsDTO);
    }

    @Test
    void testBroadcastPlayerList() {
        List<PlayerUpdateDTO> list = List.of(new PlayerUpdateDTO("testUser", "#FF0000", true));
        when(gameReadService.getPlayerUpdateList("ROOM123")).thenReturn(list);

        gameBroadcastService.broadcastPlayerList("ROOM123");

        verify(messagingTemplate).convertAndSend("/topic/players/ROOM123", list);
    }

    @Test
    void testBroadcastVoteStart() {
        gameBroadcastService.broadcastVoteStart("ROOM123", "targetUser");
        verify(messagingTemplate).convertAndSend(eq("/topic/vote/init/ROOM123"), any(VoteStartDTO.class));
    }

    @Test
    void testBroadcastVoteState() {
        gameBroadcastService.broadcastVoteState("ROOM123", 2, 3);
        verify(messagingTemplate).convertAndSend(eq("/topic/vote/cast/ROOM123"), any(VoteStateDTO.class));
    }

    @Test
    void testBroadcastChatMessage() {
        gameBroadcastService.broadcastChatMessage("ROOM123", "alice", "hello", "#ABCDEF");
        verify(messagingTemplate).convertAndSend(eq("/topic/chat/ROOM123"), any(ChatMessageDTO.class));
    }

    @Test
    void testBroadcastPersonalizedRole() {
        when(gameReadService.getPlayerRole("ROOM123", "alice")).thenReturn(PlayerRole.SPY);

        gameBroadcastService.broadcastPersonalizedRole("ROOM123", "alice");

        verify(messagingTemplate).convertAndSendToUser(eq("alice:ROOM123"), eq("/queue/role/ROOM123"), any(PlayerRoleDTO.class));
    }

    @Test
    void testBroadcastPersonalizedImageIndex() {
        when(gameReadService.getPersonalizedImageIndex("ROOM123", "bob")).thenReturn(2);

        gameBroadcastService.broadcastPersonalizedImageIndex("ROOM123", "bob");

        verify(messagingTemplate).convertAndSendToUser(eq("bob:ROOM123"), eq("/queue/highlighted/ROOM123"), any(ImageIndexDTO.class));
    }
}
