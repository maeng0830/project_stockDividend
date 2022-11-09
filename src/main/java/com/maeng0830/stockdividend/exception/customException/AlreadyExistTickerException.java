package com.maeng0830.stockdividend.exception.customException;

import org.springframework.http.HttpStatus;

public class AlreadyExistTickerException extends AbstractException {

    @Override
    public int getStatusCode() {
        return HttpStatus.SERVICE_UNAVAILABLE.value();
    }

    @Override
    public String getMessage() {
        return "이미 존재하는 회사 정보입니다.";
    }
}
