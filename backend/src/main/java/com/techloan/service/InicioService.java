package com.techloan.service;

import com.techloan.domain.Rol;
import com.techloan.security.UsuarioPrincipal;
import com.techloan.web.dto.AuthDtos.Funcion;
import com.techloan.web.dto.AuthDtos.InicioResponse;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Funciones que cada rol verá al entrar (criterio de HU-11: acceder solo a las funciones de su rol).
 * La lista sale del alcance del MVP de la Guía 13; las historias aún no construidas se marcan como no disponibles.
 */
@Service
public class InicioService {

    private static final List<Funcion> FUNCIONES_USUARIO = List.of(
            new Funcion("HU-03", "Consultar equipos", "Busca equipos por nombre o código y revisa su disponibilidad.", false),
            new Funcion("HU-12", "Solicitar préstamo", "Solicita un equipo disponible indicando período y motivo.", false),
            new Funcion("HU-17", "Mis solicitudes", "Consulta el estado de tus solicitudes.", false));

    private static final List<Funcion> FUNCIONES_ENCARGADO = List.of(
            new Funcion("HU-03", "Inventario de equipos", "Consulta y mantiene actualizado el catálogo de equipos.", false),
            new Funcion("HU-13", "Gestionar solicitudes", "Aprueba o rechaza las solicitudes pendientes.", false));

    public InicioResponse para(UsuarioPrincipal usuario) {
        List<Funcion> funciones = Rol.ENCARGADO.equals(usuario.getRol()) ? FUNCIONES_ENCARGADO : FUNCIONES_USUARIO;
        return new InicioResponse(usuario.getNombre(), usuario.getRol(), usuario.getRolNombre(), funciones);
    }
}
