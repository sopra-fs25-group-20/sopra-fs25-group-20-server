package ch.uzh.ifi.hase.soprafs25.service;

import ch.uzh.ifi.hase.soprafs25.entity.User;
import ch.uzh.ifi.hase.soprafs25.exceptions.*;
import ch.uzh.ifi.hase.soprafs25.model.UserGetDTO;
import ch.uzh.ifi.hase.soprafs25.model.UserRegisterDTO;
import ch.uzh.ifi.hase.soprafs25.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private String rawUsername;
    private String rawPassword;
    private String lowercase;
    private String encoded;
    private String token;
    private User testUser;

    @BeforeEach
    void setup() {
        rawUsername = "TestUser";
        rawPassword = "secret";
        lowercase = rawUsername.toLowerCase();
        encoded = "encodedSecret";
        token = UUID.randomUUID().toString();

        testUser = new User();
        testUser.setUserId(1L);
        testUser.setUsername(lowercase);
        testUser.setPassword(encoded);
        testUser.setToken(token);
        testUser.setWins(10);
        testUser.setDefeats(5);
        testUser.setGames(15);
        testUser.setCurrentStreak(2);
        testUser.setHighestStreak(4);
    }

    @Test @DisplayName("createUser: success")
    void createUserSuccessful() {
        when(userRepository.findByUsername(lowercase)).thenReturn(null);
        when(passwordEncoder.encode(rawPassword)).thenReturn(encoded);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserRegisterDTO result = userService.createUser(rawUsername, rawPassword);
        assertEquals(testUser.getToken(), result.getToken());

        InOrder inOrder = inOrder(userRepository, passwordEncoder, userRepository);
        inOrder.verify(userRepository).findByUsername(lowercase);
        inOrder.verify(passwordEncoder).encode(rawPassword);
        inOrder.verify(userRepository).save(any(User.class));
        verify(userRepository).flush();
    }

    @Test @DisplayName("createUser: throws when username exists")
    void createUserThrowsIfUsernameExists() {
        when(userRepository.findByUsername(lowercase)).thenReturn(new User());
        assertThrows(UserAlreadyExistsException.class,
                () -> userService.createUser(rawUsername, rawPassword));
    }

    @Test @DisplayName("loginUser: success")
    void loginUserSuccessful() {
        String user = "Alice";
        String pass = "pw";
        String userLower = user.toLowerCase();
        String hashed = "hash";
        String tok = "tok123";

        User existing = new User();
        existing.setUsername(userLower);
        existing.setPassword(hashed);
        existing.setToken(tok);

        when(userRepository.findByUsername(userLower)).thenReturn(existing);
        when(passwordEncoder.matches(pass, hashed)).thenReturn(true);

        UserRegisterDTO dto = userService.loginUser(user, pass);
        assertEquals(tok, dto.getToken());
    }

    @Test @DisplayName("loginUser: throws when user not found")
    void loginUserThrowsIfNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(null);
        assertThrows(UserNotFoundException.class,
                () -> userService.loginUser("unknown", "pw"));
    }

    @Test @DisplayName("loginUser: throws on invalid password")
    void loginUserThrowsOnInvalidPassword() {
        User existing = new User();
        existing.setUsername("bob");
        existing.setPassword("hashed");

        when(userRepository.findByUsername("bob")).thenReturn(existing);
        when(passwordEncoder.matches("wrong", "hashed")).thenReturn(false);

        assertThrows(InvalidPasswordException.class,
                () -> userService.loginUser("bob", "wrong"));
    }

    @Test @DisplayName("getUser: returns stats")
    void getUserReturnsStats() {
        when(userRepository.findByUsername(lowercase)).thenReturn(testUser);

        UserGetDTO dto = userService.getUser(rawUsername);

        assertEquals(lowercase, dto.getUsername());
        assertEquals(10, dto.getWins());
        assertEquals(5, dto.getDefeats());
        assertEquals(15, dto.getGames());
        assertEquals(2, dto.getCurrentStreak());
        assertEquals(4, dto.getHighestStreak());
    }

    // --- new tests for updateUser ---

    @Test @DisplayName("updateUser: throws when token missing")
    void updateUserThrowsWhenTokenMissing() {
        assertThrows(TokenNotFoundException.class,
                () -> userService.updateUser(lowercase, "new", "pw", null));
    }

    @Test @DisplayName("updateUser: throws when token invalid")
    void updateUserThrowsWhenTokenInvalid() {
        when(userRepository.findByToken("bad")).thenReturn(null);
        assertThrows(UserNotAuthenticatedException.class,
                () -> userService.updateUser(lowercase, "new", "pw", "bad"));
    }

    @Test @DisplayName("updateUser: throws when unauthorized target")
    void updateUserThrowsWhenNotOwner() {
        User other = new User();
        other.setUserId(2L);
        other.setUsername("other");
        when(userRepository.findByToken(token)).thenReturn(testUser);
        when(userRepository.findByUsername(lowercase)).thenReturn(other);

        assertThrows(UserNotAuthorizedException.class,
                () -> userService.updateUser(lowercase, "new", "pw", token));
    }

    @Test @DisplayName("updateUser: skip when new username blank")
    void updateUserSkipsWhenUsernameBlank() {
        testUser.setUserId(1L);
        when(userRepository.findByToken(token)).thenReturn(testUser);
        when(userRepository.findByUsername(lowercase)).thenReturn(testUser);

        userService.updateUser(lowercase, "   ", null, token);

        assertEquals(lowercase, testUser.getUsername());
        assertEquals(encoded, testUser.getPassword());
    }

    @Test @DisplayName("updateUser: change username when available")
    void updateUserChangesUsername() {
        String newName = "NewUser";
        when(userRepository.findByToken(token)).thenReturn(testUser);
        when(userRepository.findByUsername(lowercase)).thenReturn(testUser);
        when(userRepository.findByUsername(newName.toLowerCase())).thenReturn(null);

        userService.updateUser(lowercase, newName, null, token);

        assertEquals(newName.toLowerCase(), testUser.getUsername());
    }

    @Test @DisplayName("updateUser: throws when new username taken")
    void updateUserThrowsOnUsernameTaken() {
        String newName = "Exists";
        when(userRepository.findByToken(token)).thenReturn(testUser);
        when(userRepository.findByUsername(lowercase)).thenReturn(testUser);
        when(userRepository.findByUsername(newName.toLowerCase())).thenReturn(new User());

        assertThrows(UserAlreadyExistsException.class,
                () -> userService.updateUser(lowercase, newName, null, token));
    }

    @Test @DisplayName("updateUser: change password when provided")
    void updateUserChangesPassword() {
        String newPass = "newPw";
        String encodedNew = "encNew";
        when(userRepository.findByToken(token)).thenReturn(testUser);
        when(userRepository.findByUsername(lowercase)).thenReturn(testUser);
        when(passwordEncoder.encode(newPass)).thenReturn(encodedNew);

        userService.updateUser(lowercase, null, newPass, token);

        assertEquals(encodedNew, testUser.getPassword());
    }

    @Test @DisplayName("updateUser: no password change when new blank")
    void updateUserSkipsPasswordWhenBlank() {
        when(userRepository.findByToken(token)).thenReturn(testUser);
        when(userRepository.findByUsername(lowercase)).thenReturn(testUser);

        userService.updateUser(lowercase, null, "   ", token);

        assertEquals(encoded, testUser.getPassword());
    }

    @Test @DisplayName("updateUserStatsAfterGame: win")
    void updateUserStatsAfterGame_win() {
        userService.updateUserStatsAfterGame(testUser, true);

        assertEquals(11, testUser.getWins());
        assertEquals(16, testUser.getGames());
        assertEquals(3, testUser.getCurrentStreak());
        assertEquals(4, testUser.getHighestStreak());

        verify(userRepository).save(testUser);
        verify(userRepository).flush();
    }

    @Test @DisplayName("updateUserStatsAfterGame: defeat")
    void updateUserStatsAfterGame_defeat() {
        userService.updateUserStatsAfterGame(testUser, false);

        assertEquals(6, testUser.getDefeats());
        assertEquals(16, testUser.getGames());
        assertEquals(0, testUser.getCurrentStreak());

        verify(userRepository).save(testUser);
        verify(userRepository).flush();
    }

    @Test @DisplayName("validateToken: blank string")
    void validateTokenBlankString() {
        assertThrows(TokenNotFoundException.class, () -> userService.validateToken("   "));
    }
}
