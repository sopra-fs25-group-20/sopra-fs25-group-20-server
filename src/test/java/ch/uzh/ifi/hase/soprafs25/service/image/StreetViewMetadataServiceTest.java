package ch.uzh.ifi.hase.soprafs25.service.image;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class StreetViewMetadataServiceTest {

    @Mock
    private WebClient webClient;

    @SuppressWarnings("rawtypes")
    @Mock
    private WebClient.RequestHeadersUriSpec uriSpec;

    @Mock
    private WebClient.RequestHeadersSpec<?> headersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @Mock
    private JsonNode jsonNode;

    private StreetViewMetadataService service;
    private AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        service = new StreetViewMetadataService(webClient);
        ReflectionTestUtils.setField(service, "apiKey", "dummy-key");
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    void getStatus_whenStatusOk_returnsText() {
        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(any(Function.class))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(JsonNode.class)).thenReturn(Mono.just(jsonNode));
        when(jsonNode.path("status")).thenReturn(jsonNode);
        when(jsonNode.asText()).thenReturn("OK");

        String status = service.getStatus(48.8584, 2.2945);
        assertEquals("OK", status);
        verify(webClient).get();
    }

    @Test
    void getStatus_whenBodyError_throwsException() {
        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(any(Function.class))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(JsonNode.class))
                .thenReturn(Mono.error(new RuntimeException("timeout")));

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> service.getStatus(0.0, 0.0)
        );
        assertTrue(ex.getMessage().contains("timeout"));
    }

    @Test
    void getStatuses_returnsSameCountAndStatuses() {
        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(any(Function.class))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(JsonNode.class)).thenReturn(Mono.just(jsonNode));
        when(jsonNode.path("status")).thenReturn(jsonNode);
        when(jsonNode.asText()).thenReturn("ZERO_RESULTS");

        List<Coordinate> coords = List.of(
                new Coordinate(10, 20),
                new Coordinate(30, 40),
                new Coordinate(50, 60)
        );

        List<String> statuses = service.getStatuses(coords);
        assertEquals(coords.size(), statuses.size());
        statuses.forEach(s -> assertEquals("ZERO_RESULTS", s));

        verify(webClient, times(coords.size())).get();
    }
}
