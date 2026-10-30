
package com.gourish.offline_upi;

import java.math.BigDecimal;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public TransactionService(
            AccountRepository accountRepository,
            TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

   
@Transactional
public Transaction transfer(
        Long senderId,
        Long receiverId,
        BigDecimal amount) {

    validateTransfer(senderId, receiverId, amount);

   
Long firstId = Math.min(senderId, receiverId);
Long secondId = Math.max(senderId, receiverId);

Account firstAccount = accountRepository.findByIdForUpdate(firstId)
        .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Account not found: " + firstId
        ));

Account secondAccount = accountRepository.findByIdForUpdate(secondId)
        .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Account not found: " + secondId
        ));

Account sender;
Account receiver;

if (senderId.equals(firstId)) {
    sender = firstAccount;
    receiver = secondAccount;
} else {
    sender = secondAccount;
    receiver = firstAccount;
}


    
if (sender.getBalance() == null) {
    throw new IllegalStateException(
            "Sender account balance is missing");
}

if (receiver.getBalance() == null) {
    throw new IllegalStateException(
            "Receiver account balance is missing");
}


if (sender.getBalance().compareTo(amount) < 0) {
    throw new IllegalArgumentException(
            "Insufficient balance");
}


sender.setBalance(sender.getBalance().subtract(amount));

receiver.setBalance(receiver.getBalance().add(amount));

Transaction transaction = new Transaction();

transaction.setSender(sender);
transaction.setReceiver(receiver);
transaction.setAmount(amount);
transaction.setStatus("SUCCESS");
transaction.setCreatedAt(java.time.LocalDateTime.now());

return transactionRepository.save(transaction);




    
}


    private void validateTransfer(
            Long senderId,
            Long receiverId,
            BigDecimal amount) {

        if (senderId == null || receiverId == null) {
            throw new IllegalArgumentException(
                    "Sender and receiver IDs are required");
        }

        if (senderId.equals(receiverId)) {
            throw new IllegalArgumentException(
                    "Sender and receiver cannot be the same");
        }

        if (amount == null ||
                amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Transfer amount must be greater than zero");
        }
    }
}
