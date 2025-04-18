package ch.uzh.ifi.hase.soprafs25.model;

import ch.uzh.ifi.hase.soprafs25.constant.GamePhase;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GamePhaseDTOTest {

    @Test
    void constructor_setsPhaseCorrectly() {
        String phase = GamePhase.LOBBY.name().toLowerCase();

        GamePhaseDTO dto = new GamePhaseDTO(phase);

        assertThat(dto.getPhase()).isEqualTo(phase);
    }

    @Test
    void setter_updatesPhase() {
        GamePhaseDTO dto = new GamePhaseDTO(GamePhase.LOBBY.name().toLowerCase());

        dto.setPhase(GamePhase.GAME.name().toLowerCase());

        assertThat(dto.getPhase())
                .isEqualTo(GamePhase.GAME.name().toLowerCase());
    }
}
