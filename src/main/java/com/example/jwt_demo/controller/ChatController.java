package com.example.jwt_demo.controller;

import java.security.Principal;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.example.jwt_demo.model.ChatMessage;
import com.example.jwt_demo.service.UserStatusService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final UserStatusService userStatusService;

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
        String userId = principal.getName();
        userStatusService.userConnected(userId);
        
        // Отправляем сообщение о присоединении
        chatMessage.setSender(userId);
        chatMessage.setType(ChatMessage.MessageType.JOIN);
        messagingTemplate.convertAndSendToUser(
            chatMessage.getRecipient(),
            "/queue/messages",
            chatMessage
        );

        // Отправляем свой статус ONLINE
        sendStatusMessage(userId, "ONLINE");
        
        // Отправляем статусы всех онлайн пользователей
        userStatusService.getAllUserStatuses().forEach(status -> 
            messagingTemplate.convertAndSend("/topic/status", status)
        );
    }

    @MessageMapping("/user.online")
    public void userOnline(Principal principal) {
        String userId = principal.getName();
        userStatusService.userConnected(userId);
        sendStatusMessage(userId, "ONLINE");
    }

    @MessageMapping("/user.offline")
    public void userOffline(Principal principal) {
        String userId = principal.getName();
        userStatusService.userDisconnected(userId);
        sendStatusMessage(userId, "OFFLINE");
    }

    @MessageMapping("/user.status.check")
    public void checkUserStatus(@Payload ChatMessage message) {
        System.out.println("Status check requested. Current online users: " + userStatusService.getOnlineUsers());
        // Отправляем статусы всех онлайн пользователей
        userStatusService.getAllUserStatuses().forEach(status -> {
            System.out.println("Sending status message: " + status);
            messagingTemplate.convertAndSend("/topic/status", status);
        });
    }

    private void sendStatusMessage(String userId, String status) {
        ChatMessage statusMessage = new ChatMessage();
        statusMessage.setType(ChatMessage.MessageType.STATUS);
        statusMessage.setSender(userId);
        statusMessage.setContent(status);
        System.out.println("Sending individual status message: " + statusMessage);
        messagingTemplate.convertAndSend("/topic/status", statusMessage);
    }
} 