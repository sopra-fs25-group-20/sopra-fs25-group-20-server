package ch.uzh.ifi.hase.soprafs25.service;

import ch.uzh.ifi.hase.soprafs25.entity.User;
import ch.uzh.ifi.hase.soprafs25.exceptions.InvalidPasswordException;
import ch.uzh.ifi.hase.soprafs25.exceptions.UserAlreadyExistsException;
import ch.uzh.ifi.hase.soprafs25.exceptions.UserNotFoundException;
import ch.uzh.ifi.hase.soprafs25.model.UserGetDTO;
import ch.uzh.ifi.hase.soprafs25.model.UserRegisterDTO;
import ch.uzh.ifi.hase.soprafs25.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.UUID;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
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
}
