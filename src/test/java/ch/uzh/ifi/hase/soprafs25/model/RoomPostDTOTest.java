package ch.uzh.ifi.hase.soprafs25.model;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class RoomPostDTOTest {

    @Test
    void emptyCtor_andSetters() {
        RoomPostDTO dto = new RoomPostDTO();
        dto.setNickname("green");
        dto.setCode("XYZ1");
        assertThat(dto.getNickname()).isEqualTo("green");
        assertThat(dto.getCode()).isEqualTo("XYZ1");
    }

    @Test
    void fullCtor_getters() {
        RoomPostDTO dto = new RoomPostDTO("red", "AAAA");
        assertThat(dto.getNickname()).isEqualTo("red");
        assertThat(dto.getCode()).isEqualTo("AAAA");
    }
}
