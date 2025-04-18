package ch.uzh.ifi.hase.soprafs25.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ImageIndexDTOTest {

    @Test
    @DisplayName("constructor sets index value")
    void constructor_shouldSetIndex() {
        ImageIndexDTO dto = new ImageIndexDTO(2);

        assertThat(dto.getIndex()).isEqualTo(2);
    }

    @Test
    @DisplayName("setter overrides index value")
    void setter_shouldUpdateIndex() {
        ImageIndexDTO dto = new ImageIndexDTO();
        dto.setIndex(5);

        assertThat(dto.getIndex()).isEqualTo(5);
    }
}
