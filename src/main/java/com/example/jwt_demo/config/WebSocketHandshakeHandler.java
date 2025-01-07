package com.example.jwt_demo.config;

import java.security.Principal;
import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import org.springframework.web.util.UriComponentsBuilder;

public class WebSocketHandshakeHandler extends DefaultHandshakeHandler {
    
    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        String query = request.getURI().getQuery();
        if (query != null) {
            Map<String, String> queryParams = UriComponentsBuilder
                .fromUriString(request.getURI().toString())
                .build()
                .getQueryParams()
                .toSingleValueMap();
                
            String userId = queryParams.get("userId");
            if (userId != null) {
                return new UserPrincipal(userId);
            }
        }
        return null;
    }

    private static class UserPrincipal implements Principal {
        private final String name;

        public UserPrincipal(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }
} 