package me.seho.authbeproject2.service.exceptions;

import lombok.Getter;

@Getter
public class BadRequestException extends RuntimeException{
    private final String detailMessage;
    private final Object request;

    public BadRequestException(String detailMessage, Object request) {
        this.detailMessage = detailMessage;
        this.request = request;
    }
}
