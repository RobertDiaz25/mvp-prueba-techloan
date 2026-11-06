package com.techloan.config;

import com.techloan.domain.Rol;
import com.techloan.domain.Usuario;
import com.techloan.repository.RolRepository;
import com.techloan.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Crea cuentas ficticias para probar HU-11. La contraseña NO está en el código: se toma de la variable
 * de entorno TECHLOAN_SEED_PASSWORD y se guarda solo como hash BCrypt. Sin esa variable no se crea nada.
 */
@Component
public class UsuariosSemilla implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(UsuariosSemilla.class);

    private final UsuarioRepository usuarios;
    private final RolRepository roles;
    private final PasswordEncoder encoder;
    private final String password;

    public UsuariosSemilla(UsuarioRepository usuarios, RolRepository roles, PasswordEncoder encoder,
                           @Value("${techloan.semilla.password:}") String password) {
        this.usuarios = usuarios;
        this.roles = roles;
        this.encoder = encoder;
        this.password = password;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (usuarios.count() > 0) {
            return;
        }
        if (password == null || password.isBlank()) {
            log.warn("No hay usuarios y TECHLOAN_SEED_PASSWORD no está definida: no se crearon cuentas de prueba.");
            return;
        }
        Rol usuario = roles.findByCodigo(Rol.USUARIO).orElseThrow();
        Rol encargado = roles.findByCodigo(Rol.ENCARGADO).orElseThrow();
        usuarios.save(new Usuario("encargado", "Karla Hernández", encoder.encode(password), encargado));
        usuarios.save(new Usuario("20220001", "Ana Martínez", encoder.encode(password), usuario));
        usuarios.save(new Usuario("20220002", "Luis Gómez", encoder.encode(password), usuario));
        log.info("Cuentas de prueba creadas: encargado, 20220001 y 20220002.");
    }
}
