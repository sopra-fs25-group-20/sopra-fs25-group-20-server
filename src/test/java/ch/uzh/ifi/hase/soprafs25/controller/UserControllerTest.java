package ch.uzh.ifi.hase.soprafs25.controller;

import ch.uzh.ifi.hase.soprafs25.model.UserPostDTO;
import ch.uzh.ifi.hase.soprafs25.model.UserRegisterDTO;
import ch.uzh.ifi.hase.soprafs25.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testRegisterUser_success() throws Exception {
        UserPostDTO requestDTO = new UserPostDTO();
        requestDTO.setUsername("newuser");
        requestDTO.setPassword("password");

        UserRegisterDTO responseDTO = new UserRegisterDTO("generatedToken");

        Mockito.when(userService.createUser("newuser", "password")).thenReturn(responseDTO);

        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("generatedToken"));
    }

    @Test
    void testLoginUser_success() throws Exception {
        UserPostDTO requestDTO = new UserPostDTO();
        requestDTO.setUsername("existing");
        requestDTO.setPassword("pass");

        UserRegisterDTO responseDTO = new UserRegisterDTO("loginToken");

        Mockito.when(userService.loginUser("existing", "pass")).thenReturn(responseDTO);

        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("loginToken"));
    }
}
