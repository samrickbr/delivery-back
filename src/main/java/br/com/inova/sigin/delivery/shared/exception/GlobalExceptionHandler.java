package br.com.inova.sigin.delivery.shared.exception;

import br.com.inova.sigin.delivery.core.exception.CoreIntegrationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CoreIntegrationException.class)
    public ResponseEntity<Map<String, Object>> handleCoreIntegrationException(
            CoreIntegrationException exception
    ) {
        int status = exception.getStatus();

        return ResponseEntity
                .status(status)
                .body(Map.of(
                        "status", status,
                        "message", exception.getMessage()
                ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(
            IllegalArgumentException exception
    ) {
        return ResponseEntity
                .badRequest()
                .body(Map.of(
                        "status", 400,
                        "error", "Bad Request",
                        "message", exception.getMessage()
                ));
    }
}
