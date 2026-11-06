package com.techloan.web;

import com.techloan.service.ApiException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Las respuestas de error tienen la forma {"mensaje": "...", "campos": {"campo": "..."}}. */
@RestControllerAdvice
public class ApiExceptionHandler {

    public record ApiError(String mensaje, Map<String, String> campos) {
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiError> negocio(ApiException e) {
        return ResponseEntity.status(e.getEstado()).body(new ApiError(e.getMessage(), e.getCampos()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> validacion(MethodArgumentNotValidException e) {
        Map<String, String> campos = new LinkedHashMap<>();
        e.getBindingResult().getFieldErrors()
                .forEach(f -> campos.putIfAbsent(f.getField(), f.getDefaultMessage()));
        return ResponseEntity.badRequest().body(new ApiError("Revisa los datos marcados.", campos));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> formato(HttpMessageNotReadableException e) {
        return ResponseEntity.badRequest().body(new ApiError("La solicitud no tiene un formato válido.", Map.of()));
    }
}
