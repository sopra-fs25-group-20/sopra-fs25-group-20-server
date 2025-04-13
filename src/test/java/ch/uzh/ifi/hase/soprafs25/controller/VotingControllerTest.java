package ch.uzh.ifi.hase.soprafs25.controller;

import ch.uzh.ifi.hase.soprafs25.model.VoteCastDTO;
import ch.uzh.ifi.hase.soprafs25.model.VoteStartDTO;
import ch.uzh.ifi.hase.soprafs25.service.VotingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VotingControllerTest {

    @InjectMocks
    private VotingController votingController;

    @Mock
    private VotingService votingService;

    private Message<?> mockSocketMessage(String roomCode, String nickname) {
        Message<?> message = mock(Message.class);
        Map<String, Object> sessionAttributes = new HashMap<>();
        sessionAttributes.put("code", roomCode);
        sessionAttributes.put("nickname", nickname);
        Map<String, Object> headerMap = new HashMap<>();
        headerMap.put("simpSessionAttributes", sessionAttributes);
        MessageHeaders headers = new MessageHeaders(headerMap);
        when(message.getHeaders()).thenReturn(headers);
        return message;
    }

    @Test
    public void testStartVote_callsService() {
        String roomCode = "ROOM123";
        VoteStartDTO startDTO = new VoteStartDTO();
        startDTO.setTarget("targetUser");

        votingController.startVote(startDTO, mockSocketMessage(roomCode, "initiatorUser"));

        verify(votingService).createVotingSession(roomCode, "initiatorUser", "targetUser");
    }

    @Test
    public void testCastVote_callsService() {
        String roomCode = "ROOM123";
        VoteCastDTO castDTO = new VoteCastDTO();
        castDTO.setVoteYes(true);

        votingController.castVote(castDTO, mockSocketMessage(roomCode, "voterUser"));

        verify(votingService).castVote(roomCode, "voterUser", true);
    }
}
