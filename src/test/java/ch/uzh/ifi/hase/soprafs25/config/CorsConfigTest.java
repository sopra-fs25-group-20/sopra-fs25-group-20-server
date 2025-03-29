package ch.uzh.ifi.hase.soprafs25.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

public class CorsConfigTest {

    @Test
    public void addCorsMappings() {
        CorsConfig config = new CorsConfig();
        CorsRegistry registry = mock(CorsRegistery.class);
        when(registry.allowedOrigins(any())).thenReturn(registry);
        when(registry.allowedMethods(any())).thenReturn(registry);

        config.addCorsMappings(registry);
        verify(registry).allowedOrigins(any());
        verify(registry).allowedMethods("*");
    }
}