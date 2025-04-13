package ch.uzh.ifi.hase.soprafs25.util;

import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
import org.junit.jupiter.api.Test;

public class RoleUtilTest {
    
    @Test
    public void assignRolesCorrectSpyCount() {
        List<String> players = List.of("A", "B", "C", "D");
        int spyCount = 1;

        Map<String, String> roles = RoleUtil.assignRoles(players, spyCount);

        assertEquals(4, roles.size());

        long spyAssigned = roles.values().stream().filter(role -> role.equals("spy")).count();
        long innocentAssigned = roles.values().stream().filter(role -> role.equals("innocent")).count();

        assertEquals(1, spyAssigned);
        assertEquals(3, innocentAssigned);
    }

    @Test
    public void assignRolesIsRandomized() {
        List<String> players = List.of("A", "B", "C", "D");
        int spyCount = 1;

        Set<String> firstSpies = new HashSet<>();

        for (int i = 0; i < 20; i++) {
            Map<String, String> roles = RoleUtil.assignRoles(players, spyCount);
            roles.forEach((player, role) -> {
                if (role.equals("spy")) {
                    firstSpies.add(player);
                }
            });
        }
        assertTrue(firstSpies.size() > 1, "Spy role assignment should be randomized.");
    }

    @Test
    public void assignRolesThrowsOnInvalidInput() {
        assertThrows(IllegalArgumentException.class, () -> RoleUtil.assignRoles(null, 1));

        List<String> tooFewPlayers = List.of("A");
        assertThrows(IllegalArgumentException.class, () -> RoleUtil.assignRoles(tooFewPlayers, 2));
    }

    @Test
    public void assignRolesCoversAllPlayers() {
        List<String> players = List.of("X", "Y", "Z");
        Map<String, String> roles = RoleUtil.assignRoles(players, 1);

        for (String player : players) {
            assertTrue(roles.containsKey(player));
            assertNotNull(roles.get(player));
        }
    }
}
