package com.trans.service.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@RequiredArgsConstructor
public class TransactionResponse {
    private String statusCode;
    private String statusMessage;
}
