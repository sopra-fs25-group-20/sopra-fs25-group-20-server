package ch.uzh.ifi.hase.soprafs25.controller;

import ch.uzh.ifi.hase.soprafs25.model.UserGetDTO;
import ch.uzh.ifi.hase.soprafs25.model.UserPostDTO;
import ch.uzh.ifi.hase.soprafs25.model.UserPutDTO;
import ch.uzh.ifi.hase.soprafs25.model.UserRegisterDTO;
import ch.uzh.ifi.hase.soprafs25.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserRegisterDTO registerUser(@RequestBody UserPostDTO userPostDTO) {
        String username = userPostDTO.getUsername();
        String password = userPostDTO.getPassword();
        return userService.createUser(username, password);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public UserRegisterDTO loginUser(@RequestBody UserPostDTO userPostDTO) {
        String username = userPostDTO.getUsername();
        String password = userPostDTO.getPassword();
        return userService.loginUser(username, password);
    }

    @PutMapping("/account/{username}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateUser(@PathVariable("username") String targetUsername,
                           @RequestBody UserPutDTO userPutDTO) {
        String username = userPutDTO.getUsername();
        String password = userPutDTO.getPassword();
        String token = userPutDTO.getToken();
        userService.updateUser(targetUsername, username, password, token);
    }

    @GetMapping("/account/{username}")
    @ResponseStatus(HttpStatus.OK)
    public UserGetDTO getUser(@PathVariable String username) {
        return userService.getUser(username);
    }

    @GetMapping("/account/validate")
    @ResponseStatus(HttpStatus.OK)
    public void validateToken(@RequestHeader(value = "Authorization", required = false) String tokenHeader) {
        userService.validateToken(tokenHeader);
    }
}
