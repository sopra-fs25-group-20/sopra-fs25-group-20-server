package ch.uzh.ifi.hase.soprafs25.controller;

import ch.uzh.ifi.hase.soprafs25.model.VoteCastDTO;
import ch.uzh.ifi.hase.soprafs25.model.VoteStartDTO;
import ch.uzh.ifi.hase.soprafs25.service.VotingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VotingControllerTest {

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
    void testStartVote_delegatesToVotingService() {
        String roomCode = "ROOM123";
        String initiator = "Alice";
        String target = "Bob";

        VoteStartDTO startDTO = new VoteStartDTO();
        startDTO.setTarget(target);

        votingController.startVote(startDTO, mockSocketMessage(roomCode, initiator));

        verify(votingService).initializeVotingSession(roomCode, initiator, target);
    }

    @Test
    void testCastVote_delegatesToVotingService() {
        String roomCode = "ROOM123";
        String voter = "Charlie";

        VoteCastDTO castDTO = new VoteCastDTO();
        castDTO.setVoteYes(true);

        votingController.castVote(castDTO, mockSocketMessage(roomCode, voter));

        verify(votingService).castVote(roomCode, voter, true);
    }
}
