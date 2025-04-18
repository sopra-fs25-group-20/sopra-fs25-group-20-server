package ch.uzh.ifi.hase.soprafs25.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PlayerRoleDTOTest {

    @Test
    void constructor_setsRole() {
        PlayerRoleDTO dto = new PlayerRoleDTO("spy");
        assertThat(dto.getPlayerRole()).isEqualTo("spy");
    }

    @Test
    void setter_updatesRole() {
        PlayerRoleDTO dto = new PlayerRoleDTO("innocent");
        dto.setPlayerRole("spy");
        assertThat(dto.getPlayerRole()).isEqualTo("spy");
    }
}
