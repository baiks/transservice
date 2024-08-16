package com.trans.service.services;

import com.trans.service.dtos.TransactionRequest;
import com.trans.service.dtos.TransactionResponse;
import org.springframework.http.ResponseEntity;


public interface TransactionsService {
    /**
     * @param transactionRequest
     * @param customerId
     * @return
     */
    ResponseEntity<TransactionResponse> deposit(TransactionRequest transactionRequest, Long customerId);

    /**
     *
     * @param transactionRequest
     * @param customerId
     * @return
     */
    ResponseEntity<TransactionResponse> withdrawal(TransactionRequest transactionRequest, Long customerId);

    /**
     *
     * @param transactionRequest
     * @param customerId
     * @return
     */
    ResponseEntity<TransactionResponse> transfer(TransactionRequest transactionRequest, Long customerId);
}