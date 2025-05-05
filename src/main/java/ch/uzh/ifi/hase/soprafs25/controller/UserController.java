package ch.uzh.ifi.hase.soprafs25.controller;
 
import ch.uzh.ifi.hase.soprafs25.model.UserPostDTO;
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
    @ResponseBody
    public UserRegisterDTO registerUser(@RequestBody UserPostDTO userPostDTO) {
        String username = userPostDTO.getUsername();
        String password = userPostDTO.getPassword();
        return userService.createUser(username, password);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserRegisterDTO loginUser(@RequestBody UserPostDTO userPostDTO) {
        String username = userPostDTO.getUsername();
        String password = userPostDTO.getPassword();
        return userService.loginUser(username, password);
    }
}