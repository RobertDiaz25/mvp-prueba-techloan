package com.techloan.security;

import com.techloan.domain.Usuario;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Identidad del usuario autenticado, guardada en la sesión del servidor.
 * Los controladores la reciben con @AuthenticationPrincipal; nunca se confía en un id enviado por el cliente.
 */
public class UsuarioPrincipal implements UserDetails, CredentialsContainer {

    private final Long id;
    private final String username;
    private final String nombre;
    private final String rol;
    private final String rolNombre;
    private String passwordHash;

    public UsuarioPrincipal(Usuario usuario) {
        this.id = usuario.getId();
        this.username = usuario.getUsername();
        this.nombre = usuario.getNombre();
        this.rol = usuario.getRol().getCodigo();
        this.rolNombre = usuario.getRol().getNombre();
        this.passwordHash = usuario.getPasswordHash();
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getRol() { return rol; }
    public String getRolNombre() { return rolNombre; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + rol));
    }

    @Override
    public String getPassword() { return passwordHash; }

    @Override
    public String getUsername() { return username; }

    /** Spring Security llama a este método después de autenticar: el hash no queda en la sesión. */
    @Override
    public void eraseCredentials() { this.passwordHash = null; }
}
