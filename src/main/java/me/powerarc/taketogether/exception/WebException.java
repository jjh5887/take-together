package me.powerarc.taketogether.exception;

import lombok.Data;

@Data
public class WebException extends RuntimeException {
    private int status;

    public WebException(int status, String message) {
        super(message);
        this.status = status;
    }
}
