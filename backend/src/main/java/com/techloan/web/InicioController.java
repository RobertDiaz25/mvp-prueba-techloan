package com.techloan.web;

import com.techloan.security.UsuarioPrincipal;
import com.techloan.service.InicioService;
import com.techloan.web.dto.AuthDtos.InicioResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Inicio de cada rol. /api/usuario/** solo admite el rol USUARIO y /api/encargado/** solo ENCARGADO
 * (ver SecurityConfig). Sirve para comprobar CP01 y CP03.
 */
@RestController
public class InicioController {

    private final InicioService inicio;

    public InicioController(InicioService inicio) {
        this.inicio = inicio;
    }

    @GetMapping("/api/usuario/inicio")
    public InicioResponse inicioUsuario(@AuthenticationPrincipal UsuarioPrincipal usuario) {
        return inicio.para(usuario);
    }

    @GetMapping("/api/encargado/inicio")
    public InicioResponse inicioEncargado(@AuthenticationPrincipal UsuarioPrincipal usuario) {
        return inicio.para(usuario);
    }
}
