package com.websocket.chat.config;

import com.websocket.chat.model.ChatMessage;
import com.websocket.chat.model.MessageType;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;


@Slf4j
@Component
public class WebSocketChatEventListener {

  @Autowired
  private SimpMessageSendingOperations messagingTemplate;

  @EventListener
  public void handleWebSocketConnectListener(SessionConnectedEvent event) {
    log.info("Received a new web socket connection");
  }

  @EventListener
  public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
    StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
    Optional.ofNullable(headerAccessor)
        .filter(accessor -> Objects.nonNull(accessor.getSessionAttributes()) &&
            accessor.getSessionAttributes().containsKey("username"))
        .ifPresent(accessor -> {
          String username = (String) accessor.getSessionAttributes().get("username");
          ChatMessage chatMessage = ChatMessage.builder()
              .type(MessageType.LEAVE)
              .sender(username)
              .build();
          messagingTemplate.convertAndSend("/topic/public", chatMessage);
        });
  }
}