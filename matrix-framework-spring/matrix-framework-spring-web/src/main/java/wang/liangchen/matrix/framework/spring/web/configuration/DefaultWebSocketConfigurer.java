package wang.liangchen.matrix.framework.spring.web.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;
import wang.liangchen.matrix.framework.spring.boot.event.EventPublisher;
import wang.liangchen.matrix.framework.spring.web.event.WebSocketMessageEvent;
import wang.liangchen.matrix.framework.spring.web.utils.PushUtil;


/**
 * @author Liangchen.Wang
 */
@Configuration
@EnableWebSocket
public class DefaultWebSocketConfigurer implements WebSocketConfigurer {
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new TextWebSocketHandler() {
            @Override
            public void afterConnectionEstablished(WebSocketSession session) throws Exception {
                PushUtil.INSTANCE.onWebSocketOpen(session);
            }

            @Override
            protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
                EventPublisher.INSTANCE.publishEvent(new WebSocketMessageEvent(this, message.getPayload()));
            }

            @Override
            public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
                PushUtil.INSTANCE.onWebSocketError(session).accept(exception);
            }

            @Override
            public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
                if (CloseStatus.NORMAL.getCode() == status.getCode()) {
                    PushUtil.INSTANCE.onWebSocketCompletion(session).run();
                    return;
                }
                if (CloseStatus.SESSION_NOT_RELIABLE == status) {
                    PushUtil.INSTANCE.onWebSocketTimeout(session).run();
                    return;
                }
                PushUtil.INSTANCE.onWebSocketError(session).accept(new Exception(status.getReason()));
            }
        }, "/websocket").addInterceptors(new HttpSessionHandshakeInterceptor());
    }
}
