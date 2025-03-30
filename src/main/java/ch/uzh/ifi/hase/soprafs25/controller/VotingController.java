package ch.uzh.ifi.hase.soprafs25.controller;

import ch.uzh.ifi.hase.soprafs25.entity.Room;
import ch.uzh.ifi.hase.soprafs25.model.VoteStartDTO;
import ch.uzh.ifi.hase.soprafs25.model.VoteCastDTO;
import ch.uzh.ifi.hase.soprafs25.model.VoteResultDTO;
import ch.uzh.ifi.hase.soprafs25.repository.RoomRepository;
import ch.uzh.ifi.hase.soprafs25.service.VotingService;
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
    public void startVote(@Payload VoteStartDTO startDTO) {
        votingService.startVote(startDTO.getRoomCode(), startDTO.getInitiator(), startDTO.getTarget());
        messagingTemplate.convertAndSend("/topic/vote/update/" + startDTO.getRoomCode(), startDTO);
    }

    @MessageMapping("/vote/cast")
    public void castVote(@Payload VoteCastDTO castDTO) {
        votingService.castVote(castDTO.getRoomCode(), castDTO.getVoter(), castDTO.isVoteYes());
        Map<String, Boolean> votes = votingService.getVotes(castDTO.getRoomCode());
        messagingTemplate.convertAndSend("/topic/vote/update/" + castDTO.getRoomCode(), votes);

        Room room = roomRepository.findByCode(castDTO.getRoomCode());

        if (room != null && votes.size() == room.getPlayers().size()) {
            VoteResultDTO result = new VoteResultDTO();
            
            result.setInitiator(votingService.getInitiator(castDTO.getRoomCode()));
            result.setTarget(votingService.getTarget(castDTO.getRoomCode()));
            result.setVotes(votes);

            messagingTemplate.convertAndSend("/topic/vote/result/" + castDTO.getRoomCode(), result);
            votingService.clearVote(castDTO.getRoomCode());
        }
    }
}
