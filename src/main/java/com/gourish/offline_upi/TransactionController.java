
package com.gourish.offline_upi;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(
            TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/transfer")
    public Transaction transfer(
            @RequestBody TransferRequest request) {

        return transactionService.transfer(
                request.getSenderId(),
                request.getReceiverId(),
                request.getAmount()
        );
    }
}
