package com.maeng0830.stockdividend.exception.customException;

import org.springframework.http.HttpStatus;

public class FailedToScrapTickerException extends AbstractException {

    @Override
    public int getStatusCode() {
        return HttpStatus.INTERNAL_SERVER_ERROR.value();
    }

    @Override
    public String getMessage() {
        return "회사 정보 스크래핑에 실패했습니다.";
    }
}
