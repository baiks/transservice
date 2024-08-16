package com.trans.service.impl;


import com.google.gson.Gson;
import com.trans.service.dtos.NotificationDto;
import com.trans.service.dtos.TransactionRequest;
import com.trans.service.dtos.TransactionResponse;
import com.trans.service.entities.Accounts;
import com.trans.service.entities.Customers;
import com.trans.service.kafka.Producer;
import com.trans.service.repos.AccountsRepo;
import com.trans.service.repos.CustomersRepo;
import com.trans.service.services.TransactionsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

import com.trans.service.configs.Constants;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionsServiceImpl implements TransactionsService {

    private final Producer producer;
    private final AccountsRepo accountsRepo;
    private final CustomersRepo customersRepo;

    /**
     * @param transactionRequest
     * @param customerId
     * @return
     */
    @Override
    public ResponseEntity<TransactionResponse> deposit(TransactionRequest transactionRequest, Long customerId) {
        TransactionResponse transactionResponse = new TransactionResponse();
        if (transactionRequest.getAmount() <= 0) {
            transactionResponse.setStatusCode(Constants.Responses.codes.INVALID_AMOUNT);
            transactionResponse.setStatusMessage(Constants.Responses.messages.INVALID_AMOUNT);
            return new ResponseEntity<>(transactionResponse, HttpStatus.BAD_REQUEST);
        }
        Optional<Customers> customers = customersRepo.findById(customerId);
        if (!customers.isPresent()) {
            transactionResponse.setStatusCode(Constants.Responses.codes.INVALID_CUSTOMER);
            transactionResponse.setStatusMessage(Constants.Responses.messages.INVALID_CUSTOMER);
            return new ResponseEntity<>(transactionResponse, HttpStatus.BAD_REQUEST);
        }
        if (!accountsRepo.findByCustomer(customers.get()).isPresent()) {
            transactionResponse.setStatusCode(Constants.Responses.codes.INVALID_CR_ACCOUNT);
            transactionResponse.setStatusMessage(Constants.Responses.messages.INVALID_CR_ACCOUNT);
            return new ResponseEntity<>(transactionResponse, HttpStatus.BAD_REQUEST);
        }
        Optional<Accounts> account = accountsRepo.findByAccountNumber(transactionRequest.getAccountFrom());
        if (!account.isPresent()) {
            transactionResponse.setStatusCode(Constants.Responses.codes.INVALID_CR_ACCOUNT);
            transactionResponse.setStatusMessage(Constants.Responses.messages.INVALID_CR_ACCOUNT);
            return new ResponseEntity<>(transactionResponse, HttpStatus.BAD_REQUEST);
        }
        //Could have done better by handling accounting entries
        account.get().setBalance(transactionRequest.getAmount() + account.get().getBalance());
        accountsRepo.save(account.get());
        transactionResponse.setStatusMessage(Constants.Responses.messages.SUCCESS);
        transactionResponse.setStatusCode(Constants.Responses.codes.SUCCESS);
        //Could do better by maintaining a message template
        String message = "Dear Customer, your deposit has been processed successfully";
        NotificationDto notificationDto = NotificationDto.builder().message(message).recipient(customers.get().getMobileNumber()).type(1).build();
        producer.sendMessage("notifications-topic", new Gson().toJson(notificationDto));
        return new ResponseEntity<>(transactionResponse, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<TransactionResponse> withdrawal(TransactionRequest transactionRequest, Long customerId) {
        TransactionResponse transactionResponse = new TransactionResponse();
        if (transactionRequest.getAmount() <= 0) {
            transactionResponse.setStatusCode(Constants.Responses.codes.INVALID_AMOUNT);
            transactionResponse.setStatusMessage(Constants.Responses.messages.INVALID_AMOUNT);
            return new ResponseEntity<>(transactionResponse, HttpStatus.BAD_REQUEST);
        }
        Optional<Customers> customers = customersRepo.findById(customerId);
        if (!customers.isPresent()) {
            transactionResponse.setStatusCode(Constants.Responses.codes.INVALID_CUSTOMER);
            transactionResponse.setStatusMessage(Constants.Responses.messages.INVALID_CUSTOMER);
            return new ResponseEntity<>(transactionResponse, HttpStatus.BAD_REQUEST);
        }
        if (!accountsRepo.findByCustomer(customers.get()).isPresent()) {
            transactionResponse.setStatusCode(Constants.Responses.codes.INVALID_CR_ACCOUNT);
            transactionResponse.setStatusMessage(Constants.Responses.messages.INVALID_CR_ACCOUNT);
            return new ResponseEntity<>(transactionResponse, HttpStatus.BAD_REQUEST);
        }
        Optional<Accounts> account = accountsRepo.findByAccountNumber(transactionRequest.getAccountFrom());
        if (!account.isPresent()) {
            transactionResponse.setStatusCode(Constants.Responses.codes.INVALID_CR_ACCOUNT);
            transactionResponse.setStatusMessage(Constants.Responses.messages.INVALID_CR_ACCOUNT);
            return new ResponseEntity<>(transactionResponse, HttpStatus.BAD_REQUEST);
        }
        if (account.get().getBalance() < transactionRequest.getAmount()) {
            transactionResponse.setStatusCode(Constants.Responses.codes.INSUFFICIENT_FUNDS);
            transactionResponse.setStatusMessage(Constants.Responses.messages.INSUFFICIENT_FUNDS);
            return new ResponseEntity<>(transactionResponse, HttpStatus.BAD_REQUEST);
        }
        //Could have done better by handling accounting entries
        account.get().setBalance(account.get().getBalance() - transactionRequest.getAmount());
        accountsRepo.save(account.get());
        transactionResponse.setStatusMessage(Constants.Responses.messages.SUCCESS);
        transactionResponse.setStatusCode(Constants.Responses.codes.SUCCESS);
        //Could do better by maintaining a message template
        String message = "Dear Customer, your cash withdraw has been processed successfully";
        NotificationDto notificationDto = NotificationDto.builder().message(message).recipient(customers.get().getMobileNumber()).type(1).build();
        producer.sendMessage("notifications-topic", new Gson().toJson(notificationDto));
        return new ResponseEntity<>(transactionResponse, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<TransactionResponse> transfer(TransactionRequest transactionRequest, Long customerId) {
        TransactionResponse transactionResponse = new TransactionResponse();
        if (transactionRequest.getAmount() <= 0) {
            transactionResponse.setStatusCode(Constants.Responses.codes.INVALID_AMOUNT);
            transactionResponse.setStatusMessage(Constants.Responses.messages.INVALID_AMOUNT);
            return new ResponseEntity<>(transactionResponse, HttpStatus.BAD_REQUEST);
        }
        Optional<Customers> customers = customersRepo.findById(customerId);
        if (!customers.isPresent()) {
            transactionResponse.setStatusCode(Constants.Responses.codes.INVALID_CUSTOMER);
            transactionResponse.setStatusMessage(Constants.Responses.messages.INVALID_CUSTOMER);
            return new ResponseEntity<>(transactionResponse, HttpStatus.BAD_REQUEST);
        }
        if (!accountsRepo.findByCustomer(customers.get()).isPresent()) {
            transactionResponse.setStatusCode(Constants.Responses.codes.INVALID_CR_ACCOUNT);
            transactionResponse.setStatusMessage(Constants.Responses.messages.INVALID_CR_ACCOUNT);
            return new ResponseEntity<>(transactionResponse, HttpStatus.BAD_REQUEST);
        }
        Optional<Accounts> account = accountsRepo.findByAccountNumber(transactionRequest.getAccountFrom());
        if (!account.isPresent()) {
            transactionResponse.setStatusCode(Constants.Responses.codes.INVALID_DR_ACCOUNT);
            transactionResponse.setStatusMessage(Constants.Responses.messages.INVALID_DR_ACCOUNT);
            return new ResponseEntity<>(transactionResponse, HttpStatus.BAD_REQUEST);
        }
        if (account.get().getBalance() < transactionRequest.getAmount()) {
            transactionResponse.setStatusCode(Constants.Responses.codes.INSUFFICIENT_FUNDS);
            transactionResponse.setStatusMessage(Constants.Responses.messages.INSUFFICIENT_FUNDS);
            return new ResponseEntity<>(transactionResponse, HttpStatus.BAD_REQUEST);
        }

        Optional<Accounts> accountto = accountsRepo.findByAccountNumber(transactionRequest.getAccountTo());
        if (!accountto.isPresent()) {
            transactionResponse.setStatusCode(Constants.Responses.codes.INVALID_CR_ACCOUNT);
            transactionResponse.setStatusMessage(Constants.Responses.messages.INVALID_CR_ACCOUNT);
            return new ResponseEntity<>(transactionResponse, HttpStatus.BAD_REQUEST);
        }
        //Could have done better by handling accounting entries
        account.get().setBalance(account.get().getBalance() - transactionRequest.getAmount());
        accountsRepo.save(account.get());
        accountto.get().setBalance(accountto.get().getBalance() + transactionRequest.getAmount());
        accountsRepo.save(accountto.get());
        transactionResponse.setStatusMessage(Constants.Responses.messages.SUCCESS);
        transactionResponse.setStatusCode(Constants.Responses.codes.SUCCESS);
        //Could do better by maintaining a message template
        String message = "Dear Customer, your cash transfer has been processed successfully";
        NotificationDto notificationDto = NotificationDto.builder().message(message).recipient(customers.get().getMobileNumber()).type(1).build();
        producer.sendMessage("notifications-topic", new Gson().toJson(notificationDto));
        return new ResponseEntity<>(transactionResponse, HttpStatus.OK);
    }


}