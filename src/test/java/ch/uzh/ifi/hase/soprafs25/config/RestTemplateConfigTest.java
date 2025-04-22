package ch.uzh.ifi.hase.soprafs25.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

public class RestTemplateConfigTest {
    
    @Test
    void testRestTemplateBean() {
        RestTemplateConfig config = new RestTemplateConfig();
        RestTemplate restTemplate = config.restTemplate();
        assertNotNull(restTemplate, "RestTemplate bean should not be null");
    }
}
