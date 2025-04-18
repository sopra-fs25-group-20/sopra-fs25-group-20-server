package ch.uzh.ifi.hase.soprafs25.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GamePhaseDTOTest {

    @Test
    @DisplayName("constructor sets phase correctly & getter returns it")
    void constructor_shouldSetPhase() {
        GamePhaseDTO dto = new GamePhaseDTO("lobby");

        assertThat(dto.getPhase()).isEqualTo("lobby");
    }

    @Test
    @DisplayName("setter overrides phase value")
    void setter_shouldOverridePhase() {
        GamePhaseDTO dto = new GamePhaseDTO("lobby");

        dto.setPhase("game");

        assertThat(dto.getPhase()).isEqualTo("game");
    }
}
