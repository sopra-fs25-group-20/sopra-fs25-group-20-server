package ch.uzh.ifi.hase.soprafs25.service;

import ch.uzh.ifi.hase.soprafs25.constant.PlayerRole;
import ch.uzh.ifi.hase.soprafs25.entity.VotingSession;
import ch.uzh.ifi.hase.soprafs25.model.*;
import ch.uzh.ifi.hase.soprafs25.session.VoteState;
import ch.uzh.ifi.hase.soprafs25.session.VotingSessionManager;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

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

    public void broadcastVoteState(String roomCode) {
        VotingSession votingSession = VotingSessionManager.getVotingSession(roomCode);
        VoteState voteState = votingSession.getVoteState();
        Map<String, Boolean> votes = voteState.getVotes();
        VoteStateDTO voteStateDTO = new VoteStateDTO(votes);

        messagingTemplate.convertAndSend("/topic/vote/cast" + roomCode, voteStateDTO);
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
