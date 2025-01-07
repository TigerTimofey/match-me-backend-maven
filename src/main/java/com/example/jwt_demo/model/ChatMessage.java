package com.example.jwt_demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    private String content;
    private String sender;
    private String recipient;
    private MessageType type;

    public enum MessageType {
        CHAT, JOIN, LEAVE, STATUS, STATUS_CHECK
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "content='" + content + '\'' +
                ", sender='" + sender + '\'' +
                ", recipient='" + recipient + '\'' +
                ", type=" + type +
                '}';
    }
}