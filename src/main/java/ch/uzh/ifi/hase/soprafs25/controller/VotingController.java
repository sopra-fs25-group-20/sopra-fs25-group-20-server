package ch.uzh.ifi.hase.soprafs25.controller;

import ch.uzh.ifi.hase.soprafs25.entity.Room;
import ch.uzh.ifi.hase.soprafs25.entity.VotingSession;
import ch.uzh.ifi.hase.soprafs25.model.VoteStartDTO;
import ch.uzh.ifi.hase.soprafs25.model.VoteCastDTO;
import ch.uzh.ifi.hase.soprafs25.model.VoteResultDTO;
import ch.uzh.ifi.hase.soprafs25.model.VoteStateDTO;
import ch.uzh.ifi.hase.soprafs25.repository.RoomRepository;
import ch.uzh.ifi.hase.soprafs25.service.VotingService;

import ch.uzh.ifi.hase.soprafs25.util.SessionUtil;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

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

    @MessageMapping("/vote/init")
    public void startVote(@Payload VoteStartDTO startDTO, Message<?> socketMessage) {
        String roomCode = SessionUtil.getCode(socketMessage);
        String nickname = SessionUtil.getNickname(socketMessage);
        
        VotingSession votingSession = votingService.createVotingSession(roomCode, nickname, startDTO.getTarget());

        messagingTemplate.convertAndSend("/topic/vote/begin/" + roomCode, startDTO);
        messagingTemplate.convertAndSend("/topic/vote/update/" + roomCode,
                new VoteStateDTO(votingSession.getVoteState().getVotes()));
    }

    @MessageMapping("/vote/cast")
    public void castVote(@Payload VoteCastDTO castDTO, Message<?> socketMessage) {
        String roomCode = SessionUtil.getCode(socketMessage);
        String nickname = SessionUtil.getNickname(socketMessage);

        boolean voteAccepted = votingService.castVote(roomCode, nickname, castDTO.isVoteYes());

        if (voteAccepted) {
            VotingSession votingSession = votingService.getActiveVotingSession(roomCode);
            messagingTemplate.convertAndSend("/topic/vote/update/" + roomCode,
                    new VoteStateDTO(votingSession.getVoteState().getVotes()));
        }

        Room room = roomRepository.findByCode(roomCode);
        if (room != null && votingService.isVoteComplete(roomCode, room.getPlayers().size())) {
            VotingSession sessionInstance = votingService.getActiveVotingSession(roomCode);
            VoteResultDTO result = new VoteResultDTO();
            
            result.setInitiator(sessionInstance.getInitiator());
            result.setTarget(sessionInstance.getTarget());
            result.setVotes(sessionInstance.getVoteState().getVotes());

            messagingTemplate.convertAndSend("/topic/vote/result/" + roomCode, result);
            votingService.endVotingSession(roomCode);
        }
    }
}
