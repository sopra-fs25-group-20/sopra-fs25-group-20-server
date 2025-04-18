package ch.uzh.ifi.hase.soprafs25.model;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class ChatMessageDTOTest {

    @Test
    void fullConstructor_setsAllFields() {
        ChatMessageDTO dto = new ChatMessageDTO("alice", "hi", "#ff00ff");

        assertThat(dto.getNickname()).isEqualTo("alice");
        assertThat(dto.getMessage()).isEqualTo("hi");
        assertThat(dto.getColor()).isEqualTo("#ff00ff");
    }

    @Test
    void emptyConstructor_andSetters_updateFields() {
        ChatMessageDTO dto = new ChatMessageDTO();

        dto.setNickname("bob");
        dto.setMessage("hello");
        dto.setColor("#123456");

        assertThat(dto.getNickname()).isEqualTo("bob");
        assertThat(dto.getMessage()).isEqualTo("hello");
        assertThat(dto.getColor()).isEqualTo("#123456");
    }
}
