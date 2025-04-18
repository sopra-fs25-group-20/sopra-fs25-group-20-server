package ch.uzh.ifi.hase.soprafs25.service.image;

import ch.uzh.ifi.hase.soprafs25.exceptions.ImageLoadingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StreetViewMetadataServiceTest {

    private StreetViewMetadataService service;
    private RestTemplate restTemplate;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        service = new StreetViewMetadataService();

        restTemplate = mock(RestTemplate.class);
        objectMapper = mock(ObjectMapper.class);

        ReflectionTestUtils.setField(service, "restTemplate", restTemplate);
        ReflectionTestUtils.setField(service, "objectMapper", objectMapper);
        ReflectionTestUtils.setField(service, "apiKey", "dummy-key");
    }

    @Test
    void getStatus_returnsOkFromJson() throws Exception {
        String fakeResponse = "{\"status\":\"OK\"}";
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(fakeResponse);

        JsonNode mockJson = mock(JsonNode.class);
        when(objectMapper.readTree(fakeResponse)).thenReturn(mockJson);
        when(mockJson.path("status")).thenReturn(mock(JsonNode.class));
        when(mockJson.path("status").asText()).thenReturn("OK");

        String result = service.getStatus(48.8584, 2.2945);
        assertThat(result).isEqualTo("OK");
    }

    @Test
    void getStatus_throwsExceptionOnFailure() {
        when(restTemplate.getForObject(anyString(), eq(String.class)))
                .thenThrow(new RuntimeException("API call failed"));

        assertThatThrownBy(() -> service.getStatus(0.0, 0.0))
                .isInstanceOf(ImageLoadingException.class)
                .hasMessageContaining("Failed to load image");
    }
}
