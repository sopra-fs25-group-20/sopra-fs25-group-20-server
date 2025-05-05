package ch.uzh.ifi.hase.soprafs25.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserPutDTOTest {

    @Test
    @DisplayName("should have null fields by default")
    void shouldHaveNullFieldsByDefault() {
        UserPutDTO dto = new UserPutDTO();
        assertNull(dto.getUsername());
        assertNull(dto.getPassword());
        assertNull(dto.getToken());
    }

    @Test
    @DisplayName("should set and get username correctly")
    void shouldSetAndGetUsername() {
        UserPutDTO dto = new UserPutDTO();
        String username = "alice";
        dto.setUsername(username);
        assertEquals(username, dto.getUsername());
    }

    @Test
    @DisplayName("should set and get password correctly")
    void shouldSetAndGetPassword() {
        UserPutDTO dto = new UserPutDTO();
        String password = "P@ssw0rd!";
        dto.setPassword(password);
        assertEquals(password, dto.getPassword());
    }

    @Test
    @DisplayName("should set and get token correctly")
    void shouldSetAndGetToken() {
        UserPutDTO dto = new UserPutDTO();
        String token = "token-123";
        dto.setToken(token);
        assertEquals(token, dto.getToken());
    }
}
