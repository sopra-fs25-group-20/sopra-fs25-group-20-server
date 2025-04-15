package ch.uzh.ifi.hase.soprafs25.service;

import ch.uzh.ifi.hase.soprafs25.model.GamePhaseDTO;
import ch.uzh.ifi.hase.soprafs25.model.GameSettingsDTO;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class GameBroadcastService {

    private final SimpMessagingTemplate messagingTemplate;
    private final GameReadService gameReadService;

    public GameBroadcastService(SimpMessagingTemplate messagingTemplate, GameReadService gameReadService) {
        this.messagingTemplate = messagingTemplate;
        this.gameReadService = gameReadService;
    }

    public void broadcastGamePhase(String roomCode) {
        GamePhaseDTO gamePhaseDTO = gameReadService.getGamePhase(roomCode);

        messagingTemplate.convertAndSend("/topic/phase/" + roomCode, gamePhaseDTO);
    }

    public void broadcastGameSettings(String roomCode) {
        GameSettingsDTO gameSettingsDTO = gameReadService.getGameSettings(roomCode);

        messagingTemplate.convertAndSend("/topic/settings" + roomCode, gameSettingsDTO);
    }
}
