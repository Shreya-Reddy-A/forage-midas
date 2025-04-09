package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.repository.UserRepository;
import com.jpmc.midascore.service.IncentiveService;
import com.jpmc.midascore.service.TransactionService;
import com.jpmc.midascore.service.UserService;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import org.springframework.stereotype.Component;
import java.util.Optional;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.entity.Incentive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;
import java.util.stream.StreamSupport;
//import java.util.stream.Stream;

@Component
public class DatabaseConduit {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRecordRepository transactionRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private IncentiveService incentiveService;

    // ✅ Method 1: Transaction by IDs
    public void processTransaction(Long senderId, Long recipientId, float amount) {
        UserRecord sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        UserRecord recipient = userRepository.findById(recipientId)
                .orElseThrow(() -> new RuntimeException("Recipient not found"));

        if (sender.getBalance() < amount) {
            throw new RuntimeException("Insufficient balance");
        }

        System.out.println("💸 BEFORE Transaction");
        System.out.println("Sender: " + sender.getName() + " | Balance: " + sender.getBalance());
        System.out.println("Recipient: " + recipient.getName() + " | Balance: " + recipient.getBalance());

        // Deduct sender balance
        sender.setBalance(sender.getBalance() - amount);

        // Call Incentive service
        float incentiveAmount = incentiveService.getIncentiveAmount(senderId, recipientId, amount);
        System.out.println("🎁 Incentive received: " + incentiveAmount);

        // Add to recipient balance
        recipient.setBalance(recipient.getBalance() + amount + incentiveAmount);

        // Save updated users
        userRepository.save(sender);
        userRepository.save(recipient);

        // Save transaction record
        TransactionRecord transactionRecord = new TransactionRecord(sender, recipient, amount, incentiveAmount);
        transactionRepository.save(transactionRecord);

        System.out.println("💰 AFTER Transaction");
        System.out.println("Sender: " + sender.getName() + " | Balance: " + sender.getBalance());
        System.out.println("Recipient: " + recipient.getName() + " | Balance: " + recipient.getBalance());
    }

    // ✅ Method 2: Transaction by UserRecord objects
    public void processTransaction(UserRecord sender, UserRecord recipient, float amount) {
        if (sender.getBalance() < amount) {
            throw new RuntimeException("Insufficient balance");
        }

        System.out.println("💸 BEFORE Transaction");
        System.out.println("Sender: " + sender.getName() + " | Balance: " + sender.getBalance());
        System.out.println("Recipient: " + recipient.getName() + " | Balance: " + recipient.getBalance());

        // Deduct sender balance
        sender.setBalance(sender.getBalance() - amount);

        // Call Incentive service
        float incentiveAmount = incentiveService.getIncentiveAmount(sender.getId(), recipient.getId(), amount);
        System.out.println("🎁 Incentive received: " + incentiveAmount);

        // Add to recipient balance
        recipient.setBalance(recipient.getBalance() + amount + incentiveAmount);

        // Save updated users
        userRepository.save(sender);
        userRepository.save(recipient);

        // Save transaction record
        TransactionRecord transactionRecord = new TransactionRecord(sender, recipient, amount, incentiveAmount);
        transactionRepository.save(transactionRecord);

        System.out.println("💰 AFTER Transaction");
        System.out.println("Sender: " + sender.getName() + " | Balance: " + sender.getBalance());
        System.out.println("Recipient: " + recipient.getName() + " | Balance: " + recipient.getBalance());
    }

    // ✅ Optional save user method
    public void save(UserRecord user) {
        userRepository.save(user);

    }
    public void printWilburBalance() {
        Optional<UserRecord> wilburOptional = userRepository.findByName("Wilbur");
        if (wilburOptional.isPresent()) {
            UserRecord wilbur = wilburOptional.get();
            System.out.println("🐷 Wilbur's Balance: " + wilbur.getBalance());
        } else {
            System.out.println("🐷 Wilbur not found in the database.");
        }
    }
    
}