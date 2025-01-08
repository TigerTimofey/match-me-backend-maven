package com.example.jwt_demo.service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.jwt_demo.model.ChatMessage;
@Service
public class UserStatusService {
    private final Set<String> onlineUsers = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public void userConnected(String userId) {
        onlineUsers.add(userId);
        System.out.println("Connected users: " + onlineUsers);
    }

    public void userDisconnected(String userId) {
        onlineUsers.remove(userId);
        System.out.println("Connected users after disconnect: " + onlineUsers);
    }

    public boolean isUserOnline(String userId) {
        return onlineUsers.contains(userId);
    }

    public Set<String> getOnlineUsers() {
        return Collections.unmodifiableSet(onlineUsers);
    }

    public List<ChatMessage> getAllUserStatuses() {
        return onlineUsers.stream()
            .map(userId -> {
                ChatMessage statusMessage = new ChatMessage();
                statusMessage.setType(ChatMessage.MessageType.STATUS);
                statusMessage.setSender(userId);
                statusMessage.setContent("ONLINE");
                return statusMessage;
            })
            .collect(Collectors.toList());
    }
} 