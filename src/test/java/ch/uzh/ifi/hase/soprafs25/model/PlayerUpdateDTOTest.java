package ch.uzh.ifi.hase.soprafs25.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PlayerUpdateDTOTest {

    @Test
    void constructor_shouldSetFields() {
        PlayerUpdateDTO dto = new PlayerUpdateDTO("alice", "#abcdef");

        assertThat(dto.getNickname()).isEqualTo("alice");
        assertThat(dto.getColor()).isEqualTo("#abcdef");
    }

    @Test
    void setters_shouldUpdateFields() {
        PlayerUpdateDTO dto = new PlayerUpdateDTO();
        dto.setNickname("bob");
        dto.setColor("#123456");

        assertThat(dto.getNickname()).isEqualTo("bob");
        assertThat(dto.getColor()).isEqualTo("#123456");
    }
}
