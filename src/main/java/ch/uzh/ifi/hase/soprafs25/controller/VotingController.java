package ch.uzh.ifi.hase.soprafs25.controller;

import ch.uzh.ifi.hase.soprafs25.model.VoteStartDTO;
import ch.uzh.ifi.hase.soprafs25.model.VoteCastDTO;
import ch.uzh.ifi.hase.soprafs25.service.VotingService;

import ch.uzh.ifi.hase.soprafs25.util.SessionUtil;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
public class VotingController {

    private final VotingService votingService;

    public VotingController(VotingService votingService) {
        this.votingService = votingService;
    }

    @MessageMapping("/vote/init")
    public void startVote(@Payload VoteStartDTO startDTO, Message<?> socketMessage) {
        String roomCode = SessionUtil.getCode(socketMessage);
        String nickname = SessionUtil.getNickname(socketMessage);

        votingService.initializeVotingSession(roomCode, nickname, startDTO.getTarget());
    }

    @MessageMapping("/vote/cast")
    public void castVote(@Payload VoteCastDTO castDTO, Message<?> socketMessage) {
        String roomCode = SessionUtil.getCode(socketMessage);
        String nickname = SessionUtil.getNickname(socketMessage);

        votingService.castVote(roomCode, nickname, castDTO.isVoteYes());
    }
}
