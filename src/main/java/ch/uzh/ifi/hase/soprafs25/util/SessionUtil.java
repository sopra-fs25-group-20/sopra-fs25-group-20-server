package ch.uzh.ifi.hase.soprafs25.util;

import org.springframework.messaging.Message;

import java.util.Map;

public class SessionUtil {

    public static String getNickname(Message<?> message) {
        return getAttribute(message, "nickname");
    }

    public static String getCode(Message<?> message) {
        return getAttribute(message, "code");
    }

    public static String getColor(Message<?> message) {
        return getAttribute(message, "color");
    }

    private static String getAttribute(Message<?> message, String key) {
        Map<String, Object> attrs = (Map<String, Object>) message.getHeaders().get("simpSessionAttributes");
        if (attrs == null || !attrs.containsKey(key)) {
            throw new IllegalStateException("Missing WebSocket session attribute: " + key);
        }
        return (String) attrs.get(key);
    }
}
