package com.eduPlatform.apiCurso.models.user;

import java.util.List;
import lombok.Data;

@Data
public class UsuarioDTO {
    private int id;
    private String name;
    private String email;
    private List<String> roles;

    public boolean tieneRol(String nombre) {
    return roles != null && roles.contains(nombre);
}
}