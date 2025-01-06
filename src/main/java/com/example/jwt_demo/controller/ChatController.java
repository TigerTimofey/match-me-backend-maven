package com.example.jwt_demo.controller;

import java.security.Principal;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.example.jwt_demo.model.ChatMessage;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessage chatMessage, Principal principal) {
        chatMessage.setSender(principal.getName());
        messagingTemplate.convertAndSendToUser(
            chatMessage.getRecipient(),
            "/queue/messages",
            chatMessage
        );
    }

    @MessageMapping("/chat.join")
    public void joinChat(@Payload ChatMessage chatMessage, Principal principal) {
        chatMessage.setSender(principal.getName());
        chatMessage.setType(ChatMessage.MessageType.JOIN);
        messagingTemplate.convertAndSendToUser(
            chatMessage.getRecipient(),
            "/queue/messages",
            chatMessage
        );
    }
} 