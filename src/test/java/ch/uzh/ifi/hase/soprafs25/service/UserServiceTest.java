package ch.uzh.ifi.hase.soprafs25.service;

import ch.uzh.ifi.hase.soprafs25.entity.User;
import ch.uzh.ifi.hase.soprafs25.exceptions.InvalidPasswordException;
import ch.uzh.ifi.hase.soprafs25.exceptions.UserAlreadyExistsException;
import ch.uzh.ifi.hase.soprafs25.exceptions.UserNotFoundException;
import ch.uzh.ifi.hase.soprafs25.model.UserGetDTO;
import ch.uzh.ifi.hase.soprafs25.model.UserRegisterDTO;
import ch.uzh.ifi.hase.soprafs25.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
        testUser.setUsername(lowercase);
        testUser.setPassword(encoded);
        testUser.setToken(token);
        testUser.setWins(10);
        testUser.setDefeats(5);
        testUser.setGames(15);
        testUser.setCurrentStreak(2);
        testUser.setHighestStreak(4);
    }

    @Test
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

    @Test
    void createUserThrowsIfUsernameExists() {
        when(userRepository.findByUsername("john")).thenReturn(new User());
        assertThrows(UserAlreadyExistsException.class,
                () -> userService.createUser("John", "pwd"));
    }

    @Test
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

    @Test
    void loginUserThrowsIfNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(null);
        assertThrows(UserNotFoundException.class,
                () -> userService.loginUser("unknown", "pw"));
    }

    @Test
    void loginUserThrowsOnInvalidPassword() {
        User existing = new User();
        existing.setUsername("bob");
        existing.setPassword("hashed");

        when(userRepository.findByUsername("bob")).thenReturn(existing);
        when(passwordEncoder.matches("wrong", "hashed")).thenReturn(false);

        assertThrows(InvalidPasswordException.class,
                () -> userService.loginUser("bob", "wrong"));
    }

    @Test
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
}
