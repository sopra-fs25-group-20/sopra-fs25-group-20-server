package ch.uzh.ifi.hase.soprafs25.controller;

import ch.uzh.ifi.hase.soprafs25.model.VoteCastDTO;
import ch.uzh.ifi.hase.soprafs25.model.VoteStartDTO;
import ch.uzh.ifi.hase.soprafs25.entity.Room;
import ch.uzh.ifi.hase.soprafs25.entity.VotingSession;
import ch.uzh.ifi.hase.soprafs25.repository.RoomRepository;
import ch.uzh.ifi.hase.soprafs25.service.VotingService;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VotingControllerTest {

    @InjectMocks
    private VotingController votingController;

    @Mock
    private VotingService votingService;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private RoomRepository roomRepository;

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
    void testStartVote_sendsBeginAndUpdate() {
        String roomCode = "ROOM123";
        String nickname = "testUser";
        VoteStartDTO startDTO = new VoteStartDTO();
        startDTO.setTarget("testUser2");

        VotingSession mockSession = new VotingSession(roomCode, nickname, "testUser2");

        when(votingService.createVotingSession(roomCode, "testUser", "testUser2")).thenReturn(mockSession);

        votingController.startVote(startDTO, mockSocketMessage(roomCode, nickname));

        verify(messagingTemplate).convertAndSend(startsWith("/topic/vote/begin/"), eq(startDTO));
        verify(messagingTemplate).convertAndSend(startsWith("/topic/vote/update/"), any(Object.class));
    }

    @Test
    void testCastVote_triggersVoteResultIfComplete() {
        String roomCode = "ROOM123";
        String nickname = "testUser";
        VoteCastDTO castDTO = new VoteCastDTO();
        castDTO.setVoteYes(true);

        VotingSession mockSession = new VotingSession(roomCode, nickname, "testUser3");
        Room mockRoom = new Room();
        mockRoom.setCode(roomCode);
        mockRoom.addPlayer(new ch.uzh.ifi.hase.soprafs25.entity.Player());

        when(votingService.castVote(roomCode, "testUser", true)).thenReturn(true);
        when(votingService.getActiveVotingSession(roomCode)).thenReturn(mockSession);
        when(roomRepository.findByCode(roomCode)).thenReturn(mockRoom);
        when(votingService.isVoteComplete(roomCode, 1)).thenReturn(true);

        votingController.castVote(castDTO, mockSocketMessage(roomCode, nickname));

        verify(messagingTemplate).convertAndSend(startsWith("/topic/vote/update/"), any(Object.class));
        verify(messagingTemplate).convertAndSend(startsWith("/topic/vote/result/"), any(Object.class));
        verify(votingService).endVotingSession(roomCode);
    }
}
