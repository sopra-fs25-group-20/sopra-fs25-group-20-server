package ch.uzh.ifi.hase.soprafs25.service;

import ch.uzh.ifi.hase.soprafs25.entity.User;
import ch.uzh.ifi.hase.soprafs25.exceptions.*;
import ch.uzh.ifi.hase.soprafs25.model.UserGetDTO;
import ch.uzh.ifi.hase.soprafs25.model.UserRegisterDTO;
import ch.uzh.ifi.hase.soprafs25.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

@Service
@Transactional
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public UserService(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    public UserRegisterDTO createUser(String username, String password) {
        username = username.toLowerCase(Locale.ROOT);
        checkIfUserExists(username);

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setToken(UUID.randomUUID().toString());

        User savedUser = userRepository.save(newUser);
        userRepository.flush();

        return new UserRegisterDTO(savedUser.getToken());
    }

    public UserRegisterDTO loginUser(String username, String password) {
        User user = getUserByUsername(username);
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidPasswordException(username);
        }
        return new UserRegisterDTO(user.getToken());
    }

    public void updateUser(String targetUsername, String username, String password, String token) {
        User authenticatedUser = authenticateAndAuthorize(token, targetUsername);

        updateUsernameIfChanged(authenticatedUser, username);
        updatePasswordIfProvided(authenticatedUser, password);
    }

    public void updateUserStatsAfterGame(User user, boolean hasWon) {
        if (hasWon) {
            user.recordWin();
        } else {
            user.recordDefeat();
        }

        userRepository.save(user);
        userRepository.flush();
    }

    public UserGetDTO getUser(String username) {
        User user = getUserByUsername(username);

        return new UserGetDTO(
                user.getUsername(),
                user.getWins(),
                user.getDefeats(),
                user.getGames(),
                user.getCurrentStreak(),
                user.getHighestStreak()
        );
    }

    private User getUserByUsername(String username) {
        username = username.toLowerCase(Locale.ROOT);
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UserNotFoundException("username: " + username);
        }
        return user;
    }

    private void checkIfUserExists(String username) {
        User userByUsername = userRepository.findByUsername(username);

        if (userByUsername != null) {
            throw new UserAlreadyExistsException("username: " + username);
        }
    }

    private void checkUsernameAvailability(String username) {
        if (userRepository.findByUsername(username) != null) {
            throw new UserAlreadyExistsException("username: " + username);
        }
    }

    private User authenticateAndAuthorize(String token, String targetUsername) {
        if (token == null || token.trim().isEmpty()) {
            throw new TokenNotFoundException();
        }

        User authenticatedUser = userRepository.findByToken(token);
        if (authenticatedUser == null) {
            throw new UserNotAuthenticatedException("Invalid token. Please log in again.");
        }

        // When trying to update another user
        User targetUser = getUserByUsername(targetUsername);
        if (!Objects.equals(authenticatedUser.getUserId(), targetUser.getUserId())) {
            throw new UserNotAuthorizedException(authenticatedUser.getUsername(), targetUser.getUsername());
        }

        return authenticatedUser;
    }

    private void updateUsernameIfChanged(User user, String newUsername) {
        if (newUsername == null || newUsername.trim().isEmpty()) {
            return;
        }

        String normalizedNewUsername = newUsername.toLowerCase(Locale.ROOT);

        if (!user.getUsername().equals(normalizedNewUsername)) {
            checkUsernameAvailability(normalizedNewUsername);
            user.setUsername(normalizedNewUsername);
        }
    }

    private void updatePasswordIfProvided(User user, String newPassword) {
        if (newPassword != null && !newPassword.trim().isEmpty()) {
            user.setPassword(passwordEncoder.encode(newPassword));
        }
    }
}