package com.guner.consumer.exception;

public class MessageNotSuitableException extends RuntimeException {
    public MessageNotSuitableException() {
        super();
    }

    public MessageNotSuitableException(String message) {
        super(message);
    }

    public MessageNotSuitableException(String message, Throwable cause) {
        super(message, cause);
    }
}
