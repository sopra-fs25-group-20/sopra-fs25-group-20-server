package ch.uzh.ifi.hase.soprafs25.service;

import ch.uzh.ifi.hase.soprafs25.entity.User;
import ch.uzh.ifi.hase.soprafs25.exceptions.UserAlreadyExistsException;
import ch.uzh.ifi.hase.soprafs25.model.UserRegisterDTO;
import ch.uzh.ifi.hase.soprafs25.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
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
