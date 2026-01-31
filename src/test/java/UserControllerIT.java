import org.springframework.test.web.servlet.MockMvc;
import com.example.assessmentapplication.Repository.UserRepository;
import com.example.assessmentapplication.entity.User;

import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

@SpringBootTest //Starts the entire SpringBoot App for testing
@AutoConfigureMockMvc
class UserControllerIT
{
    @Autowired // Injects a fake web server to send HTTP requests to controllers without starting a real server
    MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Test
    void register_createsUser() throws Exception
    {
        String userJson = """
            {"username":"vignesh","password":"pass123"}
            """;
        mockMvc.perform(
                post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson)
        ).andExpect(status().isOk());
        User saved = userRepository.findByUsername("vignesh");
        assertNotNull(saved);
        assertEquals("vignesh",saved.getUsername());
        
    }
    @Test
    void failureTestCase() throws Exception
    {
        String badJson = """
                {"username":"",
                  "password":"pass123"}
                """;
        User wrongUser = userRepository.findByUsername("");
        mockMvc.perform(post("/register").contentType(MediaType.APPLICATION_JSON).content(badJson)).andExpect(status().isBadRequest());
        assertNull(wrongUser);
    }
}