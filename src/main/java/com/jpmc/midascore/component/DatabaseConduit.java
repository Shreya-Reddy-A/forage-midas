package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.repository.UserRepository;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import org.springframework.stereotype.Component;
import java.util.Optional;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.entity.Incentive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;
import java.util.stream.StreamSupport;

@Component
public class DatabaseConduit {

    private final UserRepository userRepository;
    private final TransactionRecordRepository transactionRepository;

    @Autowired
    private RestTemplate restTemplate;

    private static final String INCENTIVE_URL = "http://localhost:8080/incentive";

    public DatabaseConduit(UserRepository userRepository,
                           TransactionRecordRepository transactionRepository) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    public void processTransaction(Long senderId, Long recipientId, float amount) {
        Optional<UserRecord> senderOpt = userRepository.findById(senderId);
        Optional<UserRecord> recipientOpt = userRepository.findById(recipientId);

        if (senderOpt.isEmpty() || recipientOpt.isEmpty()) return;

        UserRecord sender = senderOpt.get();
        UserRecord recipient = recipientOpt.get();

        if (sender.getBalance() < amount) return;

        // 💸 Log balances BEFORE transaction
        System.out.println("💸 BEFORE Transaction");
        System.out.println("Sender: " + sender.getName() + " | Balance: " + sender.getBalance());
        System.out.println("Recipient: " + recipient.getName() + " | Balance: " + recipient.getBalance());

        // Deduct from sender
        sender.setBalance(sender.getBalance() - amount);

        // Call Incentive API
        Transaction transactionRequest = new Transaction(sender.getId(), recipient.getId(), amount);
        Incentive incentive = restTemplate.postForObject(INCENTIVE_URL, transactionRequest, Incentive.class);
        float incentiveAmount = (incentive != null) ? incentive.getAmount() : 0f;

        System.out.println("🎁 Incentive received: " + incentiveAmount);

        // Add to recipient: amount + incentive
        recipient.setBalance(recipient.getBalance() + amount + incentiveAmount);

        // Save updated balances
        userRepository.save(sender);
        userRepository.save(recipient);

        // 💰 Log balances AFTER transaction
        System.out.println("💰 AFTER Transaction");
        System.out.println("Sender: " + sender.getName() + " | Balance: " + sender.getBalance());
        System.out.println("Recipient: " + recipient.getName() + " | Balance: " + recipient.getBalance());

        // Save transaction record
        TransactionRecord transaction = new TransactionRecord(amount, sender, recipient);
        transaction.setIncentive(incentiveAmount);
        transactionRepository.save(transaction);

        System.out.println("✅ Transaction saved: " + transaction);
    }

    public void save(UserRecord user) {
        userRepository.save(user);
    }

    // 🐽 Use this to print Wilbur’s final balance
    public void printWilburBalance() {
        Optional<UserRecord> wilburOpt = StreamSupport.stream(userRepository.findAll().spliterator(), false)
            .filter(u -> u.getName().equalsIgnoreCase("wilbur"))
            .findFirst();

        if (wilburOpt.isPresent()) {
            System.out.println("🐽 FINAL BALANCE (WILBUR): " + wilburOpt.get().getBalance());
        } else {
            System.out.println("❌ Wilbur not found in DB.");
        }
    }
}
