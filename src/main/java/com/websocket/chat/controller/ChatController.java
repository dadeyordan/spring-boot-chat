package com.websocket.chat.controller;

import static java.lang.String.format;

import com.websocket.chat.model.ChatMessage;
import com.websocket.chat.model.MessageType;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

  @Autowired
  private SimpMessageSendingOperations messagingTemplate;

  @MessageMapping("/chat/{roomId}/register")
  public void register(
      @DestinationVariable String roomId,
      @Payload ChatMessage chatMessage,
      SimpMessageHeaderAccessor headerAccessor) {
    headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
    chatMessage.setDateTime(LocalDateTime.now());
    messagingTemplate.convertAndSend(format("/topic/%s", roomId), chatMessage);
  }

  @MessageMapping("/chat/{roomId}/welcome")
  public void welcome(
      @DestinationVariable String roomId,
      @Payload ChatMessage chatMessage) {
    ChatMessage adminChatMessage = ChatMessage.builder()
        .sender("Admin")
        .content(format("Welcome %s from the other world....", chatMessage.getSender()))
        .type(MessageType.CHAT)
        .build();

    messagingTemplate.convertAndSend(format("/topic/%s", roomId), adminChatMessage);
  }

  @MessageMapping("/chat/{roomId}/send")
  public void sendMessage(
      @DestinationVariable String roomId,
      @Payload ChatMessage chatMessage) {
    messagingTemplate.convertAndSend(format("/topic/%s", roomId), chatMessage);
  }
}