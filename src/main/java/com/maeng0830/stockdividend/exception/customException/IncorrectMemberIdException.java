package com.maeng0830.stockdividend.exception.customException;

import org.springframework.http.HttpStatus;

public class IncorrectMemberIdException extends AbstractException {

    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }

    @Override
    public String getMessage() {
        return "존재하지 않는 회원 아이디입니다.";
    }
}
