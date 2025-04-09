package com.jpmc.midascore.service;

import com.jpmc.midascore.entity.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class IncentiveService {

    private final RestTemplate restTemplate;

    public IncentiveService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Incentive fetchIncentiveForTransaction(Transaction transaction) {
        String incentiveApiUrl = "http://localhost:8080/incentive";
        return restTemplate.postForObject(incentiveApiUrl, transaction, Incentive.class);
    }
    public float getIncentiveAmount(Long senderId, Long recipientId, float amount) {
        Transaction transaction = new Transaction(senderId, recipientId, amount);
        Incentive incentive = fetchIncentiveForTransaction(transaction);
        return (incentive != null) ? incentive.getAmount() : 0;
    }
}
