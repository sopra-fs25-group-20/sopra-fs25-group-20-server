package ch.uzh.ifi.hase.soprafs25.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CorsConfigTest {

    @Test
    public void addCorsMappings() {
        CorsConfig config = new CorsConfig();
        CorsRegistry registry = mock(CorsRegistry.class);
        CorsRegistration registration = mock(CorsRegistration.class);
        
        when(registry.addMapping("/**")).thenReturn(registration);
        when(registration.allowedOrigins(any())).thenReturn(registration);
        when(registration.allowedMethods(any())).thenReturn(registration);

        config.addCorsMappings(registry);

        verify(registry).addMapping("/**");
        verify(registration).allowedOrigins(any());
        verify(registration).allowedMethods("*");
    }
}