package com.example.jwt_demo.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.data.domain.Pageable;
import com.example.jwt_demo.model.ChatMessage;
import com.example.jwt_demo.model.MessageEntity;
import com.example.jwt_demo.repository.MessageRepository;
import com.example.jwt_demo.service.UserStatusService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final UserStatusService userStatusService;
    private final MessageRepository messageRepository;

    @MessageMapping("/chat.acceptBra")
    public void sendAcceptBraMessage() {
        // Логируем полученное сообщение
        System.out.println("Received 'ACCEPT BRA'");

        // Создаем сообщение для фронтенда
        ChatMessage responseMessage = new ChatMessage();
        responseMessage.setSender("Server");
        responseMessage.setRecipient("User");
        responseMessage.setContent("Accepted"); // Отправляем "Accepted" обратно
        responseMessage.setType(ChatMessage.MessageType.TEXT);
        responseMessage.setTimestamp(LocalDateTime.now().toString());

        // Отправляем сообщение в топик
        messagingTemplate.convertAndSend("/topic/messages", responseMessage);
        System.out.println("Sending 'Accepted' message to /topic/messages");
    }

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessage chatMessage, Principal principal) {
        LocalDateTime now = LocalDateTime.now();
        chatMessage.setSender(chatMessage.getSender());
        chatMessage.setTimestamp(now.toString());

        // Set read as false initially for new messages
        chatMessage.setRead(false); // Set read status as false initially

        // Save the message entity
        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setContent(chatMessage.getContent());
        messageEntity.setSender(chatMessage.getSender());
        messageEntity.setRecipient(chatMessage.getRecipient());
        messageEntity.setType(chatMessage.getType());
        messageEntity.setTimestamp(now);
        messageEntity.setRead(false); // Ensure it's marked as unread when stored

        // Save to database
        messageRepository.save(messageEntity);

        // Send the message to both recipient and sender
        messagingTemplate.convertAndSendToUser(
                chatMessage.getRecipient(),
                "/queue/messages",
                chatMessage);
        messagingTemplate.convertAndSendToUser(
                chatMessage.getSender(),
                "/queue/messages",
                chatMessage);
    }

    @MessageMapping("/chat.markMessagesRead")
    public void markMessagesAsRead(@Payload ChatMessage chatMessage) {
        // Find the unread messages in the database based on sender and recipient
        List<MessageEntity> unreadMessages = messageRepository
                .findBySenderAndRecipientOrRecipientAndSenderOrderByTimestamp(
                        chatMessage.getSender(), chatMessage.getRecipient(), chatMessage.getRecipient(),
                        chatMessage.getSender());

        // Mark each message as read
        unreadMessages.forEach(message -> {
            if (!message.isRead()) {
                message.setRead(true); // Mark the message as read
                messageRepository.save(message); // Save updated message status in the database
            }
        });

        // Send updated status to the client (if needed)
        messagingTemplate.convertAndSendToUser(
                chatMessage.getSender(),
                "/queue/messages",
                chatMessage);
    }

    @MessageMapping("/chat.join")
    public void joinChat(@Payload ChatMessage chatMessage, Principal principal) {
        String userId = chatMessage.getSender();
        userStatusService.userConnected(userId);

        // Отправляем сообщение о присоединении
        chatMessage.setSender(userId);
        chatMessage.setType(ChatMessage.MessageType.JOIN);
        messagingTemplate.convertAndSendToUser(
                chatMessage.getRecipient(),
                "/queue/messages",
                chatMessage);

        // Отправляем свой статус ONLINE
        sendStatusMessage(userId, "ONLINE");

        // Отправляем статусы всех онлайн пользователей
        userStatusService.getAllUserStatuses()
                .forEach(status -> messagingTemplate.convertAndSend("/topic/status", status));
    }

    @MessageMapping("/user.online")
    public void userOnline(@Payload ChatMessage chatMessage) {
        String userId = chatMessage.getSender();
        userStatusService.userConnected(userId);
        sendStatusMessage(userId, "ONLINE");
    }

    @MessageMapping("/user.offline")
    public void userOffline(@Payload ChatMessage chatMessage) {
        String userId = chatMessage.getSender();
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

    @MessageMapping("/chat.typing")
    public void handleTypingStatus(@Payload ChatMessage chatMessage, Principal principal) {
        System.out.println("=== Получено сообщение о печатании ===");
        System.out.println("От пользователя: " + principal.getName());
        System.out.println("Для пользователя: " + chatMessage.getRecipient());

        chatMessage.setSender(chatMessage.getSender());
        chatMessage.setType(ChatMessage.MessageType.TYPING);

        System.out.println("Отправляем уведомление о печатании получателю: " + chatMessage.getRecipient());

        try {
            messagingTemplate.convertAndSendToUser(
                    chatMessage.getRecipient(),
                    "/queue/messages",
                    chatMessage);
            System.out.println("Уведомление о печатании успешно отправлено");
        } catch (Exception e) {
            System.err.println("Ошибка при отправке уведомления о печатании: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void sendStatusMessage(String userId, String status) {
        ChatMessage statusMessage = new ChatMessage();
        statusMessage.setType(ChatMessage.MessageType.STATUS);
        statusMessage.setSender(userId);
        statusMessage.setContent(status);
        System.out.println("Sending individual status message: " + statusMessage);
        messagingTemplate.convertAndSend("/topic/status", statusMessage);
    }

    @GetMapping("/api/messages/{userId1}/{userId2}")
    @ResponseBody
    public List<ChatMessage> getChatHistory(@PathVariable String userId1, @PathVariable String userId2) {
        System.out.println("Getting chat history for users: " + userId1 + " and " + userId2);
        List<MessageEntity> messages = messageRepository.findBySenderAndRecipientOrRecipientAndSenderOrderByTimestamp(
                userId1, userId2, userId2, userId1);

        // Для каждого сообщения определяем, отправлено ли оно запрашивающим
        // пользователем
        messages.forEach(msg -> msg.setSentByRequester(msg.getSender().equals(userId1)));

        System.out.println("Found messages: " + messages.size());

        return messages.stream()
                .map(msg -> {
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.setContent(msg.getContent());
                    chatMessage.setSender(msg.getSender());
                    chatMessage.setRecipient(msg.getRecipient());
                    chatMessage.setType(msg.getType());
                    chatMessage.setTimestamp(msg.getTimestamp().toString());
                    chatMessage.setSentByMe(msg.isSentByRequester()); // Добавляем информацию о том, кто отправил
                                                                      // сообщение
                    return chatMessage;
                })
                .collect(Collectors.toList());
    }

    @GetMapping("/api/messages/{userId1}/{userId2}/paginated")
    @ResponseBody
    public Map<String, Object> getPaginatedChatHistory(
            @PathVariable String userId1,
            @PathVariable String userId2,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        System.out.println("Getting paginated chat history for users: " + userId1 + " and " + userId2);
        System.out.println("Page: " + page + ", Size: " + size);

        // Создаем объект Pageable
        Pageable pageable = PageRequest.of(page, size);

        // Получаем страницу сообщений
        List<MessageEntity> messages = messageRepository.findPaginatedMessages(
                userId1, userId2, pageable);

        // Получаем общее количество сообщений
        long totalMessages = messageRepository.countMessagesBetweenUsers(userId1, userId2);

        // Конвертируем сообщения в DTO
        List<ChatMessage> chatMessages = messages.stream()
                .map(msg -> {
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.setContent(msg.getContent());
                    chatMessage.setSender(msg.getSender());
                    chatMessage.setRecipient(msg.getRecipient());
                    chatMessage.setType(msg.getType());
                    chatMessage.setTimestamp(msg.getTimestamp().toString());
                    chatMessage.setSentByMe(msg.getSender().equals(userId1));
                    return chatMessage;
                })
                .collect(Collectors.toList());

        // Формируем ответ
        Map<String, Object> response = new HashMap<>();
        response.put("messages", chatMessages);
        response.put("currentPage", page);
        response.put("totalPages", (int) Math.ceil((double) totalMessages / size));
        response.put("totalMessages", totalMessages);
        response.put("hasNext", (page + 1) * size < totalMessages);
        response.put("hasPrevious", page > 0);

        return response;
    }
}
