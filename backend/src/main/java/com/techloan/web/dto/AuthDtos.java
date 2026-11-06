package com.techloan.web.dto;

import com.techloan.security.UsuarioPrincipal;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

/** Contratos HTTP/JSON de HU-11. Ninguna respuesta incluye la contraseña ni su hash. */
public final class AuthDtos {

    private AuthDtos() {
    }

    public record LoginRequest(
            @NotBlank(message = "Escribe tu usuario o carné.") @Size(max = 40, message = "El usuario admite hasta 40 caracteres.") String username,
            @NotBlank(message = "Escribe tu contraseña.") @Size(max = 72, message = "La contraseña admite hasta 72 caracteres.") String password) {
    }

    public record UsuarioResponse(Long id, String username, String nombre, String rol, String rolNombre) {
        public static UsuarioResponse de(UsuarioPrincipal p) {
            return new UsuarioResponse(p.getId(), p.getUsername(), p.getNombre(), p.getRol(), p.getRolNombre());
        }
    }

    /** Pantalla de inicio de cada rol: quién entró y qué funciones del MVP le corresponden. */
    public record InicioResponse(String nombre, String rol, String rolNombre, List<Funcion> funciones) {
    }

    /** Función del MVP asignada al rol. disponible=false: se construye en semanas posteriores. */
    public record Funcion(String historia, String nombre, String descripcion, boolean disponible) {
    }
}
