package ch.uzh.ifi.hase.soprafs25.controller;

import ch.uzh.ifi.hase.soprafs25.entity.Room;
import ch.uzh.ifi.hase.soprafs25.model.VoteStartDTO;
import ch.uzh.ifi.hase.soprafs25.model.VoteCastDTO;
import ch.uzh.ifi.hase.soprafs25.model.VoteResultDTO;
import ch.uzh.ifi.hase.soprafs25.repository.RoomRepository;
import ch.uzh.ifi.hase.soprafs25.service.VotingService;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Map;

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

        if (!votingService.isVoteSessionActive(roomCode)) {
            votingService.startVote(roomCode, startDTO.getInitiator(), startDTO.getTarget());
            messagingTemplate.convertAndSend("/topic/vote/update/" + roomCode, startDTO);
        }
    }

    @MessageMapping("/vote/cast")
    public void castVote(@Payload VoteCastDTO castDTO, Message<?> socketMessage) {
        Map<String, Object> session = (Map<String, Object>) socketMessage.getHeaders().get("simpSessionAttributes");
        if (session == null || !session.containsKey("code")) {
            throw new IllegalStateException("Missing session attributes in WebSocket message headers");
        }
        String roomCode = (String) session.get("code");

        if (!votingService.hasVoted(roomCode, castDTO.getVoter())) {
            votingService.castVote(roomCode, castDTO.getVoter(), castDTO.isVoteYes());
        }

        Map<String, Boolean> votes = votingService.getVotes(roomCode);
        messagingTemplate.convertAndSend("/topic/vote/update/" + roomCode, votes);

        Room room = roomRepository.findByCode(roomCode);

        if (room != null && votes.size() == room.getPlayers().size()) {
            VoteResultDTO result = new VoteResultDTO();
            
            result.setInitiator(votingService.getInitiator(roomCode));
            result.setTarget(votingService.getTarget(roomCode));
            result.setVotes(votes);

            messagingTemplate.convertAndSend("/topic/vote/result/" + roomCode, result);
            votingService.endVote(roomCode);
        }
    }
}