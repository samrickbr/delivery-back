package br.com.inova.sigin.delivery.core.exception;

import lombok.Getter;

@Getter
public class CoreIntegrationException extends RuntimeException {

    private final int status;
    private final String body;

    public CoreIntegrationException(String message) {
        super(message);
        this.status = 500;
        this.body = null;
    }

    public CoreIntegrationException(String message, Throwable cause) {
        super(message, cause);
        this.status = 500;
        this.body = null;
    }

    public CoreIntegrationException(int status, String message) {
        super(message);
        this.status = status;
        this.body = null;
    }

    public CoreIntegrationException(
            int status,
            String message,
            Throwable cause
    ) {
        super(message, cause);
        this.status = status;
        this.body = null;
    }

    public CoreIntegrationException(
            int status,
            String message,
            String body
    ) {
        super(message);
        this.status = status;
        this.body = body;
    }
}