package com.jpmc.midascore.service;

import com.jpmc.midascore.foundation.Transaction;
import org.springframework.stereotype.Service;
import com.jpmc.midascore.entity.Incentive;


@Service
public class TransactionService {

    private final IncentiveService incentiveService;
    private final UserService userService; 

    public TransactionService(IncentiveService incentiveService, UserService userService) {
        this.incentiveService = incentiveService;
        this.userService = userService;
    }

    public void processTransaction(Transaction transaction) {
        validateTransaction(transaction);

        Incentive incentive = incentiveService.fetchIncentiveForTransaction(transaction);
        float incentiveAmount = incentive != null ? incentive.getAmount() : 0.0f;

        updateBalances(transaction, incentiveAmount);

        transaction.setIncentive(incentiveAmount);
    }

    private void validateTransaction(Transaction transaction) {
        if (transaction.getAmount() <= 0) {
            throw new IllegalArgumentException("Transaction amount must be positive");
        }

        float senderBalance = userService.getBalance(transaction.getSenderId());
        if (senderBalance < transaction.getAmount()) {
            throw new IllegalArgumentException("Sender has insufficient balance");
        }
    }

    private void updateBalances(Transaction transaction, float incentiveAmount) {
        userService.decreaseBalance(transaction.getSenderId(), transaction.getAmount());
        userService.increaseBalance(transaction.getRecipientId(), transaction.getAmount() + incentiveAmount);
    }
}