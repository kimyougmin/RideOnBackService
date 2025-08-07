package com.ll.rideon.global.exception;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class ErrorResponse {

    private int status;
    private String message;
    private Map<String, String> errors;
} 