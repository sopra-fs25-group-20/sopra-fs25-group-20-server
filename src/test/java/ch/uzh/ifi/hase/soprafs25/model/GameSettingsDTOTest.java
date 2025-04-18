package ch.uzh.ifi.hase.soprafs25.model;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class GameSettingsDTOTest {

    @Test
    void constructor_setsAllFields_multipleCases() {
        int[][] data = { {15,60,4}, {30,45,6}, {20,90,8} };
        String[] regions = {"europe", "asia", "world"};

        for (int i = 0; i < data.length; i++) {
            GameSettingsDTO dto = new GameSettingsDTO(
                    data[i][0], data[i][1], data[i][2], regions[i]);

            assertThat(dto.getVotingTimer()).isEqualTo(data[i][0]);
            assertThat(dto.getGameTimer()).isEqualTo(data[i][1]);
            assertThat(dto.getImageCount()).isEqualTo(data[i][2]);
            assertThat(dto.getImageRegion()).isEqualTo(regions[i]);
        }
    }
}
