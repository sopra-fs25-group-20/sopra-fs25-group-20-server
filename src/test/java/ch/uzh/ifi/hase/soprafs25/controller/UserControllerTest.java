package ch.uzh.ifi.hase.soprafs25.controller;

import ch.uzh.ifi.hase.soprafs25.model.UserGetDTO;
import ch.uzh.ifi.hase.soprafs25.model.UserPostDTO;
import ch.uzh.ifi.hase.soprafs25.model.UserPutDTO;
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

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    @Test
    void testGetUser_success() throws Exception {
        UserGetDTO userGetDTO = new UserGetDTO("existing", 10, 5, 15, 2, 4);

        Mockito.when(userService.getUser("existing")).thenReturn(userGetDTO);

        mockMvc.perform(get("/account/existing"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("existing"))
                .andExpect(jsonPath("$.wins").value(10))
                .andExpect(jsonPath("$.defeats").value(5))
                .andExpect(jsonPath("$.games").value(15))
                .andExpect(jsonPath("$.current_streak").value(2))
                .andExpect(jsonPath("$.highest_streak").value(4));
    }

    @Test
    void testUpdateUser_success() throws Exception {
        UserPutDTO requestDTO = new UserPutDTO();
        requestDTO.setUsername("updatedUser");
        requestDTO.setPassword("newPass");
        requestDTO.setToken("theToken");

        doNothing().when(userService)
                .updateUser("existing", "updatedUser", "newPass", "theToken");

        mockMvc.perform(put("/account/existing")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        Mockito.verify(userService)
                .updateUser("existing", "updatedUser", "newPass", "theToken");
    }

}
