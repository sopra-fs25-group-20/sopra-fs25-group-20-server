package ch.uzh.ifi.hase.soprafs25.service;

import ch.uzh.ifi.hase.soprafs25.constant.GamePhase;
import ch.uzh.ifi.hase.soprafs25.constant.PlayerRole;
import ch.uzh.ifi.hase.soprafs25.entity.Game;
import ch.uzh.ifi.hase.soprafs25.entity.VotingSession;
import ch.uzh.ifi.hase.soprafs25.model.VoteStartDTO;
import ch.uzh.ifi.hase.soprafs25.model.VoteStateDTO;
import ch.uzh.ifi.hase.soprafs25.session.GameSessionManager;
import ch.uzh.ifi.hase.soprafs25.session.VoteState;
import org.springframework.stereotype.Service;
import ch.uzh.ifi.hase.soprafs25.exceptions.VoteAlreadyInProgressException;
import ch.uzh.ifi.hase.soprafs25.session.VotingSessionManager;

@Service
public class VotingService {

    private final GameService gameService;
    private final GameBroadcastService gameBroadcastService;
    private final GameReadService gameReadService;
    private final GameTimerService gameTimerService;

    public VotingService(GameService gameService,
                         GameBroadcastService gameBroadcastService,
                         GameReadService gameReadService,
                         GameTimerService gameTimerService) {
        this.gameService = gameService;
        this.gameBroadcastService = gameBroadcastService;
        this.gameReadService = gameReadService;
        this.gameTimerService = gameTimerService;
    }

    public void initializeVotingSession(String roomCode, String initiator, String target) {
        createVotingSession(roomCode, initiator, target);
        gameService.advancePhase(roomCode, GamePhase.VOTE);

        gameBroadcastService.broadcastVoteStart(roomCode, target);
        gameBroadcastService.broadcastVoteState(roomCode, 0, 0);
    }

    public void castVote(String roomCode, String voter, boolean voteYes) {
        VotingSession session = getActiveVotingSession(roomCode);
        VoteState voteState = session.getVoteState();
        voteState.addVote(voter, voteYes);

        if (isVoteComplete(roomCode)) {
            handleVotingResults(roomCode);
            endVotingSession(roomCode);
        } else {
            int numberYesVotes = voteState.countYesVotes();
            int numberNoVotes = voteState.countNoVotes();
            gameBroadcastService.broadcastVoteState(roomCode, numberYesVotes, numberNoVotes);
        }
    }

    public VoteStartDTO getVoteTarget(String roomCode) {
        try {
            VotingSession session = getActiveVotingSession(roomCode);
            return new VoteStartDTO(session.getTarget());
        } catch (IllegalStateException e) {
            return new VoteStartDTO(null);
        }
    }

    public VoteStateDTO getVoteState(String roomCode) {
        try {
            VotingSession session = getActiveVotingSession(roomCode);
            VoteState voteState = session.getVoteState();

            int numberYesVotes = voteState.countYesVotes();
            int numberNoVotes = voteState.countNoVotes();
            return new VoteStateDTO(numberYesVotes, numberNoVotes);
        } catch (IllegalStateException e) {
            return new VoteStateDTO(0, 0);
        }
    }

    private void createVotingSession(String roomCode, String initiator, String target) {
        if (VotingSessionManager.isActive(roomCode)) {
            throw new VoteAlreadyInProgressException(roomCode);
        }
        VotingSession session = new VotingSession(roomCode, initiator, target);
        VotingSessionManager.addVotingSession(session);
        scheduleVotingTimer(roomCode);
    }

    private void scheduleVotingTimer(String roomCode) {
        Game game = getGame(roomCode);
        Runnable taskForVoteTimeOut = () -> {
            handleVotingResults(roomCode);
            endVotingSession(roomCode);
        };
        String timerId = roomCode + "_vote";
        int votingTimer = game.getGameSettings().getVotingTimer();
        gameTimerService.scheduleTask(timerId, votingTimer, taskForVoteTimeOut);
    }

    private VotingSession getActiveVotingSession(String roomCode) {
        return VotingSessionManager.getVotingSession(roomCode);
    }

    private void endVotingSession(String roomCode) {
        if (VotingSessionManager.isActive(roomCode)){
            VotingSessionManager.removeVotingSession(roomCode);
        }
        gameTimerService.cancelTask(roomCode + "_vote", "Vote ended");
    }

    private boolean isVoteComplete(String roomCode) {
        VotingSession session = getActiveVotingSession(roomCode);
        return session.getVoteState().getVotes().size() == gameReadService.getPlayerCount(roomCode);
    }

    private void handleVotingResults(String roomCode) {
        VotingSession votingSession = getActiveVotingSession(roomCode);
        VoteState voteState = votingSession.getVoteState();
        String targetNickname = votingSession.getTarget();

        if (voteState.countYesVotes() > voteState.countNoVotes()) {
            Game game = getGame(roomCode);

            if (votedSpy(roomCode, targetNickname)) {
                game.setGameResult(null, targetNickname, PlayerRole.INNOCENT);
            } else {
                game.setGameResult(null, targetNickname, PlayerRole.SPY);
            }

            gameService.advancePhase(roomCode, GamePhase.SUMMARY);
        } else {
            gameService.advancePhase(roomCode, GamePhase.GAME);
        }
    }

    private boolean votedSpy(String roomCode, String target) {
        return getGame(roomCode).getRole(target) == PlayerRole.SPY;
    }

    private Game getGame(String roomCode) {
        return GameSessionManager.getGameSession(roomCode);
    }
}
