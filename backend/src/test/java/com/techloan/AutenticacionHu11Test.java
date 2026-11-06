package com.techloan;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techloan.repository.UsuarioRepository;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

/**
 * Casos de prueba de HU-11 (CP01–CP04 de la Guía 13 y casos negativos adicionales).
 * Corren contra la API real con H2 en memoria y las mismas migraciones Flyway que PostgreSQL.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AutenticacionHu11Test {

    private static final String USUARIO = "20220001";
    private static final String ENCARGADO = "encargado";

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper json;
    @Autowired private UsuarioRepository usuarios;
    @Value("${techloan.semilla.password}") private String password;

    @Test
    @DisplayName("CP01: un Usuario inicia sesión y accede a su inicio con las funciones de su rol")
    void cp01_loginUsuario() throws Exception {
        MockHttpSession sesion = login(USUARIO);
        mvc.perform(get("/api/auth/me").session(sesion))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(USUARIO))
                .andExpect(jsonPath("$.rol").value("USUARIO"));
        mvc.perform(get("/api/usuario/inicio").session(sesion))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rol").value("USUARIO"))
                .andExpect(jsonPath("$.funciones[0].historia").value("HU-03"));
    }

    @Test
    @DisplayName("CP01b: el Encargado inicia sesión y accede a su inicio")
    void cp01b_loginEncargado() throws Exception {
        mvc.perform(get("/api/encargado/inicio").session(login(ENCARGADO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rol").value("ENCARGADO"))
                .andExpect(jsonPath("$.funciones[1].historia").value("HU-13"));
    }

    @Test
    @DisplayName("CP02: contraseña incorrecta devuelve 401 con mensaje genérico y sin sesión")
    void cp02_passwordIncorrecta() throws Exception {
        MvcResult r = mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content(credenciales(USUARIO, "incorrecta")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.mensaje").value("Usuario o contraseña incorrectos."))
                .andReturn();
        assertThat(r.getRequest().getSession(false)).isNull();
    }

    @Test
    @DisplayName("CP02b: un usuario inexistente recibe el mismo mensaje que una contraseña incorrecta")
    void cp02b_usuarioInexistente() throws Exception {
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content(credenciales("no-existe", password)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.mensaje").value("Usuario o contraseña incorrectos."));
    }

    @Test
    @DisplayName("CP03: un Usuario no accede al endpoint del Encargado (403) y viceversa")
    void cp03_permisosPorRol() throws Exception {
        mvc.perform(get("/api/encargado/inicio").session(login(USUARIO)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.mensaje").exists());
        mvc.perform(get("/api/usuario/inicio").session(login(ENCARGADO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("CP04: la base guarda solo hashes BCrypt y ninguna respuesta expone la contraseña")
    void cp04_sinContrasenasExpuestas() throws Exception {
        assertThat(usuarios.findAll()).isNotEmpty().allSatisfy(u -> {
            assertThat(u.getPasswordHash()).startsWith("$2");
            assertThat(u.getPasswordHash()).isNotEqualTo(password);
        });
        MvcResult r = mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content(credenciales(USUARIO, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.passwordHash").doesNotExist())
                .andReturn();
        assertThat(r.getResponse().getContentAsString()).doesNotContain(password).doesNotContain("$2");
    }

    @Test
    @DisplayName("Sin sesión, los endpoints protegidos responden 401")
    void sinSesion() throws Exception {
        mvc.perform(get("/api/auth/me")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/usuario/inicio")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/encargado/inicio")).andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Campos vacíos devuelven 400 indicando qué falta")
    void camposVacios() throws Exception {
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content(credenciales("  ", "")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.username").exists())
                .andExpect(jsonPath("$.campos.password").exists());
    }

    @Test
    @DisplayName("Cerrar sesión invalida la sesión")
    void logout() throws Exception {
        MockHttpSession sesion = login(USUARIO);
        mvc.perform(post("/api/auth/logout").session(sesion)).andExpect(status().isNoContent());
        assertThat(sesion.isInvalid()).isTrue();
        mvc.perform(get("/api/auth/me")).andExpect(status().isUnauthorized());
    }

    private MockHttpSession login(String username) throws Exception {
        MvcResult r = mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content(credenciales(username, password)))
                .andExpect(status().isOk())
                .andReturn();
        return (MockHttpSession) r.getRequest().getSession(false);
    }

    private String credenciales(String username, String clave) throws Exception {
        return json.writeValueAsString(Map.of("username", username, "password", clave));
    }
}
