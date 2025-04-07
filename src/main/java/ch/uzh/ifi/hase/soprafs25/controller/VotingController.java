package ch.uzh.ifi.hase.soprafs25.controller;

import ch.uzh.ifi.hase.soprafs25.entity.Room;
import ch.uzh.ifi.hase.soprafs25.entity.VotingSession;
import ch.uzh.ifi.hase.soprafs25.model.VoteStartDTO;
import ch.uzh.ifi.hase.soprafs25.model.VoteState;
import ch.uzh.ifi.hase.soprafs25.model.VoteCastDTO;
import ch.uzh.ifi.hase.soprafs25.model.VoteResultDTO;
import ch.uzh.ifi.hase.soprafs25.repository.RoomRepository;
import ch.uzh.ifi.hase.soprafs25.service.VotingService;
import ch.uzh.ifi.hase.soprafs25.session.VotingSessionManager;

import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Map;

import ch.uzh.ifi.hase.soprafs25.exceptions.VoteAlreadyInProgressException;

@Controller
public class VotingController {

    private final SimpMessagingTemplate messagingTemplate;
    private final VotingService votingService;
    private final RoomRepository roomRepository;

    public VotingController(SimpMessagingTemplate messagingTemplate,
                            VotingService votingService,
                            RoomRepository roomRepository) {
        this.messagingTemplate = messagingTemplate;
        this.votingService = votingService;
        this.roomRepository = roomRepository;
    }

    @MessageMapping("/vote/start")
    public void startVote(@Payload VoteStartDTO startDTO, Message<?> socketMessage) {
        Map<String, Object> session = (Map<String, Object>) socketMessage.getHeaders().get("simpSessionAttributes");
        if (session == null || !session.containsKey("code")) {
            throw new IllegalStateException("Missing session attributes in WebSocket message headers");
        }
        String roomCode = (String) session.get("code");

        if (VotingSessionManager.isActive(roomCode)) {
            throw new VoteAlreadyInProgressException(roomCode);
        }
        
        VotingSession votingSession = votingService.createVotingSession(roomCode, startDTO.getInitiator(), startDTO.getTarget());
        VotingSessionManager.addVotingSession(votingSession);
        messagingTemplate.convertAndSend("/topic/vote/begin/" + roomCode, startDTO);
        messagingTemplate.convertAndSend("/topic/vote/update/" + roomCode, votingSession.getVoteState().getVotes());
        
    }

    @MessageMapping("/vote/cast")
    public void castVote(@Payload VoteCastDTO castDTO, Message<?> socketMessage) {
        Map<String, Object> session = (Map<String, Object>) socketMessage.getHeaders().get("simpSessionAttributes");
        if (session == null || !session.containsKey("code")) {
            throw new IllegalStateException("Missing session attributes in WebSocket message headers");
        }
        String roomCode = (String) session.get("code");

        VotingSession votingSession = VotingSessionManager.getVotingSession(roomCode);

        if (!votingSession.hasVoted(castDTO.getVoter())) {
            votingSession.castVote(castDTO.getVoter(), castDTO.isVoteYes());
        }

        VoteState voteState = votingSession.getVoteState();
        messagingTemplate.convertAndSend("/topic/vote/update/" + roomCode, voteState.getVotes());

        Room room = roomRepository.findByCode(roomCode);
        if (room != null && voteState.getVotes().size() == room.getPlayers().size()) {
            VoteResultDTO result = new VoteResultDTO();
            
            result.setInitiator(votingSession.getInitiator());
            result.setTarget(votingSession.getTarget());
            result.setVotes(voteState.getVotes());

            messagingTemplate.convertAndSend("/topic/vote/result/" + roomCode, result);
            VotingSessionManager.removeVotingSession(roomCode);
        }
    }
}
