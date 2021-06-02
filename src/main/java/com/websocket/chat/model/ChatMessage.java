package com.websocket.chat.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatMessage {

  private String content;
  private String sender;
  @Default
  @JsonFormat(pattern = "dd-MM-yyyy hh:mm:ss")
  private LocalDateTime dateTime = LocalDateTime.now();
  private MessageType type;
}
