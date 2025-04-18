package ch.uzh.ifi.hase.soprafs25.model;

import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;

class VoteResultDTOTest {

    @Test
    void settersAndGetters_work() {
        VoteResultDTO dto = new VoteResultDTO();

        Map<String, Boolean> votes = Map.of("alice", true, "bob", false);
        dto.setVotes(votes);
        dto.setInitiator("alice");
        dto.setTarget("bob");

        assertThat(dto.getVotes()).isEqualTo(votes);
        assertThat(dto.getInitiator()).isEqualTo("alice");
        assertThat(dto.getTarget()).isEqualTo("bob");
    }
}
