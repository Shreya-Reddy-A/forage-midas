package com.jpmc.midascore.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

    // Assuming this is your in-memory store for now — in real app, this would come from DB
    private final Map<Long, Float> userBalances = new HashMap<>();

    public void decreaseBalance(long userId, float amount) {
        float currentBalance = userBalances.getOrDefault(userId, 0.0f);
        if (currentBalance < amount) {
            throw new IllegalArgumentException("Insufficient balance for user: " + userId);
        }
        userBalances.put(userId, currentBalance - amount);
        System.out.println("Decreased balance for user " + userId + ". New balance: " + userBalances.get(userId));
    }

    public void increaseBalance(long userId, float amount) {
        float currentBalance = userBalances.getOrDefault(userId, 0.0f);
        userBalances.put(userId, currentBalance + amount);
        System.out.println("Increased balance for user " + userId + ". New balance: " + userBalances.get(userId));
    }

    public float getBalance(long userId) {
        return userBalances.getOrDefault(userId, 0.0f);
    }

    // Optional: If you're planning to preload balances from DB or other source
    public void setInitialBalance(long userId, float balance) {
        userBalances.put(userId, balance);
    }
}
