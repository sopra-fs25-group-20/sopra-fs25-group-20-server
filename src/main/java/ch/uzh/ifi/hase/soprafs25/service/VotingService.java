package ch.uzh.ifi.hase.soprafs25.service;

import ch.uzh.ifi.hase.soprafs25.constant.GamePhase;
import ch.uzh.ifi.hase.soprafs25.constant.PlayerRole;
import ch.uzh.ifi.hase.soprafs25.entity.Game;
import ch.uzh.ifi.hase.soprafs25.entity.VotingSession;
import ch.uzh.ifi.hase.soprafs25.model.VoteResultDTO;
import ch.uzh.ifi.hase.soprafs25.model.VoteStartDTO;
import ch.uzh.ifi.hase.soprafs25.model.VoteStateDTO;
import ch.uzh.ifi.hase.soprafs25.session.VoteState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import ch.uzh.ifi.hase.soprafs25.exceptions.VoteAlreadyInProgressException;
import ch.uzh.ifi.hase.soprafs25.session.VotingSessionManager;

import java.util.Map;

@Service
public class VotingService {

    private static final Logger log = LoggerFactory.getLogger(VotingService.class);
    private final SimpMessagingTemplate messagingTemplate;
    private final GameService gameService;
    private final GameTimerService gameTimerService;

    public VotingService(SimpMessagingTemplate messagingTemplate,
                         GameService gameService, GameTimerService gameTimerService) {
        this.messagingTemplate = messagingTemplate;
        this.gameService = gameService;
        this.gameTimerService = gameTimerService;
    }

    public void createVotingSession(String roomCode, String initiator, String target) {
        if (VotingSessionManager.isActive(roomCode)) {
            throw new VoteAlreadyInProgressException(roomCode);
        }

        VotingSession session = new VotingSession(roomCode, initiator, target);
        VotingSessionManager.addVotingSession(session);
        gameService.advancePhase(roomCode, GamePhase.VOTE);

        messagingTemplate.convertAndSend("/topic/vote/init/" + roomCode, new VoteStartDTO(initiator, target));
        broadcastVoteState(roomCode);
    }

    public void castVote(String roomCode, String voter, boolean voteYes) {
        Game game = gameService.getGame(roomCode);
        validateGamePhase(game);

        VotingSession session = getActiveVotingSession(roomCode);
        session.getVoteState().addVote(voter, voteYes);

        broadcastVoteState(roomCode);
    }

    private void scheduleRoundTimeout(String roomCode) {
        int gameTimerSeconds = gameService.getGame(roomCode)
                .getGameSettings()
                .getVotingTimer();

        gameTimerService.schedule(
                roomCode + "-vote",
                () -> {
                    try {
                        if (VotingSessionManager.isActive(roomCode)) {
                            log.info("Vote timer expired for room: {}", roomCode);
                            endVoting(roomCode);
                        }
                    } catch (Exception e) {
                        log.error("Error while ending voting for room {}", roomCode, e);
                    }
                },
                gameTimerSeconds
        );
    }

    private void endVoting(String roomCode) {
        VotingSession votingSession = VotingSessionManager.getVotingSession(roomCode);
        VoteState voteState = votingSession.getVoteState();
        messagingTemplate.convertAndSend("/topic/vote/result/" + roomCode, createVoteResult(votingSession));

        if (voteState.countYesVotes() > voteState.countNoVotes()) {

            if (votedSpy(roomCode, votingSession.getTarget())) {
                createGameResult(roomCode, PlayerRole.INNOCENT);
            } else {
                createGameResult(roomCode, PlayerRole.SPY);
            }
            gameService.advancePhase(roomCode, GamePhase.SUMMARY);
        } else {
            gameService.advancePhase(roomCode, GamePhase.GAME);
            VotingSessionManager.removeVotingSession(roomCode);
        }
    }

    private boolean votedSpy(String roomCode, String target) {
        return gameService.getGame(roomCode).getRole(target) == PlayerRole.SPY;
    }

    private void createGameResult(String roomCode, PlayerRole winnerRole) {
        gameService.getGame(roomCode).getGameResult().setWinnerRole(winnerRole);
    }

    private void broadcastVoteState(String roomCode) {
        Map<String, Boolean> votes = getActiveVotingSession(roomCode).getVoteState().getVotes();
        messagingTemplate.convertAndSend("/topic/vote/cast/" + roomCode, new VoteStateDTO(votes));
    }

    private VoteResultDTO createVoteResult(VotingSession session) {
        VoteResultDTO dto = new VoteResultDTO();
        dto.setInitiator(session.getInitiator());
        dto.setTarget(session.getTarget());
        dto.setVotes(session.getVoteState().getVotes());
        return dto;
    }

    private VotingSession getActiveVotingSession(String roomCode) {
        return VotingSessionManager.getVotingSession(roomCode);
    }

    private void validateGamePhase(Game game) {
        if (game.getPhase() != GamePhase.VOTE) {
            throw new IllegalStateException("Can only vote during the VOTE phase");
        }
    }
}

