package ch.uzh.ifi.hase.soprafs25.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ImageIndexDTOTest {

    @Test
    void constructor_shouldSetIndex() {
        ImageIndexDTO dto = new ImageIndexDTO(2);

        assertThat(dto.getIndex()).isEqualTo(2);
    }

    @Test
    void setter_shouldUpdateIndex() {
        ImageIndexDTO dto = new ImageIndexDTO();
        dto.setIndex(5);

        assertThat(dto.getIndex()).isEqualTo(5);
    }
}
