package ch.uzh.ifi.hase.soprafs25.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VoteStateDTOTest {

    @Test
    @DisplayName("constructor sets vote counts correctly")
    void constructor_shouldSetCounts() {
        VoteStateDTO dto = new VoteStateDTO(3, 1);

        assertThat(dto.getNumberVotesTrue()).isEqualTo(3);
        assertThat(dto.getNumberVotesFalse()).isEqualTo(1);
    }

    @Test
    @DisplayName("setter overrides counts and getter reflects change")
    void setters_shouldOverrideCounts() {
        VoteStateDTO dto = new VoteStateDTO(0, 0);
        dto.setNumberVotesTrue(5);
        dto.setNumberVotesFalse(2);

        assertThat(dto.getNumberVotesTrue()).isEqualTo(5);
        assertThat(dto.getNumberVotesFalse()).isEqualTo(2);
    }
}
