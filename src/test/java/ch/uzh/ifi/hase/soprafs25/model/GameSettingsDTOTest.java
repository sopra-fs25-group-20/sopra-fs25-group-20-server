package ch.uzh.ifi.hase.soprafs25.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GameSettingsDTOTest {

    @Test
    @DisplayName("constructor sets all fields for various inputs (no parametrized API)")
    void constructor_setsAllFields_multipleScenarios() {
        // voting, game, count, region
        int[][] intData = {
                {15, 60, 4},
                {30, 45, 6},
                {20, 90, 8}
        };
        String[] regions = {"europe", "asia", "world"};

        for (int i = 0; i < intData.length; i++) {
            int voting = intData[i][0];
            int game   = intData[i][1];
            int count  = intData[i][2];
            String region = regions[i];

            GameSettingsDTO dto = new GameSettingsDTO(voting, game, count, region);

            assertThat(dto.getVotingTimer()).isEqualTo(voting);
            assertThat(dto.getGameTimer()).isEqualTo(game);
            assertThat(dto.getImageCount()).isEqualTo(count);
            assertThat(dto.getImageRegion()).isEqualTo(region);
        }
    }
}
