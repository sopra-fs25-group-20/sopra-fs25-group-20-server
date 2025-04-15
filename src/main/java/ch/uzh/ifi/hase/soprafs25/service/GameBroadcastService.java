package ch.uzh.ifi.hase.soprafs25.service;

import ch.uzh.ifi.hase.soprafs25.constant.PlayerRole;
import ch.uzh.ifi.hase.soprafs25.model.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameBroadcastService {

    private final SimpMessagingTemplate messagingTemplate;
    private final GameReadService gameReadService;

    public GameBroadcastService(SimpMessagingTemplate messagingTemplate,
                                GameReadService gameReadService) {
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

    public void broadcastPlayerList(String roomCode) {
        List<PlayerUpdateDTO> listPlayerUpdateDTO = gameReadService.getPlayerUpdateList(roomCode);

        messagingTemplate.convertAndSend("/topic/players/" + roomCode, listPlayerUpdateDTO);
    }

    public void broadcastPersonalizedRole(String roomCode, String nickname) {
        PlayerRole playerRole = gameReadService.getPlayerRole(roomCode, nickname);
        PlayerRoleDTO playerRoleDTO = new PlayerRoleDTO(playerRole.name().toLowerCase());

        messagingTemplate.convertAndSendToUser(
                nickname + ":" + roomCode,
                "/queue/role/" + roomCode,
                playerRoleDTO
        );
    }

    public void broadcastPersonalizedImageIndex(String roomCode, String nickname) {
        int highlightedImageIndex = gameReadService.getPersonalizedImageIndex(roomCode, nickname);
        ImageIndexDTO imageIndexDTO = new ImageIndexDTO(highlightedImageIndex);

        messagingTemplate.convertAndSendToUser(
                nickname + ":" + roomCode,
                "/queue/highlighted/" + roomCode,
                imageIndexDTO
        );
    }
}
