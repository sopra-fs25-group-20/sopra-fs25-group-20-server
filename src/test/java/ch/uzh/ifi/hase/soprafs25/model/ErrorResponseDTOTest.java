package ch.uzh.ifi.hase.soprafs25.model;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class ErrorResponseDTOTest {

    @Test
    void constructor_setsMessage() {
        ErrorResponseDTO dto = new ErrorResponseDTO("Room not found");
        assertThat(dto.getMessage()).isEqualTo("Room not found");

        dto.setMessage("Other");
        assertThat(dto.getMessage()).isEqualTo("Other");
    }
}
