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

    public void broadcastVoteStart(String roomCode, String target) {
        VoteStartDTO voteStartDTO = new VoteStartDTO(target);

        messagingTemplate.convertAndSend("/topic/vote/init/" + roomCode, voteStartDTO);
    }

    public void broadcastVoteState(String roomCode, int numberYesVotes, int numberNoVotes) {
        VoteStateDTO voteStateDTO = new VoteStateDTO(numberYesVotes, numberNoVotes);

        messagingTemplate.convertAndSend("/topic/vote/cast/" + roomCode, voteStateDTO);
    }

    public void broadcastChatMessage(String roomCode, String sender, String message, String color) {
        ChatMessageDTO chatMessageDTO = new ChatMessageDTO(sender, message, color);

        messagingTemplate.convertAndSend("/topic/chat/" + roomCode, chatMessageDTO);
    }

    public void broadcastPersonalizedRole(String roomCode, String nickname) {
        PlayerRole playerRole = gameReadService.getPlayerRole(roomCode, nickname);
        PlayerRoleDTO playerRoleDTO = new PlayerRoleDTO(
                playerRole == null
                ? null
                : playerRole.name().toLowerCase()
        );

        messagingTemplate.convertAndSendToUser(
                nickname + ":" + roomCode,
                "/queue/role/" + roomCode,
                playerRoleDTO
        );
    }

    public void broadcastPersonalizedImageIndex(String roomCode, String nickname) {
        Integer highlightedImageIndex = gameReadService.getPersonalizedImageIndex(roomCode, nickname);
        ImageIndexDTO imageIndexDTO = new ImageIndexDTO(highlightedImageIndex);

        messagingTemplate.convertAndSendToUser(
                nickname + ":" + roomCode,
                "/queue/highlighted/" + roomCode,
                imageIndexDTO
        );
    }
}
