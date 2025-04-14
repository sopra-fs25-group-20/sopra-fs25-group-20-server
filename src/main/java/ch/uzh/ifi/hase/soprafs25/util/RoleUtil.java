package ch.uzh.ifi.hase.soprafs25.util;

import ch.uzh.ifi.hase.soprafs25.constant.PlayerRole;

import java.util.*;

public class RoleUtil {

    private RoleUtil() {}

    public static Map<String, PlayerRole> assignRoles(List<String> nicknames, int spyCount) {
        if (nicknames == null || nicknames.size() < spyCount) {
            throw new IllegalArgumentException("Invalid number of spy's or empty nickname list");
        }
        List<String> shuffled = new ArrayList<>(nicknames);
        Collections.shuffle(shuffled);

        Map<String, PlayerRole> roles = new HashMap<>();
        for (int i = 0; i < shuffled.size(); i++) {
            String nickname = shuffled.get(i);
            roles.put(nickname, i < spyCount ? PlayerRole.SPY : PlayerRole.INNOCENT);
        }
        return roles;
    }
}
