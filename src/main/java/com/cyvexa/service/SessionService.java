package com.cyvexa.service;

import com.cyvexa.model.User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SessionService {
    private final Map<String, User> sessions = new ConcurrentHashMap<>();

    public String createSession(User user) {
        String token = UUID.randomUUID().toString();
        sessions.put(token, user);
        return token;
    }

    public User getUser(String token) {
        return sessions.get(token);
    }

    public boolean isValid(String token) {
        return token != null && sessions.containsKey(token);
    }

    public void removeSession(String token) {
        sessions.remove(token);
    }

    public void clearAllSessions() {
        sessions.clear();
    }
}
