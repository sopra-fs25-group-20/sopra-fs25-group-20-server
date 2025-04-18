package ch.uzh.ifi.hase.soprafs25.model;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class CreateRoomDTOTest {

    @Test
    void constructor_setsFields() {
        CreateRoomDTO dto = new CreateRoomDTO("bob", "ABCD");
        assertThat(dto.getNickname()).isEqualTo("bob");
        assertThat(dto.getRoomCode()).isEqualTo("ABCD");

        dto.setNickname("alice");
        dto.setRoomCode("EFGH");
        assertThat(dto.getNickname()).isEqualTo("alice");
        assertThat(dto.getRoomCode()).isEqualTo("EFGH");
    }
}
