package ch.uzh.ifi.hase.soprafs25.util;

import org.junit.jupiter.api.Test;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Constructor;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SessionUtilTest {

    @Test
    void constructor_throwsIllegalStateException() throws Exception {
        Constructor<SessionUtil> constructor = SessionUtil.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        InvocationTargetException ex = assertThrows(InvocationTargetException.class, constructor::newInstance);
        assertTrue(ex.getCause() instanceof IllegalStateException);
        assertEquals("Utility class", ex.getCause().getMessage());
    }

    @Test
    void getNickname_returnsCorrectValue() {
        Message<?> message = MessageBuilder.withPayload("payload")
                .setHeader("simpSessionAttributes", Map.of("nickname", "Alice"))
                .build();

        String nickname = SessionUtil.getNickname(message);
        assertEquals("Alice", nickname);
    }

    @Test
    void getAttribute_missingKey_throwsException() {
        Message<?> message = MessageBuilder.withPayload("payload")
                .setHeader("simpSessionAttributes", Map.of("nickname", "Alice"))
                .build();

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> SessionUtil.getCode(message));
        assertTrue(ex.getMessage().contains("Missing WebSocket session attribute: code"));
    }

    @Test
    void getAttribute_nullSessionAttributes_throwsException() {
        Message<?> message = MessageBuilder.withPayload("payload")
                .build(); // no simpSessionAttributes

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> SessionUtil.getColor(message));
        assertTrue(ex.getMessage().contains("Missing WebSocket session attribute: color"));
    }
}
