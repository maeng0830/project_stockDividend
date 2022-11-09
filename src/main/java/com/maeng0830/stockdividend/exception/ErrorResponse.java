package com.maeng0830.stockdividend.exception;

import lombok.Builder;

@Builder
public class ErrorResponse {
    private int code;
    private String message;
}
