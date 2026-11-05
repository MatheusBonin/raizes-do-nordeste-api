package com.raizes.nordeste_api.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @SuppressWarnings({"rawtypes", "unchecked"})
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity handleResponseStatusException(
            ResponseStatusException ex, HttpServletRequest request) {
        
        Map body = new LinkedHashMap();
        body.put("error", ex.getStatusCode().toString());
        body.put("message", ex.getReason());
        body.put("details", Collections.emptyList());
        body.put("timestamp", Instant.now().toString());
        body.put("path", request.getRequestURI());

        return ResponseEntity.status(ex.getStatusCode()).body(body);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity handleValidationException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        
        List details = new ArrayList();
        ex.getBindingResult().getFieldErrors().forEach(err -> {
            Map detail = new LinkedHashMap();
            detail.put("field", err.getField());
            detail.put("issue", err.getDefaultMessage());
            details.add(detail);
        });

        Map body = new LinkedHashMap();
        body.put("error", "ERRO_VALIDACAO");
        body.put("message", "Um ou mais campos estão inválidos.");
        body.put("details", details);
        body.put("timestamp", Instant.now().toString());
        body.put("path", request.getRequestURI());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
}