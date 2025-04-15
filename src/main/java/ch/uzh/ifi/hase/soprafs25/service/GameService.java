package ch.uzh.ifi.hase.soprafs25.service;

import ch.uzh.ifi.hase.soprafs25.constant.GamePhase;
import ch.uzh.ifi.hase.soprafs25.entity.Game;
import ch.uzh.ifi.hase.soprafs25.model.GamePhaseDTO;
import ch.uzh.ifi.hase.soprafs25.session.GameSessionManager;
import org.springframework.stereotype.Service;

@Service
public class GameService {

    public GamePhaseDTO getGamePhase(String roomCode) {
        GamePhase gamePhase = getGame(roomCode).getPhase();
        return new GamePhaseDTO(gamePhase.name().toLowerCase());
    }

    private Game getGame(String roomCode) {
        return GameSessionManager.getGameSession(roomCode);
    }
}
