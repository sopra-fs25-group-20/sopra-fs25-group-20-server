package ch.uzh.ifi.hase.soprafs25.config;


import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.StompWebSocketEndpointRegistration;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class WebSocketConfigTest {
    
    @Test
    public void testConfigureMessageBroker() {
        CustomHandshakeInterceptor interceptor = mock(CustomHandshakeInterceptor.class);

        WebSocketConfig config = new WebSocketConfig(interceptor);

        MessageBrokerRegistry registry = mock(MessageBrokerRegistry.class);

        config.configureMessageBroker(registry);
        verify(registry).enableSimpleBroker("/topic");
        verify(registry).setApplicationDestinationPrefixes("/app");
    }

    @Test
    void testRegisterStompEndpoints() {
        CustomHandshakeInterceptor interceptor = mock(CustomHandshakeInterceptor.class);
        WebSocketConfig config = new WebSocketConfig(interceptor);

        StompEndpointRegistry registry = mock(StompEndpointRegistry.class);
        StompWebSocketEndpointRegistration registration = mock(StompWebSocketEndpointRegistration.class);

        when(registry.addEndpoint("/ws")).thenReturn(registration);
        when(registration.addInterceptors(interceptor)).thenReturn(registration);
        when(registration.setAllowedOrigins(any())).thenReturn(registration);

        config.registerStompEndpoints(registry);

        verify(registry).addEndpoint("/ws");
        verify(registration).addInterceptors(interceptor);
        verify(registration).setAllowedOrigins(any());
    }
}
