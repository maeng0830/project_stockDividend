package com.maeng0830.stockdividend.exception.customException;

import org.springframework.http.HttpStatus;

public class EmptyTickerException extends AbstractException {

    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }

    @Override
    public String getMessage() {
        return "티커 정보가 존재하지 않습니다.";
    }
}
