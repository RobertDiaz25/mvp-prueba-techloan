package com.techloan.service;

import java.util.Map;
import org.springframework.http.HttpStatus;

/** Error de negocio que la API devuelve como {"mensaje": "...", "campos": {...}}. */
public class ApiException extends RuntimeException {

    private final HttpStatus estado;
    private final Map<String, String> campos;

    public ApiException(HttpStatus estado, String mensaje) {
        this(estado, mensaje, Map.of());
    }

    public ApiException(HttpStatus estado, String mensaje, Map<String, String> campos) {
        super(mensaje);
        this.estado = estado;
        this.campos = campos;
    }

    public static ApiException campo(String campo, String mensaje) {
        return new ApiException(HttpStatus.BAD_REQUEST, mensaje, Map.of(campo, mensaje));
    }

    public static ApiException noEncontrado(String mensaje) {
        return new ApiException(HttpStatus.NOT_FOUND, mensaje);
    }

    public static ApiException conflicto(String mensaje) {
        return new ApiException(HttpStatus.CONFLICT, mensaje);
    }

    public HttpStatus getEstado() { return estado; }
    public Map<String, String> getCampos() { return campos; }
}
