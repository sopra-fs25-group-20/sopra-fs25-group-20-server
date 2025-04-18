package ch.uzh.ifi.hase.soprafs25.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VoteStartDTOTest {

    @Test
    void constructor_setsTarget() {
        VoteStartDTO dto = new VoteStartDTO("bob");
        assertThat(dto.getTarget()).isEqualTo("bob");
    }

    @Test
    void setter_updatesTarget() {
        VoteStartDTO dto = new VoteStartDTO();
        dto.setTarget("alice");
        assertThat(dto.getTarget()).isEqualTo("alice");
    }
}
