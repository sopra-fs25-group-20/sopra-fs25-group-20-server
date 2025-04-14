package ch.uzh.ifi.hase.soprafs25.util;

import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

import ch.uzh.ifi.hase.soprafs25.constant.PlayerRole;
import org.junit.jupiter.api.Test;

class RoleUtilTest {

    @Test
    void assignRolesCorrectSpyCount() {
        List<String> players = List.of("A", "B", "C", "D");
        int spyCount = 1;

        Map<String, PlayerRole> roles = RoleUtil.assignRoles(players, spyCount);

        assertEquals(4, roles.size());

        long spyAssigned = roles.values().stream().filter(role -> role == PlayerRole.SPY).count();
        long innocentAssigned = roles.values().stream().filter(role -> role == PlayerRole.INNOCENT).count();

        assertEquals(1, spyAssigned);
        assertEquals(3, innocentAssigned);
    }

    @Test
    void assignRolesIsRandomized() {
        List<String> players = List.of("A", "B", "C", "D");
        int spyCount = 1;

        Set<String> firstSpies = new HashSet<>();

        for (int i = 0; i < 20; i++) {
            Map<String, PlayerRole> roles = RoleUtil.assignRoles(players, spyCount);
            roles.forEach((player, role) -> {
                if (role == PlayerRole.SPY) {
                    firstSpies.add(player);
                }
            });
        }
        assertTrue(firstSpies.size() > 1, "Spy role assignment should be randomized.");
    }

    @Test
    void assignRolesThrowsOnInvalidInput() {
        assertThrows(IllegalArgumentException.class, () -> RoleUtil.assignRoles(null, 1));

        List<String> tooFewPlayers = List.of("A");
        assertThrows(IllegalArgumentException.class, () -> RoleUtil.assignRoles(tooFewPlayers, 2));
    }

    @Test
    void assignRolesCoversAllPlayers() {
        List<String> players = List.of("X", "Y", "Z");
        Map<String, PlayerRole> roles = RoleUtil.assignRoles(players, 1);

        for (String player : players) {
            assertTrue(roles.containsKey(player));
            assertNotNull(roles.get(player));
        }
    }
}
