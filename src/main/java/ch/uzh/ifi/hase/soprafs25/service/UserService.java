package ch.uzh.ifi.hase.soprafs25.service;

import ch.uzh.ifi.hase.soprafs25.entity.User;
import ch.uzh.ifi.hase.soprafs25.exceptions.UserAlreadyExistsException;
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

    private void checkIfUserExists(String username) {
        User userByUsername = userRepository.findByUsername(username);

        if (userByUsername != null) {
            throw new UserAlreadyExistsException("username: " + username);
        }
    }
}
