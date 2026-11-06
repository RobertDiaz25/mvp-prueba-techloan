package com.techloan.web;

import com.techloan.security.UsuarioPrincipal;
import com.techloan.service.ApiException;
import com.techloan.web.dto.AuthDtos.LoginRequest;
import com.techloan.web.dto.AuthDtos.UsuarioResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** HU-11: inicio de sesión, cierre de sesión y consulta de la identidad actual. */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    static final String CREDENCIALES_INVALIDAS = "Usuario o contraseña incorrectos.";

    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository contextos;

    public AuthController(AuthenticationManager authenticationManager, SecurityContextRepository contextos) {
        this.authenticationManager = authenticationManager;
        this.contextos = contextos;
    }

    @PostMapping("/login")
    public UsuarioResponse login(@Valid @RequestBody LoginRequest req, HttpServletRequest request,
                                 HttpServletResponse response) {
        Authentication auth;
        try {
            auth = authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(req.username().trim(), req.password()));
        } catch (AuthenticationException e) {
            // El mismo mensaje si el usuario no existe o si la contraseña falla: no revela qué cuentas existen.
            throw new ApiException(HttpStatus.UNAUTHORIZED, CREDENCIALES_INVALIDAS);
        }
        request.getSession(true);
        request.changeSessionId(); // un id de sesión nuevo tras autenticar evita la fijación de sesión
        SecurityContext contexto = SecurityContextHolder.createEmptyContext();
        contexto.setAuthentication(auth);
        SecurityContextHolder.setContext(contexto);
        contextos.saveContext(contexto, request, response);
        return UsuarioResponse.de((UsuarioPrincipal) auth.getPrincipal());
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(HttpServletRequest request) {
        HttpSession sesion = request.getSession(false);
        if (sesion != null) {
            sesion.invalidate();
        }
        SecurityContextHolder.clearContext();
    }

    @GetMapping("/me")
    public UsuarioResponse yo(@AuthenticationPrincipal UsuarioPrincipal usuario) {
        return UsuarioResponse.de(usuario);
    }
}
