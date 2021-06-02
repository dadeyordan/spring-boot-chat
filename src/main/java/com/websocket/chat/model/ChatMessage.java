package com.websocket.chat.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatMessage {

  private String content;
  private String sender;
  private MessageType type;
}
