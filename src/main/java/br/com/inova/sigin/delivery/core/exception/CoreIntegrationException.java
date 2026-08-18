package br.com.inova.sigin.delivery.core.exception;

public class CoreIntegrationException extends RuntimeException {

    public CoreIntegrationException(String message) {
        super(message);
    }

    public CoreIntegrationException(String message, Throwable cause) {
        super(message, cause);
    }
}