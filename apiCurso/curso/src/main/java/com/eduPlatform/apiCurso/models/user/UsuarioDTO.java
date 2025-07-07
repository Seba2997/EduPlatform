package com.eduPlatform.apiCurso.models.user;

import java.util.Set;
import lombok.Data;

@Data
public class UsuarioDTO {
    private int id;
    private String name;
    private String email;
    private Set<RolDTO> roles;

    public boolean tieneRol(String nombreRol) {
        return roles.stream().anyMatch(r -> r.getNombre().equalsIgnoreCase(nombreRol));
    }
}