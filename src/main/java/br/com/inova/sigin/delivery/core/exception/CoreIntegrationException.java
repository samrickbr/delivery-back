package br.com.inova.sigin.delivery.core.exception;

import lombok.Getter;

@Getter
public class CoreIntegrationException extends RuntimeException {

    private final int status;

    public CoreIntegrationException(String message) {
        super(message);
        this.status = 500;
    }

    public CoreIntegrationException(String message, Throwable cause) {
        super(message, cause);
        this.status = 500;
    }

    public CoreIntegrationException(int status, String message) {
        super(message);
        this.status = status;
    }

    public CoreIntegrationException(int status, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
    }
}
